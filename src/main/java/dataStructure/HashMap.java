package dataStructure;

import java.util.ArrayList;
import java.util.List;

public class HashMap<K, V> {
    private K[] keys;
    private V[] values;
    private boolean[] occupied;
    private int capacity;
    private int size;

    @SuppressWarnings("unchecked")
    public HashMap(int initialCapacity) {
        this.capacity = initialCapacity;
        this.keys = (K[]) new Object[capacity];
        this.values = (V[]) new Object[capacity];
        this.occupied = new boolean[capacity];
        this.size = 0;
    }

    public void put(K key, V value) {
        if (size >= capacity * 0.75f) resize();
        int index = findSlot(key);
        if (!occupied[index]) {
            keys[index] = key;
            occupied[index] = true;
            size++;
        }
        values[index] = value;
    }

    public V get(K key) {
        int index = findSlot(key);
        return occupied[index] ? values[index] : null;
    }

    public V getOrDefault(K key, V defaultValue) {
        V val = get(key);
        return (val != null) ? val : defaultValue;
    }

    private int findSlot(K key) {
        int hash = (key.hashCode() & 0x7FFFFFFF);
        int index = hash % capacity;
        while (occupied[index]) {
            if (keys[index].equals(key)) return index;
            index = (index + 1) % capacity;
        }
        return index;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        K[] oldKeys = keys;
        V[] oldValues = values;
        boolean[] oldOccupied = occupied;
        capacity *= 2;
        keys = (K[]) new Object[capacity];
        values = (V[]) new Object[capacity];
        occupied = new boolean[capacity];
        size = 0;
        for (int i = 0; i < oldOccupied.length; i++) {
            if (oldOccupied[i]) put(oldKeys[i], oldValues[i]);
        }
    }

    public List<Entry<K, V>> entrySet() {
        List<Entry<K, V>> entries = new ArrayList<>(size);
        for (int i = 0; i < capacity; i++) {
            if (occupied[i]) entries.add(new Entry<>(keys[i], values[i]));
        }
        return entries;
    }

    public int size() { return size; }

    public static class Entry<K, V> {
        public final K key;
        public final V value;
        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
        public K getKey() { return key; }
        public V getValue() { return value; }
    }
}