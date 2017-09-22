package me.bramhaag.bcf.util;

public class Triple<T, T1, T2> {

    private T left;
    private T1 middle;
    private T2 right;

    public Triple(T left, T1 middle, T2 right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

    public T getLeft() {
        return left;
    }

    public T1 getMiddle() {
        return middle;
    }

    public T2 getRight() {
        return right;
    }
}
