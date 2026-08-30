import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/** Maven-free, cross-platform repository snapshot tool. Requires Java 21+. */
public final class RepoSnapshot {
    private record RepositoryConfig(String name, String environmentVariable, String relativePath, String fallback,
                                    boolean required, boolean profileScan, Set<String> extensions) {}
    private record FileRecord(String repository, String relativePath, long length, Instant modified, String sha256) {}
    private record RepositorySummary(String name, String root, boolean available, Map<String, Integer> extensions, int count) {}

    public static void main(String[] args) throws Exception {
        if (args.length != 1) throw new IllegalArgumentException("Usage: java RepoSnapshot.java <practice-root>");
        Path practiceRoot = Path.of(args[0]).toAbsolutePath().normalize();
        Path configPath = practiceRoot.resolve("config/repositories.json");
        String config = Files.readString(configPath, StandardCharsets.UTF_8);
        List<RepositoryConfig> repositories = parseRepositories(config);
        Set<String> extensions = parseArray(config, "defaultExtensions");
        Set<String> excluded = parseArray(config, "excludedDirectoryNames");

        Path snapshotRoot = practiceRoot.resolve(".interviewer/snapshots");
        Path snapshotPath = snapshotRoot.resolve("repo-snapshot.json");
        Map<String, String> oldHashes = Files.exists(snapshotPath)
                ? parsePreviousHashes(Files.readString(snapshotPath, StandardCharsets.UTF_8)) : Map.of();

        List<FileRecord> records = new ArrayList<>();
        List<RepositorySummary> summaries = new ArrayList<>();
        for (RepositoryConfig repository : repositories) {
            if (!repository.profileScan()) continue;
            Path root = resolveRoot(practiceRoot, repository);
            if (root == null) {
                summaries.add(new RepositorySummary(repository.name(), "UNAVAILABLE", false, Map.of(), 0));
                continue;
            }
            Set<String> effectiveExtensions = repository.extensions().isEmpty() ? extensions : repository.extensions();
            Map<String, Integer> counts = new TreeMap<>();
            try (Stream<Path> paths = Files.walk(root)) {
                paths.filter(Files::isRegularFile)
                        .filter(path -> included(root, path, effectiveExtensions, excluded))
                        .forEach(path -> {
                            try {
                                String relative = root.relativize(path).toString().replace('\\', '/');
                                String extension = extension(path.getFileName().toString());
                                counts.merge(extension, 1, Integer::sum);
                                records.add(new FileRecord(repository.name(), relative, Files.size(path),
                                        Files.getLastModifiedTime(path).toInstant(), sha256(path)));
                            } catch (IOException exception) {
                                throw new RuntimeException("Could not inspect " + path, exception);
                            }
                        });
            }
            summaries.add(new RepositorySummary(repository.name(), root.toString(), true, counts,
                    counts.values().stream().mapToInt(Integer::intValue).sum()));
        }
        records.sort(Comparator.comparing(FileRecord::repository).thenComparing(FileRecord::relativePath));

        Map<String, String> newHashes = new LinkedHashMap<>();
        for (FileRecord record : records) newHashes.put(key(record.repository(), record.relativePath()), record.sha256());
        List<String> added = newHashes.keySet().stream().filter(key -> !oldHashes.containsKey(key)).sorted().toList();
        List<String> removed = oldHashes.keySet().stream().filter(key -> !newHashes.containsKey(key)).sorted().toList();
        List<String> modified = newHashes.keySet().stream()
                .filter(key -> oldHashes.containsKey(key) && !oldHashes.get(key).equals(newHashes.get(key))).sorted().toList();

        Files.createDirectories(snapshotRoot);
        String scannedAt = Instant.now().toString();
        atomicWrite(snapshotPath, renderJson(scannedAt, summaries, records));
        Files.writeString(snapshotRoot.resolve("REPO_CHANGES.md"),
                renderChanges(scannedAt, Files.exists(snapshotPath), oldHashes.isEmpty(), added, modified, removed), StandardCharsets.UTF_8);
        Files.writeString(snapshotRoot.resolve("REPO_INVENTORY.md"),
                renderInventory(scannedAt, summaries), StandardCharsets.UTF_8);

        System.out.println("Snapshot: " + snapshotPath);
        System.out.println("Included files: " + records.size());
        System.out.printf("Changes: +%d ~%d -%d%n", added.size(), modified.size(), removed.size());
    }

