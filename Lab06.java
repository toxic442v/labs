import java.util.*;
import java.util.List;

public class Lab06 {

    interface Body { void go() throws Exception; }
    record Exercise(String id, String title, boolean interactive, Body body) {}

    static String[] extra = {};     

    // ===== Exercise 6.1: Abstract Shape: Circle, Rectangle, Square =====
    static class Ex6_1 {
        static abstract class Shape {
            abstract double area();
            abstract double perimeter();

            @Override public String toString() {
                return String.format("%s: area=%.2f, perimeter=%.2f", getClass().getSimpleName(), area(), perimeter());
            }
        }

        static class Circle extends Shape {
            private final double r;
            Circle(double r) { this.r = r; }
            @Override double area()      { return Math.PI * r * r; }
            @Override double perimeter() { return 2 * Math.PI * r; }
        }

        static class Rectangle extends Shape {
            private final double w, h;
            Rectangle(double w, double h) { this.w = w; this.h = h; }
            @Override double area()      { return w * h; }
            @Override double perimeter() { return 2 * (w + h); }
        }

        static class Square extends Rectangle {
            Square(double side) { super(side, side); }
        }

        static class ShapeDemo {
            static void run() throws Exception {
                Shape[] shapes = { new Circle(1), new Rectangle(2, 3), new Square(4) };
                for (Shape s : shapes) System.out.println(s);
            }
        }
    }

    // ===== Exercise 6.2: Flyable: Bird and Aeroplane =====
    static class Ex6_2 {
        interface Flyable {
            void fly();
        }
        static class Bird implements Flyable {
            @Override public void fly() { System.out.println("Bird flaps its wings and flies"); }
        }
        static class Aeroplane implements Flyable {
            @Override public void fly() { System.out.println("Aeroplane takes off using jet engines"); }
        }

        static class FlyDemo {
            static void run() throws Exception {
                Flyable[] things = { new Bird(), new Aeroplane() };
                for (Flyable f : things) f.fly();
            }
        }
    }

    // ===== Exercise 6.3: Abstract Account =====
    static class Ex6_3 {
        static abstract class Account {
            protected double balance;

            void deposit(double amount) {                     
                if (amount <= 0) throw new IllegalArgumentException("Deposit must be positive");
                balance += amount;
            }

            abstract double monthlyFee();                     

            void applyMonthlyFee() { balance -= monthlyFee(); }
            double getBalance()    { return balance; }
        }

        static class SavingsAccount extends Account {
            @Override double monthlyFee() { return 0.0; }
        }

        static class CurrentAccount extends Account {
            @Override double monthlyFee() { return 10.0; }
        }

        static class AccountDemo {
            static void run() throws Exception {
                Account[] accounts = { new SavingsAccount(), new CurrentAccount() };
                for (Account a : accounts) {
                    a.deposit(500);
                    a.applyMonthlyFee();
                    System.out.println(a.getClass().getSimpleName() + ": " + a.getBalance());
                }
            }
        }
    }

    // ===== Exercise 6.4: Comparable by name, Comparator by marks =====
    static class Ex6_4 {
        static class Student implements Comparable<Student> {
            private final String name;
            private final int marks;

            Student(String name, int marks) { this.name = name; this.marks = marks; }
            String getName() { return name; }
            int getMarks()   { return marks; }

            @Override public int compareTo(Student o) { return name.compareTo(o.name); } 
            @Override public String toString() { return name + "(" + marks + ")"; }
        }

        static class StudentSort {
            static void run() throws Exception {
                List<Student> list = new ArrayList<>(List.of(
                    new Student("Kuda", 65), new Student("Ada", 88), new Student("Lynn", 72)));

                Collections.sort(list);
                System.out.println("By name:  " + list);

                Comparator<Student> byMarks = Comparator.comparingInt(Student::getMarks).reversed();
                list.sort(byMarks);
                System.out.println("By marks: " + list);
            }
        }
    }

    // ===== Exercise 6.5: Two default methods: A.super.hello() =====
    static class Ex6_5 {
        interface A { default void hello() { System.out.println("Hello from A"); } }
        interface B { default void hello() { System.out.println("Hello from B"); } }

        static class C implements A, B {
            @Override
            public void hello() {
                A.super.hello();       
                B.super.hello();       
            }
        }

        static class DiamondDemo {
            static void run() throws Exception { new C().hello(); }
        }
    }

    static final List<Exercise> ALL = List.of(
        new Exercise("6.1", "Abstract Shape: Circle, Rectangle, Square", false, Ex6_1.ShapeDemo::run),
        new Exercise("6.2", "Flyable: Bird and Aeroplane", false, Ex6_2.FlyDemo::run),
        new Exercise("6.3", "Abstract Account", false, Ex6_3.AccountDemo::run),
        new Exercise("6.4", "Comparable by name, Comparator by marks", false, Ex6_4.StudentSort::run),
        new Exercise("6.5", "Two default methods: A.super.hello()", false, Ex6_5.DiamondDemo::run));

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
            System.out.println("No exercise " + args[0] + ". Try: java Lab06 list");
            return;
        }
        for (Exercise e : ALL) {
            if (e.interactive()) {
                System.out.println("=== Exercise " + e.id() + " skipped (keyboard input): run  java Lab06 " + e.id());
                continue;
            }
            System.out.println("=== Exercise " + e.id() + ": " + e.title() + " ===");
            e.body().go();
            System.out.println();
        }
    }
}
