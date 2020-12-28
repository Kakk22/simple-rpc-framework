package com.cyf.extension;

/**
 * @author 陈一锋
 * @date 2020/12/28 19:04
 **/
public class Holder<T> {
    private volatile T value;

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
