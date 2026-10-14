import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

public class Lab09 {

    interface Body { void go() throws Exception; }
    record Exercise(String id, String title, boolean interactive, Body body) {}

    static String[] extra = {};     

    // ===== Exercise 9.1: enum Level with a switch =====
    static class Ex9_1 {
        enum Level { LOW, MEDIUM, HIGH }

        static class LevelDemo {
            static void advice(Level level) {
                switch (level) {
                    case LOW    -> System.out.println("LOW: no action needed");
                    case MEDIUM -> System.out.println("MEDIUM: keep an eye on it");
                    case HIGH   -> System.out.println("HIGH: act immediately");
                }
            }

            static void run() throws Exception {
                for (Level l : Level.values()) advice(l);
            }
        }
    }

    // ===== Exercise 9.2: record Student =====
    static class Ex9_2 {
        record Student(String name, int age) {}

        static class RecordDemo {
            static void run() throws Exception {
                Student a = new Student("Tariro", 20);
                Student b = new Student("Tariro", 20);

                System.out.println(a);                         
                System.out.println(a.equals(b));               
                System.out.println(a == b);                    
                System.out.println(a.name() + " " + a.age());  
            }
        }
    }

    // ===== Exercise 9.3: Book equals/hashCode by ISBN in a HashSet =====
    static class Ex9_3 {
        static class Book {
            private final String isbn;
            private final String title;

            Book(String isbn, String title) { this.isbn = isbn; this.title = title; }

            @Override
            public boolean equals(Object o) {                 
                if (this == o) return true;
                if (!(o instanceof Book other)) return false;
                return isbn.equals(other.isbn);               
            }

            @Override
            public int hashCode() { return Objects.hash(isbn); }   

            @Override public String toString() { return title + " [" + isbn + "]"; }
        }

        static class BookSetDemo {
            static void run() throws Exception {
                Set<Book> shelf = new HashSet<>();
                System.out.println(shelf.add(new Book("978-0132350884", "Clean Code")));
                System.out.println(shelf.add(new Book("978-0132350884", "Clean Code (2nd printing)")));
                System.out.println("Size: " + shelf.size());
            }
        }
    }

    // ===== Exercise 9.4: LinkedList with nested Node and inner Iterator =====
    static class Ex9_4 {
        static class LinkedList<T> implements Iterable<T> {

            private static class Node<T> {
                final T value;
                Node<T> next;
                Node(T value) { this.value = value; }
            }

            private Node<T> head, tail;
            private int size;

            public void add(T value) {
                Node<T> n = new Node<>(value);
                if (head == null) head = tail = n;
                else { tail.next = n; tail = n; }
                size++;
            }

            public int size() { return size; }

            @Override
            public Iterator<T> iterator() { return new Iter(); }

            private class Iter implements Iterator<T> {
                private Node<T> current = head;             

                @Override public boolean hasNext() { return current != null; }

                @Override public T next() {
                    if (current == null) throw new NoSuchElementException();
                    T v = current.value;
                    current = current.next;
                    return v;
                }
            }

            static void run() throws Exception {
                LinkedList<String> list = new LinkedList<>();
                list.add("alpha"); list.add("beta"); list.add("gamma");
                for (String s : list) System.out.print(s + " "); 
                System.out.println("(" + list.size() + " items)");
            }
        }
    }

    // ===== Exercise 9.5: enum Coin: fewest coins =====
    static class Ex9_5 {
        enum Coin {
            PENNY(1), NICKEL(5), DIME(10), QUARTER(25);

            private final int cents;
            Coin(int cents) { this.cents = cents; }
            int cents() { return cents; }

            static Map<Coin, Integer> change(int amount) {
                if (amount < 0) throw new IllegalArgumentException("Negative amount");
                Map<Coin, Integer> result = new LinkedHashMap<>();
                Coin[] coins = values();
                for (int i = coins.length - 1; i >= 0; i--) {
                    int n = amount / coins[i].cents;
                    if (n > 0) {
                        result.put(coins[i], n);
                        amount -= n * coins[i].cents;
                    }
                }
                return result;
            }

            static void run() throws Exception {
                Map<Coin, Integer> r = change(68);
                System.out.println(r);
                System.out.println("Coins used: " + r.values().stream().mapToInt(Integer::intValue).sum());
            }
        }
    }

    static final List<Exercise> ALL = List.of(
        new Exercise("9.1", "enum Level with a switch", false, Ex9_1.LevelDemo::run),
        new Exercise("9.2", "record Student", false, Ex9_2.RecordDemo::run),
        new Exercise("9.3", "Book equals/hashCode by ISBN in a HashSet", false, Ex9_3.BookSetDemo::run),
        new Exercise("9.4", "LinkedList with nested Node and inner Iterator", false, Ex9_4.LinkedList::run),
        new Exercise("9.5", "enum Coin: fewest coins", false, Ex9_5.Coin::run));

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
            System.out.println("No exercise " + args[0] + ". Try: java Lab09 list");
            return;
        }
        for (Exercise e : ALL) {
            if (e.interactive()) {
                System.out.println("=== Exercise " + e.id() + " skipped (keyboard input): run  java Lab09 " + e.id());
                continue;
            }
            System.out.println("=== Exercise " + e.id() + ": " + e.title() + " ===");
            e.body().go();
            System.out.println();
        }
    }
}