    private static Path resolveRoot(Path practiceRoot, RepositoryConfig repository) {
        List<String> candidates = new ArrayList<>();
        String environment = System.getenv(repository.environmentVariable());
        if (environment != null && !environment.isBlank()) candidates.add(environment);
        candidates.add(practiceRoot.getParent().resolve(repository.relativePath()).toString());
        if (repository.fallback() != null && !repository.fallback().isBlank()) candidates.add(repository.fallback());
        Path found = candidates.stream().map(Path::of).map(path -> path.toAbsolutePath().normalize())
                .filter(Files::isDirectory).findFirst().orElse(null);
        if (found == null && repository.required()) {
            throw new IllegalStateException("Repository " + repository.name()
                    + " was not found. Keep it beside MocksPractice or set " + repository.environmentVariable() + ".");
        }
        return found;
    }

    private static boolean included(Path root, Path path, Set<String> extensions, Set<String> excluded) {
        String ext = extension(path.getFileName().toString());
        if (!extensions.contains(ext)) return false;
        for (Path segment : root.relativize(path)) {
            if (excluded.contains(segment.toString().toLowerCase(Locale.ROOT))) return false;
        }
        return true;
    }

    private static String extension(String name) {
        int index = name.lastIndexOf('.');
        return index < 0 ? "[none]" : name.substring(index).toLowerCase(Locale.ROOT);
    }

