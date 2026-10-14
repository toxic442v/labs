import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.Scanner;

final class Text {
    private Text() { }

    static String csvSafe(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        if (value.contains(",")) {
            throw new IllegalArgumentException(field + " must not contain a comma");
        }
        return value.trim();
    }
}

interface Borrowable {
    void borrow(Member member) throws ItemNotAvailableException;
    void giveBack();
}

class ItemNotAvailableException extends Exception {
    public ItemNotAvailableException(String message) { super(message); }
}

class MemberLimitExceededException extends Exception {
    public MemberLimitExceededException(String message) { super(message); }
}

class Member {
    private final String id;
    private final String name;

    public Member(String id, String name) {
        this.id = Text.csvSafe(id, "Member id");
        this.name = Text.csvSafe(name, "Member name");
    }

    public String getId()   { return id; }
    public String getName() { return name; }
    String toCsv()          { return id + "," + name; }

    @Override public String toString() { return id + " " + name; }
}

record Loan(String itemId, String memberId, LocalDate dueDate) {
    String toCsv() { return itemId + "," + memberId + "," + dueDate; }

    static Loan fromCsv(String line) {
        String[] p = line.split(",");
        return new Loan(p[0], p[1], LocalDate.parse(p[2]));
    }
}

abstract class LibraryItem implements Borrowable {
    private final String id;
    private final String title;
    private boolean available = true;
    private Member borrower;

    protected LibraryItem(String id, String title) {
        this.id = Text.csvSafe(id, "Item id");
        this.title = Text.csvSafe(title, "Title");
    }

    public abstract int loanPeriodDays();       
    abstract String toCsv();                    

    @Override
    public void borrow(Member member) throws ItemNotAvailableException {
        if (!available) throw new ItemNotAvailableException(title + " is already on loan");
        available = false;                      
        borrower = member;
    }

    @Override
    public void giveBack() { available = true; borrower = null; }

    public String getId()        { return id; }
    public String getTitle()     { return title; }
    public boolean isAvailable() { return available; }

    @Override public String toString() {
        return id + " " + title + (available ? "" : " (on loan to " + borrower.getName() + ")");
    }
}

class Book extends LibraryItem {
    private final String author;

    public Book(String id, String title, String author) {
        super(id, title);
        this.author = Text.csvSafe(author, "Author");
    }

    @Override public int loanPeriodDays() { return 21; }
    @Override String toCsv() { return "BOOK," + getId() + "," + getTitle() + "," + author; }
    @Override public String toString() { return "[Book] " + super.toString() + " by " + author; }
}

class Dvd extends LibraryItem {
    private final int minutes;

    public Dvd(String id, String title, int minutes) {
        super(id, title);
        if (minutes <= 0) throw new IllegalArgumentException("Running time must be positive");
        this.minutes = minutes;
    }

    @Override public int loanPeriodDays() { return 7; }
    @Override String toCsv() { return "DVD," + getId() + "," + getTitle() + "," + minutes; }
    @Override public String toString() { return "[DVD] " + super.toString() + " (" + minutes + " min)"; }
}

class Library {
    static final int MAX_LOANS = 3;

    private final Map<String, LibraryItem> items = new HashMap<>();
    private final Map<String, Member> members = new HashMap<>();
    private final Map<String, Loan> loans = new HashMap<>();     

    public void addItem(LibraryItem item) {
        if (items.putIfAbsent(item.getId(), item) != null) {
            throw new IllegalArgumentException("Duplicate item id " + item.getId());
        }
    }

    public void registerMember(Member member) {
        if (members.putIfAbsent(member.getId(), member) != null) {
            throw new IllegalArgumentException("Duplicate member id " + member.getId());
        }
    }

    public long activeLoans(String memberId) {
        return loans.values().stream().filter(l -> l.memberId().equals(memberId)).count();
    }

