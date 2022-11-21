package com.acme.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author ：wk
 * @date ：Created in 2022/10/6 8:28 下午
 * @description：通用建造者模式构造器
 */
public class GenericBuilder<T> {
    private final Supplier<T> supplier;

    private final List<Consumer<T>> consumers = new ArrayList<>();

    public GenericBuilder(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public static <T> GenericBuilder<T> of(Supplier<T> supplier) {
        return new GenericBuilder<>(supplier);
    }

    public <U> GenericBuilder<T> with(BiConsumer<T, U> consumer, U value) {
        Consumer<T> c = param -> consumer.accept(param, value);
        consumers.add(c);
        return this;
    }
    public T build() {
        T instance = supplier.get();
        consumers.forEach(consumer -> consumer.accept(instance));
        consumers.clear();
        return instance;
    }
}
