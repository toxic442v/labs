import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Lab08 {

    interface Body { void go() throws Exception; }
    record Exercise(String id, String title, boolean interactive, Body body) {}

    static String[] extra = {};     

    // ===== Exercise 8.1: Sum, max and reverse of 10 integers =====
    static class Ex8_1 {
        static class IntListDemo {
            static void run() throws Exception {
                List<Integer> nums = new ArrayList<>(List.of(4, 8, 15, 16, 23, 42, 7, 1, 9, 30));

                int sum = 0;
                for (int n : nums) sum += n;
                int max = Collections.max(nums);

                List<Integer> reversed = new ArrayList<>(nums);
                Collections.reverse(reversed);

                System.out.println("Sum: " + sum);
                System.out.println("Max: " + max);
                System.out.println("Reversed: " + reversed);
            }
        }
    }

    // ===== Exercise 8.2: Remove duplicates with a Set =====
    static class Ex8_2 {
        static class Dedupe {
            static void run() throws Exception {
                List<String> names = List.of("Ada", "Ben", "Ada", "Cara", "Ben");
                Set<String> unique = new LinkedHashSet<>(names);  
                System.out.println(new ArrayList<>(unique));
            }
        }
    }

    // ===== Exercise 8.3: Generic Stack<T> =====
    static class Ex8_3 {
        static class GenericStack<T> {
            private final List<T> items = new ArrayList<>();

            public void push(T item) { items.add(item); }

            public T pop() {
                if (isEmpty()) throw new IllegalStateException("Stack is empty");
                return items.remove(items.size() - 1);
            }

            public T peek() {
                if (isEmpty()) throw new IllegalStateException("Stack is empty");
                return items.get(items.size() - 1);
            }

            public boolean isEmpty() { return items.isEmpty(); }

            static void run() throws Exception {
                GenericStack<String> s = new GenericStack<>();
                s.push("a"); s.push("b"); s.push("c");
                System.out.println(s.peek());     // c
                System.out.println(s.pop());      // c
                System.out.println(s.pop());      // b
                System.out.println(s.isEmpty());  // false

                GenericStack<Integer> n = new GenericStack<>();
                n.push(1);
            }
        }
    }

    // ===== Exercise 8.4: Word frequency sorted descending =====
    static class Ex8_4 {
        static class WordFrequency {
            static void run() throws Exception {
                String text = "the cat and the dog and the bird";

                Map<String, Integer> freq = new HashMap<>();
                for (String w : text.toLowerCase().split("\\s+")) {
                    freq.merge(w, 1, Integer::sum);
                }

                List<Map.Entry<String, Integer>> entries = new ArrayList<>(freq.entrySet());
                entries.sort(Map.Entry.<String, Integer>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()));      

                for (Map.Entry<String, Integer> e : entries) {
                    System.out.println(e.getKey() + " = " + e.getValue());
                }
            }
        }
    }

    // ===== Exercise 8.5: Generic bubbleSort =====
    static class Ex8_5 {
        static class BubbleSort {
            static <T extends Comparable<T>> void bubbleSort(T[] arr) {
                for (int i = 0; i < arr.length - 1; i++) {
                    boolean swapped = false;
                    for (int j = 0; j < arr.length - 1 - i; j++) {
                        if (arr[j].compareTo(arr[j + 1]) > 0) {
                            T tmp = arr[j]; arr[j] = arr[j + 1]; arr[j + 1] = tmp;
                            swapped = true;
                        }
                    }
                    if (!swapped) break;           
                }
            }

            static void run() throws Exception {
                Integer[] nums = {5, 2, 9, 1};
                String[] words = {"pear", "apple", "fig"};
                bubbleSort(nums);
                bubbleSort(words);
                System.out.println(Arrays.toString(nums));    
                System.out.println(Arrays.toString(words));   
            }
        }
    }

    static final List<Exercise> ALL = List.of(
        new Exercise("8.1", "Sum, max and reverse of 10 integers", false, Ex8_1.IntListDemo::run),
        new Exercise("8.2", "Remove duplicates with a Set", false, Ex8_2.Dedupe::run),
        new Exercise("8.3", "Generic Stack<T>", false, Ex8_3.GenericStack::run),
        new Exercise("8.4", "Word frequency sorted descending", false, Ex8_4.WordFrequency::run),
        new Exercise("8.5", "Generic bubbleSort", false, Ex8_5.BubbleSort::run));

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
            System.out.println("No exercise " + args[0] + ". Try: java Lab08 list");
            return;
        }
        for (Exercise e : ALL) {
            if (e.interactive()) {
                System.out.println("=== Exercise " + e.id() + " skipped (keyboard input): run  java Lab08 " + e.id());
                continue;
            }
            System.out.println("=== Exercise " + e.id() + ": " + e.title() + " ===");
            e.body().go();
            System.out.println();
        }
    }
}
