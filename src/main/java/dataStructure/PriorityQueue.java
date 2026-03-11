package dataStructure;

import exception.PriorityQueueEmptyHeapException;
import exception.PriorityQueueNullElementException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PriorityQueue<T> {
    private final List<T> heap;
    private final Comparator<T> comparator;

    public PriorityQueue(Comparator<T> comparator) {
        this.heap = new ArrayList<>();
        this.comparator = comparator;
    }

    public PriorityQueue() {
        this((a, b) -> ((Comparable<T>) a).compareTo(b));
    }

    public void insert(T element) {
        if (element == null) {
            throw new PriorityQueueNullElementException("Element cannot be null");
        }
        heap.add(element);
        heapifyUp(heap.size() - 1);
    }

    public T extractMin() {
        if (heap.isEmpty()) {
            throw new PriorityQueueEmptyHeapException("Element does not exist");
        }

        T minElement = heap.getFirst();
        T lastElement = heap.removeLast();

        if (!heap.isEmpty()) {
            heap.set(0, lastElement);
            heapifyDown(0);
        }

        return minElement;
    }

    public int size() {
        return heap.size();
    }

    private void heapifyUp(int index) {
        while (index > 0) {
            int parentIndex = getParent(index);

            if (compare(index, parentIndex) >= 0) {
                break;
            }

            swap(index, parentIndex);
            index = parentIndex;
        }
    }

    private void heapifyDown(int index) {
        int size = heap.size();

        while (true) {
            int leftChildIndex = getLeftChild(index);
            int rightChildIndex = getRightChild(index);
            int smallestIndex = index;

            if (leftChildIndex < size && compare(leftChildIndex, smallestIndex) < 0) {
                smallestIndex = leftChildIndex;
            }

            if (rightChildIndex < size && compare(rightChildIndex, smallestIndex) < 0) {
                smallestIndex = rightChildIndex;
            }

            if (smallestIndex == index) {
                break;
            }

            swap(index, smallestIndex);
            index = smallestIndex;
        }
    }

    private void swap(int i, int j) {
        T temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

    private int getParent(int index) {
        return (index - 1) / 2;
    }

    private int getLeftChild(int index) {
        return 2 * index + 1;
    }

    private int getRightChild(int index) {
        return 2 * index + 2;
    }

    private int compare(int i, int j) {
        return comparator.compare(heap.get(i), heap.get(j));
    }

}
