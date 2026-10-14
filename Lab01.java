import java.time.Year;
import java.util.List;

public class Lab01 {

    interface Body { void go() throws Exception; }
    record Exercise(String id, String title, boolean interactive, Body body) {}

    static String[] extra = {};     

    // ===== Exercise 1.1: Car with display() and isAntique() (1.1 and 1.2) =====
    static class Ex1_1 {
        static class Car {
            String brand;
            int year;
            double mileage;

            void display() {
                System.out.println(brand + " (" + year + "), " + mileage + " km");
            }

            boolean isAntique() {
                return Year.now().getValue() - year > 25;  
            }

            static void run() throws Exception {
                Car c1 = new Car(); c1.brand = "Toyota"; c1.year = 2020; c1.mileage = 45000;
                Car c2 = new Car(); c2.brand = "Mazda";  c2.year = 1998; c2.mileage = 210000;
                Car c3 = new Car(); c3.brand = "Honda";  c3.year = 2012; c3.mileage = 98000.5;

                Car[] cars = { c1, c2, c3 };
                for (Car c : cars) {
                    c.display();
                    System.out.println("  antique? " + c.isAntique());
                }
            }
        }
    }

    // ===== Exercise 1.3: Rectangle: largest area, then aliasing (1.3 and 1.4) =====
    static class Ex1_3 {
        static class Rectangle {
            double width, height;

            double area()      { return width * height; }
            double perimeter() { return 2 * (width + height); }

            static void run() throws Exception {
                // ---- Exercise 1.3: largest of 5 rectangles ----
                double[][] dims = { {2, 3}, {4, 5}, {1, 9}, {6, 2}, {3, 3} };
                Rectangle[] rects = new Rectangle[5];
                for (int i = 0; i < rects.length; i++) {
                    rects[i] = new Rectangle();
                    rects[i].width  = dims[i][0];
                    rects[i].height = dims[i][1];
                }

                Rectangle largest = rects[0];
                for (Rectangle r : rects) {
                    if (r.area() > largest.area()) largest = r;
                }
                System.out.println("Largest: " + largest.width + " x " + largest.height
                        + ", area = " + largest.area() + ", perimeter = " + largest.perimeter());

                // ---- Exercise 1.4: predicted output 10.0 (r1 and r2 are aliases of ONE object) ----
                Rectangle r1 = new Rectangle();
                r1.width = 5;
                Rectangle r2 = r1;  
                r2.width = 10;
                System.out.println(r1.width);
            }
        }
    }

    // ===== Exercise 1.5: Person has an Address, null handling =====
    static class Ex1_5 {
        static class Address {
            String city;
        }

        static class Person {
            String name;
            Address address;        

            static void run() throws Exception {
                Person p = new Person();
                p.name = "Tariro";
                p.address = new Address();
                p.address.city = "Harare";
                System.out.println(p.name + " lives in " + p.address.city); 

                Person q = new Person();
                q.name = "Farai";                    

                if (q.address != null) {
                    System.out.println(q.address.city);
                } else {
                    System.out.println(q.name + " has no address yet");
                }
            }
        }
    }

    static final List<Exercise> ALL = List.of(
        new Exercise("1.1", "Car with display() and isAntique() (1.1 and 1.2)", false, Ex1_1.Car::run),
        new Exercise("1.3", "Rectangle: largest area, then aliasing (1.3 and 1.4)", false, Ex1_3.Rectangle::run),
        new Exercise("1.5", "Person has an Address, null handling", false, Ex1_5.Person::run));

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
            System.out.println("No exercise " + args[0] + ". Try: java Lab01 list");
            return;
        }
        for (Exercise e : ALL) {
            if (e.interactive()) {
                System.out.println("=== Exercise " + e.id() + " skipped (keyboard input): run  java Lab01 " + e.id());
                continue;
            }
            System.out.println("=== Exercise " + e.id() + ": " + e.title() + " ===");
            e.body().go();
            System.out.println();
        }
    }
}
