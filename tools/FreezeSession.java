import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Stream;

/** Cross-platform freeze/verify tool for candidate session files. Requires Java 21+. */
public final class FreezeSession {
    private record Entry(String relativePath, String sha256) {}

    public static void main(String[] args) throws Exception {
        if (args.length < 2 || args.length > 3) {
            throw new IllegalArgumentException("Usage: java FreezeSession.java <practice-root> <session-path> [--verify]");
        }
        Path practice = Path.of(args[0]).toAbsolutePath().normalize();
        Path suppliedSession = Path.of(args[1]);
        Path session = (suppliedSession.isAbsolute() ? suppliedSession : practice.resolve(suppliedSession))
                .toAbsolutePath().normalize();
        if (!session.startsWith(practice) || !Files.isDirectory(session)) throw new IllegalArgumentException("Session must exist inside MocksPractice.");
        String relativeSession = practice.relativize(session).toString().replace('\\', '/');
        Path manifest = practice.resolve(".interviewer/frozen/" + relativeSession.replace('/', '-') + ".json");
        List<Entry> entries = entries(session);
        if (args.length == 3 && "--verify".equals(args[2])) {
            verify(manifest, entries);
            System.out.println("Freeze verification passed: " + entries.size() + " files");
            return;
        }
        Files.createDirectories(manifest.getParent());
        Files.writeString(manifest, render(relativeSession, entries), StandardCharsets.UTF_8);
        System.out.println("Frozen " + entries.size() + " files: " + manifest);
    }

    private static List<Entry> entries(Path session) throws Exception {
        try (Stream<Path> stream = Files.walk(session)) {
            return stream.filter(Files::isRegularFile)
                    .filter(path -> !session.relativize(path).startsWith("target"))
                    .sorted(Comparator.comparing(Path::toString))
                    .map(path -> {
                        try { return new Entry(session.relativize(path).toString().replace('\\', '/'), sha256(path)); }
                        catch (Exception exception) { throw new RuntimeException(exception); }
                    }).toList();
        }
    }

    private static void verify(Path manifest, List<Entry> entries) throws IOException {
        if (!Files.exists(manifest)) throw new IllegalStateException("Freeze manifest does not exist: " + manifest);
        String json = Files.readString(manifest, StandardCharsets.UTF_8);
        for (Entry entry : entries) {
            String expected = "\"relativePath\": \"" + escape(entry.relativePath()) + "\", \"sha256\": \"" + entry.sha256() + "\"";
            if (!json.contains(expected)) throw new IllegalStateException("Frozen file changed or is missing from manifest: " + entry.relativePath());
        }
        long manifestCount = json.lines().filter(line -> line.contains("\"relativePath\"")).count();
        if (manifestCount != entries.size()) throw new IllegalStateException("Frozen file count changed: expected " + manifestCount + " actual " + entries.size());
    }

    private static String render(String session, List<Entry> entries) {
        StringBuilder out = new StringBuilder("{\n  \"schemaVersion\": 1,\n  \"session\": \"")
                .append(escape(session)).append("\",\n  \"frozenAt\": \"").append(Instant.now()).append("\",\n  \"files\": [\n");
        for (int i = 0; i < entries.size(); i++) {
            Entry entry = entries.get(i);
            out.append("    {\"relativePath\": \"").append(escape(entry.relativePath())).append("\", \"sha256\": \"")
                    .append(entry.sha256()).append("\"}").append(i + 1 == entries.size() ? "\n" : ",\n");
        }
        return out.append("  ]\n}\n").toString();
    }

    private static String sha256(Path path) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (var input = Files.newInputStream(path)) {
            byte[] buffer = new byte[8192];
            for (int read; (read = input.read(buffer)) >= 0;) digest.update(buffer, 0, read);
        }
        return HexFormat.of().formatHex(digest.digest());
    }

    private static String escape(String value) { return value.replace("\\", "\\\\").replace("\"", "\\\""); }
}
