package com.fanpics.opensource.android.modelrecord.event;

public class SuccessEvent<T> {
    protected T result;
    private boolean resultLoaded = false;
    private boolean hasFinished;

    public SuccessEvent<T> setResult(T result) {
        this.result = result;

        if (!resultLoaded) {
            resultLoaded = result != null;
        }

        return this;
    }

    public T getResult() {
        return result;
    }

    public boolean resultHasLoaded() {
        return resultLoaded;
    }

    public void clearResult() {
        result = null;
    }

    public void setHasFinished(boolean hasFinished) {
        this.hasFinished = hasFinished;
    }

    public boolean hasFinished() {
        return hasFinished;
    }
}
