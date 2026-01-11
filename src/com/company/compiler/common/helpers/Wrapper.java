package com.company.compiler.common.helpers;

public abstract class Wrapper<T> {
    private final Object object;
    private final T wrapped;

    public Wrapper(Object object, T wrapped) {
        this.object = object;
        this.wrapped = wrapped;
    }

    public Object getObject() {
        return object;
    }

    public T getWrapped() {
        return wrapped;
    }
}
