package dataStructure;

import exception.PriorityQueueEmptyHeapException;
import exception.PriorityQueueNullElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PriorityQueueTest {

    private PriorityQueue<Integer> pq;

    @BeforeEach
    void setUp() {
        pq = new PriorityQueue<>();
    }

    @Test
    void testInsertAndExtractMin() {
        pq.insert(5);
        pq.insert(3);
        pq.insert(7);

        assertEquals(3, pq.extractMin());
        assertEquals(5, pq.extractMin());
        assertEquals(7, pq.extractMin());
    }

    @Test
    void testSize() {
        assertEquals(0, pq.size());

        pq.insert(1);
        assertEquals(1, pq.size());

        pq.insert(2);
        assertEquals(2, pq.size());

        pq.extractMin();
        assertEquals(1, pq.size());

        pq.extractMin();
        assertEquals(0, pq.size());
    }

    @Test
    void testInsertNullThrowsException() {
        assertThrows(PriorityQueueNullElementException.class, () -> pq.insert(null));
    }

    @Test
    void testExtractMinFromEmptyQueueThrowsException() {
        assertThrows(PriorityQueueEmptyHeapException.class, () -> pq.extractMin());
    }

    @Test
    void testSingleElement() {
        pq.insert(42);
        assertEquals(1, pq.size());
        assertEquals(42, pq.extractMin());
        assertEquals(0, pq.size());
    }

    @Test
    void testInsertInAscendingOrder() {
        pq.insert(1);
        pq.insert(2);
        pq.insert(3);
        pq.insert(4);
        pq.insert(5);

        assertEquals(1, pq.extractMin());
        assertEquals(2, pq.extractMin());
        assertEquals(3, pq.extractMin());
        assertEquals(4, pq.extractMin());
        assertEquals(5, pq.extractMin());
    }

    @Test
    void testInsertInDescendingOrder() {
        pq.insert(5);
        pq.insert(4);
        pq.insert(3);
        pq.insert(2);
        pq.insert(1);

        assertEquals(1, pq.extractMin());
        assertEquals(2, pq.extractMin());
        assertEquals(3, pq.extractMin());
        assertEquals(4, pq.extractMin());
        assertEquals(5, pq.extractMin());
    }

    @Test
    void testInsertRandomOrder() {
        pq.insert(3);
        pq.insert(1);
        pq.insert(4);
        pq.insert(1);
        pq.insert(5);
        pq.insert(9);
        pq.insert(2);
        pq.insert(6);

        assertEquals(1, pq.extractMin());
        assertEquals(1, pq.extractMin());
        assertEquals(2, pq.extractMin());
        assertEquals(3, pq.extractMin());
        assertEquals(4, pq.extractMin());
        assertEquals(5, pq.extractMin());
        assertEquals(6, pq.extractMin());
        assertEquals(9, pq.extractMin());
    }

    @Test
    void testDuplicateValues() {
        pq.insert(5);
        pq.insert(5);
        pq.insert(5);

        assertEquals(5, pq.extractMin());
        assertEquals(5, pq.extractMin());
        assertEquals(5, pq.extractMin());
        assertEquals(0, pq.size());
    }

    @Test
    void testNegativeNumbers() {
        pq.insert(-5);
        pq.insert(3);
        pq.insert(-10);
        pq.insert(0);

        assertEquals(-10, pq.extractMin());
        assertEquals(-5, pq.extractMin());
        assertEquals(0, pq.extractMin());
        assertEquals(3, pq.extractMin());
    }

    @Test
    void testLargeNumberOfElements() {
        for (int i = 100; i > 0; i--) {
            pq.insert(i);
        }

        assertEquals(100, pq.size());

        for (int i = 1; i <= 100; i++) {
            assertEquals(i, pq.extractMin());
        }

        assertEquals(0, pq.size());
    }

    @Test
    void testStringPriorityQueue() {
        PriorityQueue<String> stringPq = new PriorityQueue<>();
        stringPq.insert("dog");
        stringPq.insert("cat");
        stringPq.insert("bird");
        stringPq.insert("ant");

        assertEquals("ant", stringPq.extractMin());
        assertEquals("bird", stringPq.extractMin());
        assertEquals("cat", stringPq.extractMin());
        assertEquals("dog", stringPq.extractMin());
    }

    @Test
    void testMixedOperations() {
        pq.insert(5);
        pq.insert(3);
        assertEquals(3, pq.extractMin());

        pq.insert(7);
        pq.insert(1);
        assertEquals(1, pq.extractMin());
        assertEquals(5, pq.extractMin());

        pq.insert(2);
        assertEquals(2, pq.extractMin());
        assertEquals(7, pq.extractMin());

        assertEquals(0, pq.size());
    }

    @Test
    void testCustomComparableObject() {
        class Person implements Comparable<Person> {
            String name;
            int age;

            Person(String name, int age) {
                this.name = name;
                this.age = age;
            }

            @Override
            public int compareTo(Person other) {
                return Integer.compare(this.age, other.age);
            }
        }

        PriorityQueue<Person> personPq = new PriorityQueue<>();
        personPq.insert(new Person("Alice", 30));
        personPq.insert(new Person("Bob", 25));
        personPq.insert(new Person("Charlie", 35));

        assertEquals("Bob", personPq.extractMin().name);
        assertEquals("Alice", personPq.extractMin().name);
        assertEquals("Charlie", personPq.extractMin().name);
    }

    @Test
    void testHeapPropertyMaintained() {
        for (int i = 0; i < 20; i++) {
            pq.insert((int) (Math.random() * 100));
        }

        Integer prev = pq.extractMin();
        while (pq.size() > 0) {
            Integer current = pq.extractMin();
            assertTrue(current >= prev);
            prev = current;
        }
    }

    @Test
    void testCustomComparator() {
        PriorityQueue<Integer> maxPq = new PriorityQueue<>((a, b) -> b - a);
        maxPq.insert(1);
        maxPq.insert(5);
        maxPq.insert(3);

        assertEquals(5, maxPq.extractMin());
        assertEquals(3, maxPq.extractMin());
        assertEquals(1, maxPq.extractMin());
    }

    @Test
    void testWithHuffmanNodes() {
        PriorityQueue<compression.HuffmanNode> nodePq = new PriorityQueue<>();
        nodePq.insert(new compression.HuffmanNode(65L, 10, 1));
        nodePq.insert(new compression.HuffmanNode(66L, 5, 1));
        nodePq.insert(new compression.HuffmanNode(67L, 15, 1));

        assertEquals(5, nodePq.extractMin().getFrequency());
        assertEquals(10, nodePq.extractMin().getFrequency());
        assertEquals(15, nodePq.extractMin().getFrequency());
    }
}