    private static String sha256(Path path) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (var input = Files.newInputStream(path)) {
                byte[] buffer = new byte[8192];
                for (int read; (read = input.read(buffer)) >= 0;) digest.update(buffer, 0, read);
            }
            return java.util.HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException impossible) {
            throw new IllegalStateException(impossible);
        }
    }

    private static List<RepositoryConfig> parseRepositories(String json) {
        int start = json.indexOf("\"repositories\"");
        int arrayStart = json.indexOf('[', start);
        int arrayEnd = json.indexOf(']', arrayStart);
        String block = json.substring(arrayStart + 1, arrayEnd);
        Matcher objects = Pattern.compile("\\{([^{}]+)}", Pattern.DOTALL).matcher(block);
        List<RepositoryConfig> result = new ArrayList<>();
        while (objects.find()) {
            String object = objects.group(1);
            result.add(new RepositoryConfig(field(object, "name"), field(object, "environmentVariable"),
                    field(object, "relativeToPracticeParent"), optionalField(object, "windowsFallback"),
                    optionalBoolean(object, "required", true), optionalBoolean(object, "profileScan", true),
                    parseCsv(optionalField(object, "extensionsCsv"))));
        }
        if (result.isEmpty()) throw new IllegalArgumentException("No repositories in config.");
        return result;
    }

    private static Set<String> parseArray(String json, String name) {
        Matcher array = Pattern.compile("\\\"" + Pattern.quote(name) + "\\\"\\s*:\\s*\\[(.*?)]", Pattern.DOTALL).matcher(json);
        if (!array.find()) throw new IllegalArgumentException("Missing config array: " + name);
        Set<String> result = new HashSet<>();
        Matcher values = Pattern.compile("\"((?:\\\\.|[^\"])*)\"").matcher(array.group(1));
        while (values.find()) result.add(unescape(values.group(1)).toLowerCase(Locale.ROOT));
        return result;
    }

    private static Set<String> parseCsv(String value) {
        if (value == null || value.isBlank()) return Set.of();
        Set<String> result = new HashSet<>();
        for (String entry : value.split(",")) {
            if (!entry.isBlank()) result.add(entry.trim().toLowerCase(Locale.ROOT));
        }
        return result;
    }

    private static boolean optionalBoolean(String object, String name, boolean fallback) {
        Matcher matcher = Pattern.compile("\\\"" + Pattern.quote(name) + "\\\"\\s*:\\s*(true|false)", Pattern.CASE_INSENSITIVE).matcher(object);
        return matcher.find() ? Boolean.parseBoolean(matcher.group(1)) : fallback;
    }

    private static String field(String object, String name) {
        String value = optionalField(object, name);
        if (value == null) throw new IllegalArgumentException("Missing repository field: " + name);
        return value;
    }

    private static String optionalField(String object, String name) {
        Matcher matcher = Pattern.compile("\\\"" + Pattern.quote(name) + "\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\"])*)\\\"").matcher(object);
        return matcher.find() ? unescape(matcher.group(1)) : null;
    }

    private static Map<String, String> parsePreviousHashes(String json) {
        Pattern record = Pattern.compile("\\\"repository\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\"])*)\\\".*?"
                + "\\\"relativePath\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\"])*)\\\".*?"
                + "\\\"sha256\\\"\\s*:\\s*\\\"([a-fA-F0-9]{64})\\\"", Pattern.DOTALL);
        Matcher matcher = record.matcher(json);
        Map<String, String> result = new HashMap<>();
        while (matcher.find()) result.put(key(unescape(matcher.group(1)), unescape(matcher.group(2))), matcher.group(3).toLowerCase(Locale.ROOT));
        return result;
    }

    private static String renderJson(String scannedAt, List<RepositorySummary> summaries, List<FileRecord> records) {
        StringBuilder out = new StringBuilder("{\n  \"schemaVersion\": 1,\n  \"scannedAt\": \"").append(scannedAt).append("\",\n  \"repositories\": [\n");
        for (int i = 0; i < summaries.size(); i++) {
            RepositorySummary summary = summaries.get(i);
            out.append("    {\"name\": \"").append(escape(summary.name())).append("\", \"root\": \"")
                    .append(escape(summary.root())).append("\", \"available\": ").append(summary.available())
                    .append(", \"fileCount\": ").append(summary.count()).append("}");
            out.append(i + 1 == summaries.size() ? "\n" : ",\n");
        }
        out.append("  ],\n  \"files\": [\n");
        for (int i = 0; i < records.size(); i++) {
            FileRecord record = records.get(i);
            out.append("    {\"repository\": \"").append(escape(record.repository())).append("\", \"relativePath\": \"")
                    .append(escape(record.relativePath())).append("\", \"length\": ").append(record.length())
                    .append(", \"lastWriteUtc\": \"").append(record.modified()).append("\", \"sha256\": \"")
                    .append(record.sha256()).append("\"}");
            out.append(i + 1 == records.size() ? "\n" : ",\n");
        }
        return out.append("  ]\n}\n").toString();
    }

    private static String renderChanges(String scannedAt, boolean snapshotExists, boolean oldEmpty,
            List<String> added, List<String> modified, List<String> removed) {
        StringBuilder out = new StringBuilder("# Repository Changes\n\nScan: ").append(scannedAt).append("\n\n");
        if (!snapshotExists || oldEmpty) return out.append("Initial baseline created. Repository contents establish exposure only.\n").toString();
        out.append("- Added: ").append(added.size()).append("\n- Modified: ").append(modified.size())
                .append("\n- Removed: ").append(removed.size()).append("\n");
        appendSection(out, "Added", added); appendSection(out, "Modified", modified); appendSection(out, "Removed", removed);
        return out.toString();
    }

    private static void appendSection(StringBuilder out, String title, List<String> values) {
        out.append("\n## ").append(title).append("\n");
        if (values.isEmpty()) out.append("- None\n");
        else {
            values.stream().limit(250).forEach(value -> out.append("- ").append(value).append("\n"));
            if (values.size() > 250) out.append("- ... ").append(values.size() - 250).append(" more in snapshot JSON\n");
        }
    }

    private static String renderInventory(String scannedAt, List<RepositorySummary> summaries) {
        StringBuilder out = new StringBuilder("# Repository Inventory\n\nScan: ").append(scannedAt).append("\n");
        for (RepositorySummary summary : summaries) {
            out.append("\n## ").append(summary.name()).append("\n\n- Root: ").append(summary.root())
                    .append("\n- Available: ").append(summary.available())
                    .append("\n- Included source files: ").append(summary.count()).append("\n- Extensions:\n");
            summary.extensions().forEach((extension, count) -> out.append("  - ").append(extension).append(": ").append(count).append("\n"));
        }
        return out.toString();
    }

    private static void atomicWrite(Path target, String content) throws IOException {
        Path temp = target.resolveSibling(target.getFileName() + ".tmp");
        Files.writeString(temp, content, StandardCharsets.UTF_8);
        try { Files.move(temp, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING); }
        catch (AtomicMoveNotSupportedException exception) { Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING); }
    }

    private static String key(String repository, String path) { return repository + "|" + path; }
    private static String escape(String value) { return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n"); }
    private static String unescape(String value) { return value.replace("\\/", "/").replace("\\\\", "\\").replace("\\\"", "\""); }
}
