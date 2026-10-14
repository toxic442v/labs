import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Lab10 {

    interface Body { void go() throws Exception; }
    record Exercise(String id, String title, boolean interactive, Body body) {}

    static String[] extra = {};   
    
    static class Ex10_1 {
        static class SaveLines {
            static void run() throws Exception {
                Scanner in = new Scanner(System.in);
                List<String> lines = new ArrayList<>();
                for (int i = 1; i <= 5; i++) {
                    System.out.print("Line " + i + ": ");
                    lines.add(in.nextLine());
                }

                Path file = Path.of("notes.txt");
                try {
                    Files.write(file, lines);
                    System.out.println("--- read back from " + file + " ---");
                    for (String line : Files.readAllLines(file)) System.out.println(line);
                } catch (IOException e) {
                    System.out.println("File problem: " + e.getMessage());
                }
            }
        }
    }

    // ===== Exercise 10.2: Predicate and removeIf =====
    static class Ex10_2 {
        static class RemoveEvens {
            static void run() throws Exception {
                List<Integer> nums = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8));  
                Predicate<Integer> isEven = n -> n % 2 == 0;
                nums.removeIf(isEven);
                System.out.println(nums);
            }
        }
    }

    // ===== Exercise 10.3: Count lines, words, characters of sample.txt (or a path you give) =====
    static class Ex10_3 {
        static class TextStats {
            static void run() throws Exception {
                Path file = Path.of(extra.length > 0 ? extra[0] : "sample.txt");
                try {
                    String content = Files.readString(file);
                    long lines = content.lines().count();
                    long words = Arrays.stream(content.trim().split("\\s+"))
                                       .filter(w -> !w.isEmpty())
                                       .count();
                    int chars = content.length();
                    System.out.println("Lines: " + lines + ", words: " + words + ", characters: " + chars);
                } catch (IOException e) {
                    System.out.println("Cannot read " + file + ": " + e.getMessage());
                }
            }
        }
    }

    // ===== Exercise 10.4: Names above the class average =====
    static class Ex10_4 {
        record Pupil(String name, double mark) {}

        static class AboveAverage {
            static void run() throws Exception {
                List<Pupil> pupils = List.of(
                    new Pupil("Kuda", 65), new Pupil("Ada", 88), new Pupil("Lynn", 72),
                    new Pupil("Ben", 55),  new Pupil("Cara", 91));

                double avg = pupils.stream().mapToDouble(Pupil::mark).average().orElse(0);
                System.out.println("Average: " + avg);

                pupils.stream()
                      .filter(p -> p.mark() > avg)
                      .map(Pupil::name)
                      .sorted()
                      .forEach(System.out::println);
            }
        }
    }

    // ===== Exercise 10.5: Group learners by grade band, one file per band =====
    static class Ex10_5 {
        record Learner(String name, int roll, double gpa) {
            String toCsv() { return name + "," + roll + "," + gpa; }

            static Learner fromCsv(String line) {
                String[] p = line.split(",");
                return new Learner(p[0], Integer.parseInt(p[1]), Double.parseDouble(p[2]));
            }
        }

        static class StudentRepository {
            private final Path file;

            StudentRepository(String fileName) { this.file = Path.of(fileName); }

            void saveAll(List<Learner> learners) throws IOException {
                List<String> lines = new ArrayList<>();
                for (Learner l : learners) lines.add(l.toCsv());
                Files.write(file, lines);
            }

            List<Learner> loadAll() throws IOException {
                List<Learner> result = new ArrayList<>();
                if (!Files.exists(file)) return result;
                for (String line : Files.readAllLines(file)) {
                    if (!line.isBlank()) result.add(Learner.fromCsv(line));
                }
                return result;
            }
        }

        static class GradeBands {
            static String band(Learner l) {
                if (l.gpa() >= 3.5) return "A";
                if (l.gpa() >= 3.0) return "B";
                return "C";
            }

            static void run() throws Exception {
                StudentRepository repo = new StudentRepository("learners.csv");
                if (!Files.exists(Path.of("learners.csv"))) {
                    repo.saveAll(List.of(new Learner("Ada", 1, 3.9), new Learner("Kuda", 2, 3.2),
                                         new Learner("Rudo", 3, 2.8), new Learner("Ben", 4, 3.6)));
                    System.out.println("Created sample learners.csv");
                }
                List<Learner> learners = repo.loadAll();

                Map<String, List<Learner>> byBand = learners.stream()
                    .collect(Collectors.groupingBy(GradeBands::band, TreeMap::new, Collectors.toList()));

                for (Map.Entry<String, List<Learner>> e : byBand.entrySet()) {
                    List<String> lines = new ArrayList<>();
                    lines.add("Band " + e.getKey() + " report (" + e.getValue().size() + " learners)");
                    e.getValue().stream()
                        .sorted(Comparator.comparing(Learner::name))
                        .forEach(l -> lines.add(l.name() + "," + l.gpa()));

                    Path out = Path.of("band-" + e.getKey() + ".txt");
                    Files.write(out, lines);
                    System.out.println("Wrote " + out + " with " + e.getValue().size() + " learner(s)");
                }
            }
        }
    }

    static final List<Exercise> ALL = List.of(
        new Exercise("10.1", "Save five typed lines, read them back (needs keyboard input)", true, Ex10_1.SaveLines::run),
        new Exercise("10.2", "Predicate and removeIf", false, Ex10_2.RemoveEvens::run),
        new Exercise("10.3", "Count lines, words, characters of sample.txt (or a path you give)", false, Ex10_3.TextStats::run),
        new Exercise("10.4", "Names above the class average", false, Ex10_4.AboveAverage::run),
        new Exercise("10.5", "Group learners by grade band, one file per band", false, Ex10_5.GradeBands::run));

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equals("list")) {
            for (Exercise e : ALL) System.out.println(e.id() + "  " + e.title());
            return;
        }
        if (args.length > 0) {
            extra = java.util.Arrays.copyOfRange(args, 1, args.length);
            for (Exercise e : ALL) {
                if (e.id().equalsIgnoreCase(args[0])) { e.body().go(); return; }
            }
            System.out.println("No exercise " + args[0] + ". Try: java Lab10 list");
            return;
        }
        for (Exercise e : ALL) {
            if (e.interactive()) {
                System.out.println("=== Exercise " + e.id() + " skipped (keyboard input): run  java Lab10 " + e.id());
                continue;
            }
            System.out.println("=== Exercise " + e.id() + ": " + e.title() + " ===");
            e.body().go();
            System.out.println();
        }
    }
}