    public Loan borrow(String memberId, String itemId)
            throws ItemNotAvailableException, MemberLimitExceededException {
        Member member = members.get(memberId);
        if (member == null) throw new IllegalArgumentException("Unknown member " + memberId);
        LibraryItem item = items.get(itemId);
        if (item == null) throw new IllegalArgumentException("Unknown item " + itemId);

        long current = activeLoans(memberId);
        if (current >= MAX_LOANS) {
            throw new MemberLimitExceededException(
                member.getName() + " already has " + current + " loans (maximum " + MAX_LOANS + ")");
        }
        item.borrow(member);                  

        Loan loan = new Loan(itemId, memberId, LocalDate.now().plusDays(item.loanPeriodDays()));
        loans.put(itemId, loan);
        return loan;
    }

    public void giveBack(String itemId) {
        Loan loan = loans.remove(itemId);
        if (loan == null) throw new IllegalArgumentException("Item " + itemId + " is not on loan");
        items.get(itemId).giveBack();
    }

    public List<Loan> overdue(LocalDate today) {
        return loans.values().stream()
            .filter(l -> l.dueDate().isBefore(today))
            .sorted(Comparator.comparing(Loan::dueDate))
            .toList();
    }

    public List<LibraryItem> searchByTitle(String text) {
        return items.values().stream()
            .filter(i -> i.getTitle().toLowerCase().contains(text.toLowerCase()))
            .sorted(Comparator.comparing(LibraryItem::getTitle))
            .toList();
    }

    public void save(Path dir) throws IOException {
        Files.createDirectories(dir);
        Files.write(dir.resolve("items.csv"), items.values().stream()
            .sorted(Comparator.comparing(LibraryItem::getId)).map(LibraryItem::toCsv).toList());
        Files.write(dir.resolve("members.csv"), members.values().stream()
            .sorted(Comparator.comparing(Member::getId)).map(Member::toCsv).toList());
        Files.write(dir.resolve("loans.csv"), loans.values().stream().map(Loan::toCsv).toList());
    }

    public static Library load(Path dir) throws IOException {
        Library lib = new Library();

        Path itemFile = dir.resolve("items.csv");
        if (Files.exists(itemFile)) {
            for (String line : Files.readAllLines(itemFile)) {
                if (line.isBlank()) continue;
                String[] p = line.split(",");
                lib.addItem(switch (p[0]) {
                    case "BOOK" -> new Book(p[1], p[2], p[3]);
                    case "DVD"  -> new Dvd(p[1], p[2], Integer.parseInt(p[3]));
                    default -> throw new IOException("Unknown item type " + p[0]);
                });
            }
        }

        Path memberFile = dir.resolve("members.csv");
        if (Files.exists(memberFile)) {
            for (String line : Files.readAllLines(memberFile)) {
                if (line.isBlank()) continue;
                String[] p = line.split(",");
                lib.registerMember(new Member(p[0], p[1]));
            }
        }

        Path loanFile = dir.resolve("loans.csv");
        if (Files.exists(loanFile)) {
            for (String line : Files.readAllLines(loanFile)) {
                if (line.isBlank()) continue;
                Loan loan = Loan.fromCsv(line);
                LibraryItem item = lib.items.get(loan.itemId());
                Member member = lib.members.get(loan.memberId());
                if (item == null || member == null) throw new IOException("Loan refers to unknown data: " + line);
                try {
                    item.borrow(member);
                } catch (ItemNotAvailableException e) {
                    throw new IOException("Corrupt loan data: " + e.getMessage(), e);
                }
                lib.loans.put(loan.itemId(), loan);    
            }
        }
        return lib;
    }
}

class LibraryTest {
    static int failures = 0;

    static void check(String name, boolean ok) {
        System.out.println((ok ? "PASS " : "FAIL ") + name);
        if (!ok) failures++;
    }

