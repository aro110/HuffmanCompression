package dataStructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HashMapTest {

    private HashMap<String, Integer> map;

    @BeforeEach
    void setUp() {
        map = new HashMap<>(16);
    }

    @Test
    void testPutAndGet() {
        map.put("key1", 100);
        assertEquals(100, map.get("key1"));
    }

    @Test
    void testPutOverwrite() {
        map.put("key1", 100);
        map.put("key1", 200);
        assertEquals(200, map.get("key1"));
        assertEquals(1, map.size());
    }

    @Test
    void testGetNonExistent() {
        assertNull(map.get("nonexistent"));
    }

    @Test
    void testGetOrDefault() {
        map.put("key1", 100);
        assertEquals(100, map.getOrDefault("key1", 0));
        assertEquals(0, map.getOrDefault("nonexistent", 0));
    }

    @Test
    void testSize() {
        assertEquals(0, map.size());
        map.put("key1", 100);
        assertEquals(1, map.size());
        map.put("key2", 200);
        assertEquals(2, map.size());
    }

    @Test
    void testEntrySet() {
        map.put("key1", 100);
        map.put("key2", 200);

        List<HashMap.Entry<String, Integer>> entries = map.entrySet();
        assertEquals(2, entries.size());

        boolean foundKey1 = false;
        boolean foundKey2 = false;

        for (HashMap.Entry<String, Integer> entry : entries) {
            if (entry.getKey().equals("key1") && entry.getValue() == 100) {
                foundKey1 = true;
            }
            if (entry.getKey().equals("key2") && entry.getValue() == 200) {
                foundKey2 = true;
            }
        }

        assertTrue(foundKey1);
        assertTrue(foundKey2);
    }

    @Test
    void testResize() {
        HashMap<String, Integer> smallMap = new HashMap<>(4);
        for (int i = 0; i < 100; i++) {
            smallMap.put("key" + i, i);
        }

        assertEquals(100, smallMap.size());

        for (int i = 0; i < 100; i++) {
            assertEquals(i, smallMap.get("key" + i));
        }
    }

    @Test
    void testLongKeys() {
        HashMap<Long, String> longMap = new HashMap<>(16);
        longMap.put(1L, "one");
        longMap.put(2L, "two");
        longMap.put(3L, "three");

        assertEquals("one", longMap.get(1L));
        assertEquals("two", longMap.get(2L));
        assertEquals("three", longMap.get(3L));
        assertEquals(3, longMap.size());
    }

    @Test
    void testIntegerKeys() {
        HashMap<Integer, String> intMap = new HashMap<>(16);
        intMap.put(1, "one");
        intMap.put(2, "two");

        assertEquals("one", intMap.get(1));
        assertEquals("two", intMap.get(2));
    }

    @Test
    void testHashCollisions() {
        HashMap<Integer, String> smallMap = new HashMap<>(2);

        smallMap.put(1, "one");
        smallMap.put(17, "seventeen");
        smallMap.put(33, "thirty-three");

        assertEquals("one", smallMap.get(1));
        assertEquals("seventeen", smallMap.get(17));
        assertEquals("thirty-three", smallMap.get(33));
        assertEquals(3, smallMap.size());
    }

    @Test
    void testLongLongMap() {
        HashMap<Long, Long> freqMap = new HashMap<>(16);
        freqMap.put(65L, 100L);
        freqMap.put(66L, 200L);

        assertEquals(100L, freqMap.get(65L));
        assertEquals(200L, freqMap.get(66L));
    }

    @Test
    void testGetOrDefaultWithLong() {
        HashMap<Long, Long> freqMap = new HashMap<>(16);
        freqMap.put(65L, 100L);

        assertEquals(100L, freqMap.getOrDefault(65L, 0L));
        assertEquals(0L, freqMap.getOrDefault(66L, 0L));
    }

    @Test
    void testEntrySetIteration() {
        HashMap<Long, Integer> frequencies = new HashMap<>(16);
        frequencies.put(65L, 5);
        frequencies.put(66L, 10);
        frequencies.put(67L, 15);

        int sum = 0;
        for (HashMap.Entry<Long, Integer> entry : frequencies.entrySet()) {
            sum += entry.getValue();
        }

        assertEquals(30, sum);
    }

    @Test
    void testEmptyMap() {
        assertEquals(0, map.size());
        assertNull(map.get("anyKey"));
        assertTrue(map.entrySet().isEmpty());
    }

    @Test
    void testLargeNumberOfElements() {
        HashMap<Long, Integer> largeMap = new HashMap<>(16);
        for (long i = 0; i < 1000; i++) {
            largeMap.put(i, (int) i);
        }

        assertEquals(1000, largeMap.size());

        for (long i = 0; i < 1000; i++) {
            assertEquals((int) i, largeMap.get(i));
        }
    }

    @Test
    void testNegativeHashCodes() {
        HashMap<Integer, String> negativeMap = new HashMap<>(16);
        negativeMap.put(-1, "minus one");
        negativeMap.put(-100, "minus hundred");
        negativeMap.put(Integer.MIN_VALUE, "min value");

        assertEquals("minus one", negativeMap.get(-1));
        assertEquals("minus hundred", negativeMap.get(-100));
        assertEquals("min value", negativeMap.get(Integer.MIN_VALUE));
    }

    @Test
    void testZeroKey() {
        HashMap<Long, Integer> zeroMap = new HashMap<>(16);
        zeroMap.put(0L, 999);

        assertEquals(999, zeroMap.get(0L));
    }

    @Test
    void testMaxLongKey() {
        HashMap<Long, Integer> maxMap = new HashMap<>(16);
        maxMap.put(Long.MAX_VALUE, 123);

        assertEquals(123, maxMap.get(Long.MAX_VALUE));
    }

    @Test
    void testOverwriteMultipleTimes() {
        map.put("key", 1);
        map.put("key", 2);
        map.put("key", 3);
        map.put("key", 4);

        assertEquals(4, map.get("key"));
        assertEquals(1, map.size());
    }

    @Test
    void testEntryKeyAndValue() {
        map.put("testKey", 42);

        List<HashMap.Entry<String, Integer>> entries = map.entrySet();
        assertEquals(1, entries.size());

        HashMap.Entry<String, Integer> entry = entries.get(0);
        assertEquals("testKey", entry.getKey());
        assertEquals(42, entry.getValue());
        assertEquals("testKey", entry.key);
        assertEquals(42, entry.value);
    }
}