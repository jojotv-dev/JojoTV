package com.fasterxml.jackson.databind.util;

import java.util.function.BiConsumer;

/* JADX INFO: loaded from: classes3.dex */
public interface LookupCache<K, V> {
    void clear();

    default void contents(BiConsumer<K, V> biConsumer) {
        throw new UnsupportedOperationException();
    }

    default LookupCache<K, V> emptyCopy() {
        throw new UnsupportedOperationException("LookupCache implementation " + getClass().getName() + " does not implement `emptyCopy()`");
    }

    V get(Object obj);

    V put(K k, V v10);

    V putIfAbsent(K k, V v10);

    int size();
}