    static void run() throws Exception {
        Library lib = new Library();
        lib.addItem(new Book("B1", "Clean Code", "Robert Martin"));
        lib.addItem(new Book("B2", "Effective Java", "Joshua Bloch"));
        lib.addItem(new Book("B3", "Java Concurrency", "Brian Goetz"));
        lib.addItem(new Dvd("D1", "The Matrix", 136));
        lib.registerMember(new Member("M1", "Tariro"));
        lib.registerMember(new Member("M2", "Farai"));

        lib.borrow("M1", "B1");
        lib.borrow("M1", "B2");
        lib.borrow("M1", "D1");
        check("3 loans allowed", lib.activeLoans("M1") == 3);

        try { lib.borrow("M1", "B3"); check("limit enforced", false); }
        catch (MemberLimitExceededException e) { check("limit enforced", true); }

        try { lib.borrow("M2", "B1"); check("unavailable item rejected", false); }
        catch (ItemNotAvailableException e) { check("unavailable item rejected", true); }

        lib.giveBack("B1");
        lib.borrow("M2", "B1");
        check("returned item can be borrowed again", lib.activeLoans("M2") == 1);
        check("search finds titles", lib.searchByTitle("java").size() == 2);
        check("nothing overdue today", lib.overdue(LocalDate.now()).isEmpty());
        check("all loans overdue after 60 days", lib.overdue(LocalDate.now().plusDays(60)).size() == 3);

        Path dir = Files.createTempDirectory("lib-test");
        lib.save(dir);
        Library copy = Library.load(dir);
        check("loans survive save and load", copy.activeLoans("M1") == 2 && copy.activeLoans("M2") == 1);
        try { copy.borrow("M2", "B2"); check("loaded item still on loan", false); }
        catch (ItemNotAvailableException e) { check("loaded item still on loan", true); }

        System.out.println(failures == 0 ? "All tests passed" : failures + " test(s) failed");
    }
}

public class LibraryApp {
    private static String ask(Scanner in, String prompt) {
        System.out.print(prompt + ": ");
        return in.nextLine().trim();
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equals("test")) {
            LibraryTest.run();
            return;
        }
        Path dir = Path.of("library-data");
        Library lib = Library.load(dir);
        Scanner in = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n1) Add item   2) Register member   3) Borrow   4) Return");
            System.out.println("5) List overdue   6) Search by title   0) Save and exit");
            String choice = ask(in, "> ");
            try {
                switch (choice) {
                    case "1" -> {
                        String type = ask(in, "Type (B = book, D = dvd)").toUpperCase();
                        String id = ask(in, "Item id");
                        String title = ask(in, "Title");
                        if (type.equals("B")) {
                            lib.addItem(new Book(id, title, ask(in, "Author")));
                        } else if (type.equals("D")) {
                            lib.addItem(new Dvd(id, title, Integer.parseInt(ask(in, "Minutes"))));
                        } else {
                            System.out.println("Unknown type");
                        }
                    }
                    case "2" -> lib.registerMember(new Member(ask(in, "Member id"), ask(in, "Name")));
                    case "3" -> {
                        Loan loan = lib.borrow(ask(in, "Member id"), ask(in, "Item id"));
                        System.out.println("Borrowed; due back " + loan.dueDate());
                    }
                    case "4" -> {
                        lib.giveBack(ask(in, "Item id"));
                        System.out.println("Returned");
                    }
                    case "5" -> {
                        List<Loan> late = lib.overdue(LocalDate.now());
                        if (late.isEmpty()) System.out.println("Nothing overdue");
                        late.forEach(l -> System.out.println(l.itemId() + " (member " + l.memberId()
                                + ") was due " + l.dueDate()));
                    }
                    case "6" -> {
                        List<LibraryItem> found = lib.searchByTitle(ask(in, "Search text"));
                        if (found.isEmpty()) System.out.println("No matches");
                        found.forEach(System.out::println);
                    }
                    case "0" -> running = false;
                    default -> System.out.println("Please choose 0 to 6");
                }
            } catch (ItemNotAvailableException | MemberLimitExceededException e) {
                System.out.println("Cannot borrow: " + e.getMessage());
            } catch (IllegalArgumentException e) {         
                System.out.println("Invalid input: " + e.getMessage());
            }
        }
        lib.save(dir);
        System.out.println("Data saved. Goodbye.");
    }
}
