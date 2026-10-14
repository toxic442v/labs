import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Lab03 {

    interface Body { void go() throws Exception; }
    record Exercise(String id, String title, boolean interactive, Body body) {}

    static String[] extra = {};     

    // ===== Exercise 3.1: Student with validated marks and grade (3.1 and 3.2) =====
    static class Ex3_1 {
        static class Student {
            private String name;
            private int marks;

            public Student(String name, int marks) {
                setName(name);
                setMarks(marks);             
            }

            public String getName() { return name; }
            public int getMarks()   { return marks; }

            public void setName(String name) {
                if (name == null || name.isBlank()) throw new IllegalArgumentException("Name required");
                this.name = name.trim();
            }

            public void setMarks(int marks) {
                if (marks < 0 || marks > 100) {
                    throw new IllegalArgumentException("Marks must be 0 to 100, got " + marks);
                }
                this.marks = marks;
            }

            public String getGrade() {
                if (marks >= 70) return "A";
                if (marks >= 60) return "B";
                if (marks >= 50) return "C";
                return "F";
            }

            static void run() throws Exception {
                Student s = new Student("Rudo", 74);
                System.out.println(s.getName() + ": " + s.getMarks() + " -> " + s.getGrade());
                try {
                    s.setMarks(130);
                } catch (IllegalArgumentException e) {
                    System.out.println("Rejected: " + e.getMessage());
                }
            }
        }
    }

    // ===== Exercise 3.3: BankAccount with transaction history =====
    static class Ex3_3 {
        static class BankAccount {

            private final String accountNumber;
            private String ownerName;
            private double balance;
            private final List<String> history = new ArrayList<>();

            public BankAccount(String accountNumber, String ownerName, double openingBalance) {
                if (openingBalance < 0) {
                    throw new IllegalArgumentException("Opening balance cannot be negative");
                }
                this.accountNumber = accountNumber;
                setOwnerName(ownerName);
                this.balance = openingBalance;
                history.add("OPEN " + openingBalance);
            }

            public String getAccountNumber() { return accountNumber; }
            public String getOwnerName()     { return ownerName; }
            public double getBalance()       { return balance; }

            public void setOwnerName(String ownerName) {
                if (ownerName == null || ownerName.isBlank()) {
                    throw new IllegalArgumentException("Owner name required");
                }
                this.ownerName = ownerName.trim();
            }

            public void deposit(double amount) {
                if (amount <= 0) throw new IllegalArgumentException("Deposit must be positive");
                balance += amount;
                history.add("DEPOSIT " + amount);
            }

            public boolean withdraw(double amount) {
                if (amount <= 0) throw new IllegalArgumentException("Withdrawal must be positive");
                if (amount > balance) { history.add("DECLINED " + amount); return false; }
                balance -= amount;
                history.add("WITHDRAW " + amount);
                return true;
            }

            public List<String> getHistory() { return List.copyOf(history); }

            @Override
            public String toString() {
                return String.format("%s [%s] balance: %.2f", accountNumber, ownerName, balance);
            }
        }

        static class BankDemo {
            static void run() throws Exception {
                BankAccount acc = new BankAccount("ACC-001", "Tendai", 500.00);
                acc.deposit(250.00);
                System.out.println(acc);

                System.out.println("Withdraw 100: " + acc.withdraw(100.00));
                System.out.println("Withdraw 5000: " + acc.withdraw(5000.00));
                System.out.println(acc);

                System.out.println("History: " + acc.getHistory());
                try {
                    acc.getHistory().add("FAKE");
                } catch (UnsupportedOperationException e) {
                    System.out.println("History is read-only");
                }
            }
        }
    }

    // ===== Exercise 3.4: ImmutablePerson =====
    static class Ex3_4 {
        static final class ImmutablePerson {                
            private final String name;                      
            private final LocalDate dateOfBirth;
            private final List<String> languages;

            public ImmutablePerson(String name, LocalDate dateOfBirth, List<String> languages) {
                this.name = Objects.requireNonNull(name);
                this.dateOfBirth = Objects.requireNonNull(dateOfBirth);
                this.languages = List.copyOf(languages);     
            }

            public String getName()            { return name; }
            public LocalDate getDateOfBirth()  { return dateOfBirth; }   
            public List<String> getLanguages() { return languages; }       
            public ImmutablePerson withName(String newName) {
                return new ImmutablePerson(newName, dateOfBirth, languages);
            }

            static void run() throws Exception {
                ImmutablePerson p = new ImmutablePerson("Chipo", LocalDate.of(2001, 5, 17), List.of("English", "Shona"));
                ImmutablePerson q = p.withName("Chipo M.");
                System.out.println(p.getName() + " / " + q.getName() + " / " + q.getLanguages());
            }
        }
    }

    // ===== Exercise 3.5: Temperature stored in kelvin =====
    static class Ex3_5 {
        static class Temperature {
            private static final double ABSOLUTE_ZERO_C = -273.15;
            private double kelvin;                      
            public static Temperature ofCelsius(double c) {
                Temperature t = new Temperature();
                t.setCelsius(c);
                return t;
            }

            public double getCelsius()    { return kelvin + ABSOLUTE_ZERO_C; }
            public double getFahrenheit() { return getCelsius() * 9 / 5 + 32; }

            public void setCelsius(double c) {
                if (c < ABSOLUTE_ZERO_C) {
                    throw new IllegalArgumentException("Below absolute zero: " + c);
                }
                this.kelvin = c - ABSOLUTE_ZERO_C;
            }

            static void run() throws Exception {
                Temperature t = Temperature.ofCelsius(25);
                System.out.printf("%.2f C = %.2f F%n", t.getCelsius(), t.getFahrenheit());
                try { t.setCelsius(-300); } catch (IllegalArgumentException e) { System.out.println(e.getMessage()); }
            }
        }
    }

    static final List<Exercise> ALL = List.of(
        new Exercise("3.1", "Student with validated marks and grade (3.1 and 3.2)", false, Ex3_1.Student::run),
        new Exercise("3.3", "BankAccount with transaction history", false, Ex3_3.BankDemo::run),
        new Exercise("3.4", "ImmutablePerson", false, Ex3_4.ImmutablePerson::run),
        new Exercise("3.5", "Temperature stored in kelvin", false, Ex3_5.Temperature::run));

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
            System.out.println("No exercise " + args[0] + ". Try: java Lab03 list");
            return;
        }
        for (Exercise e : ALL) {
            if (e.interactive()) {
                System.out.println("=== Exercise " + e.id() + " skipped (keyboard input): run  java Lab03 " + e.id());
                continue;
            }
            System.out.println("=== Exercise " + e.id() + ": " + e.title() + " ===");
            e.body().go();
            System.out.println();
        }
    }
}
