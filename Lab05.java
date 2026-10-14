import java.util.Arrays;
import java.util.List;

public class Lab05 {

    interface Body { void go() throws Exception; }
    record Exercise(String id, String title, boolean interactive, Body body) {}

    static String[] extra = {};    

    // ===== Exercise 5.1: Overloaded print =====
    static class Ex5_1 {
        static class Printer {
            void print(int x)     { System.out.println("int: " + x); }
            void print(double x)  { System.out.println("double: " + x); }
            void print(String x)  { System.out.println("String: " + x); }
            void print(int[] x)   { System.out.println("int[]: " + Arrays.toString(x)); }

            static void run() throws Exception {
                Printer p = new Printer();
                p.print(5);
                p.print(2.5);
                p.print("hello");
                p.print(new int[] {1, 2, 3});
            }
        }
    }

    // ===== Exercise 5.2: Animal, Cat, Cow, Duck =====
    static class Ex5_2 {
        static class Animal {
            String sound() { return "..."; }
        }
        static class Cat  extends Animal { @Override String sound() { return "Meow"; } }
        static class Cow  extends Animal { @Override String sound() { return "Moo"; } }
        static class Duck extends Animal { @Override String sound() { return "Quack"; } }

        static class Farm {
            static void run() throws Exception {
                Animal[] animals = { new Cat(), new Cow(), new Duck() };
                for (Animal a : animals) {
                    System.out.println(a.getClass().getSimpleName() + " says " + a.sound());
                }
            }
        }
    }

    // ===== Exercise 5.3: Payroll with Intern added =====
    static class Ex5_3 {
        static class Employee {

            private final String name;
            protected double baseSalary;      

            public Employee(String name, double baseSalary) {
                this.name = name;
                this.baseSalary = baseSalary;
                System.out.println("Employee constructor: " + name);
            }

            public String getName() { return name; }

            public double calculatePay() {
                return baseSalary;
            }

            public void describe() {
                System.out.printf("%s earns %.2f%n", name, calculatePay());
            }
        }

        static class Manager extends Employee {

            private double bonus;

            public Manager(String name, double baseSalary, double bonus) {
                super(name, baseSalary);
                this.bonus = bonus;
                System.out.println("Manager constructor: " + name);
            }

            @Override
            public double calculatePay() {
                return super.calculatePay() + bonus;
            }
        }

        static class Developer extends Employee {

            private int overtimeHours;
            private static final double OVERTIME_RATE = 25.0;

            public Developer(String name, double baseSalary, int overtimeHours) {
                super(name, baseSalary);
                this.overtimeHours = overtimeHours;
            }

            @Override
            public double calculatePay() {
                return baseSalary + overtimeHours * OVERTIME_RATE;
            }
        }

        static class Intern extends Employee {
            private final double stipend;

            public Intern(String name, double stipend) {
                super(name, 0);
                this.stipend = stipend;
            }

            @Override
            public double calculatePay() { return stipend; }
        }

        static class Payroll {
            static double totalPayroll(Employee[] staff) {
                double total = 0;
                for (Employee e : staff) total += e.calculatePay();
                return total;
            }

            static void run() throws Exception {
                Employee[] staff = {
                    new Employee("Kudzai", 2000),
                    new Manager("Nyasha", 3500, 800),
                    new Developer("Tatenda", 3000, 10),
                    new Intern("Chipo", 600)
                };
                System.out.printf("Total payroll: %.2f%n", totalPayroll(staff));
            }
        }
    }

    // ===== Exercise 5.4: Method vs field through a superclass reference =====
    static class Ex5_4 {
        static class Parent {
            String label = "Parent field";
            String who() { return "Parent method"; }
        }
        static class Child extends Parent {
            String label = "Child field";                  
            @Override String who() { return "Child method"; }
        }

        static class FieldVsMethod {
            static void run() throws Exception {
                Parent p = new Child();
                System.out.println(p.who());            
                System.out.println(p.label);           
                System.out.println(((Child) p).label);  
            }
        }
    }

    // ===== Exercise 5.5A: PaymentProcessor, Design A (instanceof) =====
    static class Ex5_5A {
        static class Payment {
            final double amount;
            Payment(double amount) { this.amount = amount; }
        }
        static class CardPayment extends Payment {
            final String cardNumber;
            CardPayment(double amount, String cardNumber) { super(amount); this.cardNumber = cardNumber; }
        }
        static class MobileMoneyPayment extends Payment {
            final String phone;
            MobileMoneyPayment(double amount, String phone) { super(amount); this.phone = phone; }
        }

        static class PaymentProcessor {
            void pay(Payment p) {
                if (p instanceof CardPayment c) {
                    System.out.println("Charging card " + c.cardNumber + ": " + c.amount);
                } else if (p instanceof MobileMoneyPayment m) {
                    System.out.println("Sending mobile money to " + m.phone + ": " + m.amount);
                } else {
                    throw new IllegalArgumentException("Unsupported payment type");
                }
            }
        }

        static class PaymentDemoA {
            static void run() throws Exception {
                PaymentProcessor proc = new PaymentProcessor();
                proc.pay(new CardPayment(50, "4111-1111"));
                proc.pay(new MobileMoneyPayment(20, "0771234567"));
            }
        }
    }

    // ===== Exercise 5.5B: PaymentProcessor, Design B (polymorphic process()) =====
    static class Ex5_5B {
        static abstract class Payment {
            final double amount;
            Payment(double amount) { this.amount = amount; }
            abstract void process();                       // each subclass knows how to pay
        }
        static class CardPayment extends Payment {
            final String cardNumber;
            CardPayment(double amount, String cardNumber) { super(amount); this.cardNumber = cardNumber; }
            @Override void process() { System.out.println("Charging card " + cardNumber + ": " + amount); }
        }
        static class MobileMoneyPayment extends Payment {
            final String phone;
            MobileMoneyPayment(double amount, String phone) { super(amount); this.phone = phone; }
            @Override void process() { System.out.println("Sending mobile money to " + phone + ": " + amount); }
        }

        static class PaymentProcessor {
            void pay(Payment p) { p.process(); }        
        }

        static class PaymentDemoB {
            static void run() throws Exception {
                PaymentProcessor proc = new PaymentProcessor();
                proc.pay(new CardPayment(50, "4111-1111"));
                proc.pay(new MobileMoneyPayment(20, "0771234567"));
            }
        }
    }

    static final List<Exercise> ALL = List.of(
        new Exercise("5.1", "Overloaded print", false, Ex5_1.Printer::run),
        new Exercise("5.2", "Animal, Cat, Cow, Duck", false, Ex5_2.Farm::run),
        new Exercise("5.3", "Payroll with Intern added", false, Ex5_3.Payroll::run),
        new Exercise("5.4", "Method vs field through a superclass reference", false, Ex5_4.FieldVsMethod::run),
        new Exercise("5.5A", "PaymentProcessor, Design A (instanceof)", false, Ex5_5A.PaymentDemoA::run),
        new Exercise("5.5B", "PaymentProcessor, Design B (polymorphic process())", false, Ex5_5B.PaymentDemoB::run));

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
            System.out.println("No exercise " + args[0] + ". Try: java Lab05 list");
            return;
        }
        for (Exercise e : ALL) {
            if (e.interactive()) {
                System.out.println("=== Exercise " + e.id() + " skipped (keyboard input): run  java Lab05 " + e.id());
                continue;
            }
            System.out.println("=== Exercise " + e.id() + ": " + e.title() + " ===");
            e.body().go();
            System.out.println();
        }
    }
}
