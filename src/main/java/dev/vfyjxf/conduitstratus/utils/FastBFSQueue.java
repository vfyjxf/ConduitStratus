package dev.vfyjxf.conduitstratus.utils;

import java.util.NoSuchElementException;

public class FastBFSQueue {
    private int head;
    private int tail;
    private int[] array;
    private int length;

    public FastBFSQueue(int capacity) {
        this.array = new int[capacity];
        this.length = capacity;
        this.head = 0;
        this.tail = 0;
    }

    private void expand() {
        int newLength = this.array.length << 1;
        final int[] newArray = new int[newLength];
        if (this.head > this.tail) {
            System.arraycopy(this.array, this.head, newArray, 0, length - this.head);
            System.arraycopy(this.array, 0, newArray, length - this.head, this.tail);
        } else {
            System.arraycopy(this.array, this.head, newArray, 0, this.tail - this.head);
        }

        this.head = 0;
        this.tail = length;
        this.array = newArray;
        this.length = newLength;

    }

    public void enqueue(int value) {
        this.array[this.tail++] = value;
        if (this.tail == this.length) {
            this.tail = 0;
        }
        if (this.tail == this.head) {
            this.expand();
        }
    }

    public int dequeue() {
        if (this.head == this.tail) {
            throw new NoSuchElementException();
        }
        int value = this.array[this.head++];
        if (this.head == this.length) {
            this.head = 0;
        }
        return value;
    }

    public boolean notEmpty() {
        return this.head != this.tail;
    }

}
