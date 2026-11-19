package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.databind.util.internal.PrivateMaxEntriesMap;
import java.io.Serializable;
import java.util.Map;
import java.util.function.BiConsumer;

/* JADX INFO: loaded from: classes3.dex */
public class LRUMap<K, V> implements LookupCache<K, V>, Serializable {
    private static final long serialVersionUID = 2;
    protected final int _initialEntries;
    protected final transient PrivateMaxEntriesMap<K, V> _map;
    protected final int _maxEntries;

    public LRUMap(int i10, int i11) {
        this._initialEntries = i10;
        this._maxEntries = i11;
        this._map = new PrivateMaxEntriesMap.Builder().initialCapacity(i10).maximumCapacity(i11).concurrencyLevel(4).build();
    }

    @Override // com.fasterxml.jackson.databind.util.LookupCache
    public void clear() {
        this._map.clear();
    }

    @Override // com.fasterxml.jackson.databind.util.LookupCache
    public void contents(BiConsumer<K, V> biConsumer) {
        for (Map.Entry<K, V> entry : this._map.entrySet()) {
            biConsumer.accept(entry.getKey(), entry.getValue());
        }
    }

    @Override // com.fasterxml.jackson.databind.util.LookupCache
    public LookupCache<K, V> emptyCopy() {
        return new LRUMap(this._initialEntries, this._maxEntries);
    }

    @Override // com.fasterxml.jackson.databind.util.LookupCache
    public V get(Object obj) {
        return this._map.get(obj);
    }

    @Override // com.fasterxml.jackson.databind.util.LookupCache
    public V put(K k, V v10) {
        return this._map.put(k, v10);
    }

    @Override // com.fasterxml.jackson.databind.util.LookupCache
    public V putIfAbsent(K k, V v10) {
        return this._map.putIfAbsent(k, v10);
    }

    public Object readResolve() {
        return new LRUMap(this._initialEntries, this._maxEntries);
    }

    @Override // com.fasterxml.jackson.databind.util.LookupCache
    public int size() {
        return this._map.size();
    }
}
