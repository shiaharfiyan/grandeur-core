package io.github.shiaharfiyan.utils.helpers;

public final class Result<T> {
    private boolean succeeded;
    private T value;

    public boolean IsSucceeded() {
        return succeeded;
    }

    void SetSucceeded(boolean succeeded) {
        this.succeeded = succeeded;
    }

    public T GetValue() {
        return value;
    }

    void SetValue(T value) {
        this.value = value;
    }
}