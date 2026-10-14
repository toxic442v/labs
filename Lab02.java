import java.util.List;

public class Lab02 {

    interface Body { void go() throws Exception; }
    record Exercise(String id, String title, boolean interactive, Body body) {}

    static String[] extra = {};     
    // ===== Exercise 2.1: Circle: constructors and static counter (2.1 and 2.2) =====
    static class Ex2_1 {
        static class Circle {
            private static int count = 0;       
            private final double radius;

            public Circle(double radius) {
                this.radius = radius;
                count++;
            }

            public Circle() {
                this(1.0);                     
            }

            public static int getCount() { return count; }
            public double area()         { return Math.PI * radius * radius; }

            static void run() throws Exception {
                Circle a = new Circle();
                Circle b = new Circle(2.5);
                System.out.printf("%.2f %.2f%n", a.area(), b.area());
                System.out.println("Circles created: " + Circle.getCount());
            }
        }
    }

    // ===== Exercise 2.3: Employee: auto id, overloads, copy constructor =====
    static class Ex2_3 {
        static class Employee {
            private static int nextId = 1;

            private final int employeeId;
            private final String name;
            private final String department;
            private final double salary;

            public Employee(String name, String department, double salary) {
                this.employeeId = nextId++;    
                this.name = name;
                this.department = department;
                this.salary = salary;
            }

            public Employee(String name, double salary) { this(name, "Unassigned", salary); }
            public Employee(String name)                { this(name, 0.0); }
            public Employee(Employee other) { this(other.name, other.department, other.salary); }

            @Override
            public String toString() {
                return "#" + employeeId + " " + name + ", " + department + ", " + salary;
            }

            static void run() throws Exception {
                Employee e1 = new Employee("Kudzai", "IT", 2000);
                Employee e2 = new Employee("Nyasha", 3500);
                Employee e3 = new Employee("Tatenda");
                Employee e4 = new Employee(e1);
                System.out.println(e1);
                System.out.println(e2);
                System.out.println(e3);
                System.out.println(e4);
            }
        }
    }

    // ===== Exercise 2.4: Order of static and instance blocks =====
    static class Ex2_4 {
        static class TwoBlocks {
            static { System.out.println("static 1"); }
            { System.out.println("instance 1"); }
            TwoBlocks() { System.out.println("constructor"); }
            static { System.out.println("static 2"); }
            { System.out.println("instance 2"); }

            static void run() throws Exception {
                System.out.println("main starts");
                new TwoBlocks();
                new TwoBlocks();
            }
        }
    }

    // ===== Exercise 2.5: Temperature with static factories =====
    static class Ex2_5 {
        static final class Temperature {
            private final double celsius;

            private Temperature(double celsius) { this.celsius = celsius; }

            public static Temperature fromCelsius(double c)    { return new Temperature(c); }
            public static Temperature fromFahrenheit(double f) { return new Temperature((f - 32) * 5 / 9); }

            public double getCelsius()    { return celsius; }
            public double getFahrenheit() { return celsius * 9 / 5 + 32; }

            static void run() throws Exception {
                System.out.println(Temperature.fromFahrenheit(212).getCelsius());  
                System.out.println(Temperature.fromCelsius(37).getFahrenheit());    
            }
        }
    }

    static final List<Exercise> ALL = List.of(
        new Exercise("2.1", "Circle: constructors and static counter (2.1 and 2.2)", false, Ex2_1.Circle::run),
        new Exercise("2.3", "Employee: auto id, overloads, copy constructor", false, Ex2_3.Employee::run),
        new Exercise("2.4", "Order of static and instance blocks", false, Ex2_4.TwoBlocks::run),
        new Exercise("2.5", "Temperature with static factories", false, Ex2_5.Temperature::run));

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
            System.out.println("No exercise " + args[0] + ". Try: java Lab02 list");
            return;
        }
        for (Exercise e : ALL) {
            if (e.interactive()) {
                System.out.println("=== Exercise " + e.id() + " skipped (keyboard input): run  java Lab02 " + e.id());
                continue;
            }
            System.out.println("=== Exercise " + e.id() + ": " + e.title() + " ===");
            e.body().go();
            System.out.println();
        }
    }
}
