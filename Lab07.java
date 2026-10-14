import java.util.List;
import java.util.Scanner;

public class Lab07 {

    interface Body { void go() throws Exception; }
    record Exercise(String id, String title, boolean interactive, Body body) {}

    static String[] extra = {};    

    // ===== Exercise 7.1: Divide two typed integers (needs keyboard input) =====
    static class Ex7_1 {
        static class SafeDivide {
            static void run() throws Exception {
                Scanner in = new Scanner(System.in);
                try {
                    System.out.print("First number: ");
                    int a = Integer.parseInt(in.nextLine().trim());
                    System.out.print("Second number: ");
                    int b = Integer.parseInt(in.nextLine().trim());
                    System.out.println(a + " / " + b + " = " + (a / b));
                } catch (NumberFormatException e) {
                    System.out.println("Please enter whole numbers only.");
                } catch (ArithmeticException e) {
                    System.out.println("Cannot divide by zero.");
                } finally {
                    in.close();
                }
            }
        }
    }

    // ===== Exercise 7.2: Array access with ArrayIndexOutOfBoundsException =====
    static class Ex7_2 {
        static class SafeArray {
            static int elementAt(int[] data, int index) {
                try {
                    return data[index];
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Invalid index " + index + ": " + e.getMessage());
                    return -1;
                }
            }

            static void run() throws Exception {
                int[] data = {10, 20, 30};
                System.out.println(elementAt(data, 1));
                System.out.println(elementAt(data, 5));
            }
        }
    }

    // ===== Exercise 7.3: InvalidAgeException (checked) =====
    static class Ex7_3 {
        static class InvalidAgeException extends Exception {        
            private final int age;
            InvalidAgeException(int age) {
                super("Age must be between 0 and 120, but was " + age);
                this.age = age;
            }
            int getAge() { return age; }
        }

        static class AgeCheck {
            static void validateAge(int age) throws InvalidAgeException {
                if (age < 0 || age > 120) throw new InvalidAgeException(age);
            }

            static void run() throws Exception {
                int[] ages = { 25, -3, 130 };
                for (int a : ages) {
                    try {
                        validateAge(a);
                        System.out.println(a + " is valid");
                    } catch (InvalidAgeException e) {
                        System.out.println("Rejected: " + e.getMessage());
                    }
                }
            }
        }
    }

    // ===== Exercise 7.4: withdraw throws InsufficientFundsException =====
    static class Ex7_4 {
        static class InsufficientFundsException extends Exception {

            private final double shortfall;

            public InsufficientFundsException(String message, double shortfall) {
                super(message);
                this.shortfall = shortfall;
            }

            public double getShortfall() { return shortfall; }
        }

        static class BankAccount {
            private final String accountNumber;
            private double balance;

            public BankAccount(String accountNumber, double openingBalance) {
                if (openingBalance < 0) throw new IllegalArgumentException("Opening balance cannot be negative");
                this.accountNumber = accountNumber;
                this.balance = openingBalance;
            }

            public double getBalance() { return balance; }

            public void deposit(double amount) {
                if (amount <= 0) throw new IllegalArgumentException("Deposit must be positive");
                balance += amount;
            }

            public void withdraw(double amount) throws InsufficientFundsException {
                if (amount <= 0) throw new IllegalArgumentException("Withdrawal must be positive");
                if (amount > balance) {
                    throw new InsufficientFundsException(
                        "Balance " + balance + " is less than " + amount, amount - balance);
                }
                balance -= amount;
            }

            static void run() throws Exception {
                BankAccount acc = new BankAccount("ACC-001", 750);
                try {
                    acc.withdraw(100);
                    System.out.println("Balance now: " + acc.getBalance());
                    acc.withdraw(5000);
                } catch (InsufficientFundsException e) {
                    System.out.println(e.getMessage() + "; short by " + e.getShortfall());
                }
            }
        }
    }

    // ===== Exercise 7.5: return inside try, print inside finally =====
    static class Ex7_5 {
        static class FinallyReturn {
            static int test() {
                try {
                    System.out.println("in try");
                    return 1;
                } finally {
                    System.out.println("in finally");
                }
            }

            static void run() throws Exception {
                int r = test();
                System.out.println("returned " + r);
            }
        }
    }

    static final List<Exercise> ALL = List.of(
        new Exercise("7.1", "Divide two typed integers (needs keyboard input)", true, Ex7_1.SafeDivide::run),
        new Exercise("7.2", "Array access with ArrayIndexOutOfBoundsException", false, Ex7_2.SafeArray::run),
        new Exercise("7.3", "InvalidAgeException (checked)", false, Ex7_3.AgeCheck::run),
        new Exercise("7.4", "withdraw throws InsufficientFundsException", false, Ex7_4.BankAccount::run),
        new Exercise("7.5", "return inside try, print inside finally", false, Ex7_5.FinallyReturn::run));

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
            System.out.println("No exercise " + args[0] + ". Try: java Lab07 list");
            return;
        }
        for (Exercise e : ALL) {
            if (e.interactive()) {
                System.out.println("=== Exercise " + e.id() + " skipped (keyboard input): run  java Lab07 " + e.id());
                continue;
            }
            System.out.println("=== Exercise " + e.id() + ": " + e.title() + " ===");
            e.body().go();
            System.out.println();
        }
    }
}
