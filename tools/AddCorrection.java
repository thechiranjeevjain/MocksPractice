import dev.dsareview.domain.Difficulty;
import dev.dsareview.domain.Problem;
import dev.dsareview.domain.ReviewDeck;
import dev.dsareview.infrastructure.json.JacksonReviewDeckRepository;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;

/** Adds a generic mock-correction card to a Review OS deck without rescheduling it. */
public final class AddCorrection {
    public static void main(String[] args) {
        if (args.length < 4 || args.length > 6) {
            throw new IllegalArgumentException("Usage: AddCorrection <practice-root> <id> <title> <area> [difficulty] [session-path]");
        }
        Path practice = Path.of(args[0]).toAbsolutePath().normalize();
        String id = normalizeId(args[1]);
        String title = require(args[2], "title");
        String area = require(args[3], "area");
        Difficulty difficulty = Difficulty.valueOf(args.length >= 5 ? args[4].toUpperCase(Locale.ROOT) : "MEDIUM");
        String sessionPath = args.length >= 6 ? args[5] : "";

        var repository = new JacksonReviewDeckRepository(practice.resolve("review/review.json"));
        ReviewDeck deck = repository.load();
        if (deck.findProblem(id).isEmpty()) {
            Problem problem = new Problem();
            problem.setId(id);
            problem.setTitle(title);
            problem.setContentType("mock-correction");
            problem.setPattern(area);
            problem.setDifficulty(difficulty);
            problem.setTags(List.of("mock-correction", slug(area)));
            problem.setCodePath(sessionPath);
            problem.setNotesPath(".interviewer/PROGRESS.md");
            problem.setGithubUrl("");
            problem.setPrompt("Cold-reconstruct " + title + " before opening sources or using AI. Produce Trigger, Pattern, "
                    + "Invariant, Template, Fallback, and Optimization; then perform it, defend it, and solve one changed-constraint variation.");
            problem.setAnswer("");
            problem.setSourceRefs(sessionPath.isBlank() ? List.of() : List.of(sessionPath));
            ZoneId zone = ZoneId.of(deck.getSettings().getTimeZone());
            problem.setNextReview(LocalDate.now(zone));
            deck.getProblems().add(problem);
        }
        deck.setGeneratedAt(Instant.now());
        repository.save(deck);
        System.out.println(id);
    }

    private static String normalizeId(String value) {
        String normalized = slug(require(value, "id")).toUpperCase(Locale.ROOT);
        return normalized.startsWith("MOCK-") ? normalized : "MOCK-" + normalized;
    }

    private static String slug(String value) { return value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", ""); }
    private static String require(String value, String label) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(label + " must not be blank");
        return value.trim();
    }
}
