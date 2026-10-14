import java.util.ArrayList;
import java.util.List;

public class Lab04 {

    interface Body { void go() throws Exception; }
    record Exercise(String id, String title, boolean interactive, Body body) {}

    static String[] extra = {};     

    // ===== Exercise 4.1: Person, Student, Teacher (4.1 and 4.2) =====
    static class Ex4_1 {
        static class Person {
            private final String name;
            private final int age;

            Person(String name, int age) { this.name = name; this.age = age; }

            String getName() { return name; }
            void display() { System.out.println(name + ", age " + age); }  
        }

        static class Student extends Person {
            private final String course;
            Student(String name, int age, String course) { super(name, age); this.course = course; }
            String getCourse() { return course; }
        }

        static class Teacher extends Person {
            private final String subject;
            Teacher(String name, int age, String subject) { super(name, age); this.subject = subject; }
            String getSubject() { return subject; }
        }

        static class PersonDemo {
            static void run() throws Exception {
                Student s = new Student("Tariro", 20, "Computer Science");
                Teacher t = new Teacher("Mr Moyo", 45, "OOP");

                s.display();                       
                System.out.println("  course: " + s.getCourse());
                t.display();                       
                System.out.println("  teaches: " + t.getSubject());
            }
        }
    }

    // ===== Exercise 4.3: Constructor order: Vehicle, Car, ElectricCar =====
    static class Ex4_3 {
        static class Vehicle {
            Vehicle() { System.out.println("Vehicle constructor"); }
        }
        static class Car extends Vehicle {
            Car() { System.out.println("Car constructor"); }               
        }
        static class ElectricCar extends Car {
            ElectricCar() { System.out.println("ElectricCar constructor"); }  
        }

        static class VehicleChain {
            static void run() throws Exception {
                new ElectricCar();
            }
        }
    }

    // ===== Exercise 4.4: Dog extends Animal: the super(...) fix =====
    static class Ex4_4 {
        static class Animal {
            private final String name;
            Animal(String name) { this.name = name; }      
            String getName() { return name; }
        }
        static class Dog extends Animal {
            Dog(String name) { super(name); }              
        }

        static class DogDemo {
            static void run() throws Exception {
                System.out.println(new Dog("Rex").getName());
            }
        }
    }

    // ===== Exercise 4.5: Stack by composition vs inheritance =====
    static class Ex4_5 {
        static class Stack<T> {
            private final List<T> items = new ArrayList<>();

            public void push(T x) { items.add(x); }

            public T pop() {
                if (items.isEmpty()) throw new IllegalStateException("Stack is empty");
                return items.remove(items.size() - 1);
            }

            public T peek() {
                if (items.isEmpty()) throw new IllegalStateException("Stack is empty");
                return items.get(items.size() - 1);
            }

            public boolean isEmpty() { return items.isEmpty(); }

            static void run() throws Exception {
                Stack<Integer> s = new Stack<>();
                s.push(1); s.push(2);
                System.out.println(s.pop() + " " + s.pop() + " " + s.isEmpty());

                BadStack<Integer> b = new BadStack<>();
                b.push(1); b.push(2);
                b.add(0, 99);      
                System.out.println("BadStack exposes: " + b);
            }
        }

        static class BadStack<T> extends ArrayList<T> {
            void push(T x) { add(x); }
            T pop()        { return remove(size() - 1); }
        }
    }

    static final List<Exercise> ALL = List.of(
        new Exercise("4.1", "Person, Student, Teacher (4.1 and 4.2)", false, Ex4_1.PersonDemo::run),
        new Exercise("4.3", "Constructor order: Vehicle, Car, ElectricCar", false, Ex4_3.VehicleChain::run),
        new Exercise("4.4", "Dog extends Animal: the super(...) fix", false, Ex4_4.DogDemo::run),
        new Exercise("4.5", "Stack by composition vs inheritance", false, Ex4_5.Stack::run));

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
            System.out.println("No exercise " + args[0] + ". Try: java Lab04 list");
            return;
        }
        for (Exercise e : ALL) {
            if (e.interactive()) {
                System.out.println("=== Exercise " + e.id() + " skipped (keyboard input): run  java Lab04 " + e.id());
                continue;
            }
            System.out.println("=== Exercise " + e.id() + ": " + e.title() + " ===");
            e.body().go();
            System.out.println();
        }
    }
}
