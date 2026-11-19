package com.fasterxml.jackson.databind.util.internal;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* JADX INFO: loaded from: classes3.dex */
public final class PrivateMaxEntriesMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable {
    static final long MAXIMUM_CAPACITY = 9223372034707292160L;
    static final int NCPU;
    static final int NUMBER_OF_READ_BUFFERS;
    static final int READ_BUFFERS_MASK;
    static final int READ_BUFFER_DRAIN_THRESHOLD = 8;
    static final int READ_BUFFER_INDEX_MASK = 15;
    static final int READ_BUFFER_SIZE = 16;
    static final int READ_BUFFER_THRESHOLD = 4;
    static final int WRITE_BUFFER_DRAIN_THRESHOLD = 16;
    static final long serialVersionUID = 1;
    final AtomicLong capacity;
    final int concurrencyLevel;
    final ConcurrentMap<K, Node<K, V>> data;
    final AtomicReference<DrainStatus> drainStatus;
    transient Set<Map.Entry<K, V>> entrySet;
    final LinkedDeque<Node<K, V>> evictionDeque;
    final Lock evictionLock;
    transient Set<K> keySet;
    final AtomicLongArray readBufferDrainAtWriteCount;
    final long[] readBufferReadCount;
    final AtomicLongArray readBufferWriteCount;
    final AtomicReferenceArray<Node<K, V>> readBuffers;
    transient Collection<V> values;
    final AtomicLong weightedSize;
    final Queue<Runnable> writeBuffer;

    public final class AddTask implements Runnable {
        final Node<K, V> node;
        final int weight;

        public AddTask(Node<K, V> node, int i10) {
            this.weight = i10;
            this.node = node;
        }

        @Override // java.lang.Runnable
        public void run() {
            AtomicLong atomicLong = PrivateMaxEntriesMap.this.weightedSize;
            atomicLong.lazySet(atomicLong.get() + ((long) this.weight));
            if (((WeightedValue) this.node.get()).isAlive()) {
                PrivateMaxEntriesMap.this.evictionDeque.add(this.node);
                PrivateMaxEntriesMap.this.evict();
            }
        }
    }

    public static final class Builder<K, V> {
        static final int DEFAULT_CONCURRENCY_LEVEL = 16;
        static final int DEFAULT_INITIAL_CAPACITY = 16;
        long capacity = -1;
        int initialCapacity = 16;
        int concurrencyLevel = 16;

        public PrivateMaxEntriesMap<K, V> build() {
            PrivateMaxEntriesMap.checkState(this.capacity >= 0);
            return new PrivateMaxEntriesMap<>(this);
        }

        public Builder<K, V> concurrencyLevel(int i10) {
            PrivateMaxEntriesMap.checkArgument(i10 > 0);
            this.concurrencyLevel = i10;
            return this;
        }

        public Builder<K, V> initialCapacity(int i10) {
            PrivateMaxEntriesMap.checkArgument(i10 >= 0);
            this.initialCapacity = i10;
            return this;
        }

        public Builder<K, V> maximumCapacity(long j10) {
            PrivateMaxEntriesMap.checkArgument(j10 >= 0);
            this.capacity = j10;
            return this;
        }
    }

    public enum DrainStatus {
        IDLE { // from class: com.fasterxml.jackson.databind.util.internal.PrivateMaxEntriesMap.DrainStatus.1
            @Override // com.fasterxml.jackson.databind.util.internal.PrivateMaxEntriesMap.DrainStatus
            public boolean shouldDrainBuffers(boolean z2) {
                return !z2;
            }
        },
        REQUIRED { // from class: com.fasterxml.jackson.databind.util.internal.PrivateMaxEntriesMap.DrainStatus.2
            @Override // com.fasterxml.jackson.databind.util.internal.PrivateMaxEntriesMap.DrainStatus
            public boolean shouldDrainBuffers(boolean z2) {
                return true;
            }
        },
        PROCESSING { // from class: com.fasterxml.jackson.databind.util.internal.PrivateMaxEntriesMap.DrainStatus.3
            @Override // com.fasterxml.jackson.databind.util.internal.PrivateMaxEntriesMap.DrainStatus
            public boolean shouldDrainBuffers(boolean z2) {
                return false;
            }
        };

        public abstract boolean shouldDrainBuffers(boolean z2);
    }

    public final class EntryIterator implements Iterator<Map.Entry<K, V>> {
        Node<K, V> current;
        final Iterator<Node<K, V>> iterator;

        public EntryIterator() {
            this.iterator = PrivateMaxEntriesMap.this.data.values().iterator();
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override // java.util.Iterator
        public void remove() {
            PrivateMaxEntriesMap.checkState(this.current != null);
            PrivateMaxEntriesMap.this.remove(this.current.key);
            this.current = null;
        }

        @Override // java.util.Iterator
        public Map.Entry<K, V> next() {
            this.current = this.iterator.next();
            return new WriteThroughEntry(this.current);
        }
    }

    public final class EntrySet extends AbstractSet<Map.Entry<K, V>> {
        final PrivateMaxEntriesMap<K, V> map;

        public EntrySet() {
            this.map = PrivateMaxEntriesMap.this;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public void clear() {
            this.map.clear();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry entry = (Map.Entry) obj;
            Node<K, V> node = this.map.data.get(entry.getKey());
            return node != null && node.getValue().equals(entry.getValue());
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean remove(Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry entry = (Map.Entry) obj;
            return this.map.remove(entry.getKey(), entry.getValue());
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return this.map.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean add(Map.Entry<K, V> entry) {
            throw new UnsupportedOperationException("ConcurrentLinkedHashMap does not allow add to be called on entrySet()");
        }
    }

    public final class KeyIterator implements Iterator<K> {
        K current;
        final Iterator<K> iterator;

        public KeyIterator() {
            this.iterator = PrivateMaxEntriesMap.this.data.keySet().iterator();
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override // java.util.Iterator
        public K next() {
            K next = this.iterator.next();
            this.current = next;
            return next;
        }

        @Override // java.util.Iterator
        public void remove() {
            PrivateMaxEntriesMap.checkState(this.current != null);
            PrivateMaxEntriesMap.this.remove(this.current);
            this.current = null;
        }
    }

    public final class KeySet extends AbstractSet<K> {
        final PrivateMaxEntriesMap<K, V> map;

        public KeySet() {
            this.map = PrivateMaxEntriesMap.this;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public void clear() {
            this.map.clear();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(Object obj) {
            return PrivateMaxEntriesMap.this.containsKey(obj);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<K> iterator() {
            return new KeyIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean remove(Object obj) {
            return this.map.remove(obj) != null;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return this.map.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public Object[] toArray() {
            return this.map.data.keySet().toArray();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public <T> T[] toArray(T[] tArr) {
            return (T[]) this.map.data.keySet().toArray(tArr);
        }
    }

    public static final class Node<K, V> extends AtomicReference<WeightedValue<V>> implements Linked<Node<K, V>> {
        final K key;
        Node<K, V> next;
        Node<K, V> prev;

        public Node(K k, WeightedValue<V> weightedValue) {
            super(weightedValue);
            this.key = k;
        }

        public V getValue() {
            return ((WeightedValue) get()).value;
        }

        @Override // com.fasterxml.jackson.databind.util.internal.Linked
        public Node<K, V> getNext() {
            return this.next;
        }

        @Override // com.fasterxml.jackson.databind.util.internal.Linked
        public Node<K, V> getPrevious() {
            return this.prev;
        }

        @Override // com.fasterxml.jackson.databind.util.internal.Linked
        public void setNext(Node<K, V> node) {
            this.next = node;
        }

        @Override // com.fasterxml.jackson.databind.util.internal.Linked
        public void setPrevious(Node<K, V> node) {
            this.prev = node;
        }
    }

    public final class RemovalTask implements Runnable {
        final Node<K, V> node;

        public RemovalTask(Node<K, V> node) {
            this.node = node;
        }

        @Override // java.lang.Runnable
        public void run() {
            PrivateMaxEntriesMap.this.evictionDeque.remove(this.node);
            PrivateMaxEntriesMap.this.makeDead(this.node);
        }
    }

    public static final class SerializationProxy<K, V> implements Serializable {
        static final long serialVersionUID = 1;
        final long capacity;
        final int concurrencyLevel;
        final Map<K, V> data;

        public SerializationProxy(PrivateMaxEntriesMap<K, V> privateMaxEntriesMap) {
            this.concurrencyLevel = privateMaxEntriesMap.concurrencyLevel;
            this.data = new HashMap(privateMaxEntriesMap);
            this.capacity = privateMaxEntriesMap.capacity.get();
        }

        public Object readResolve() {
            PrivateMaxEntriesMap<K, V> privateMaxEntriesMapBuild = new Builder().maximumCapacity(this.capacity).build();
            privateMaxEntriesMapBuild.putAll(this.data);
            return privateMaxEntriesMapBuild;
        }
    }

    public final class UpdateTask implements Runnable {
        final Node<K, V> node;
        final int weightDifference;

        public UpdateTask(Node<K, V> node, int i10) {
            this.weightDifference = i10;
            this.node = node;
        }

        @Override // java.lang.Runnable
        public void run() {
            AtomicLong atomicLong = PrivateMaxEntriesMap.this.weightedSize;
            atomicLong.lazySet(atomicLong.get() + ((long) this.weightDifference));
            PrivateMaxEntriesMap.this.applyRead(this.node);
            PrivateMaxEntriesMap.this.evict();
        }
    }

    public final class ValueIterator implements Iterator<V> {
        Node<K, V> current;
        final Iterator<Node<K, V>> iterator;

        public ValueIterator() {
            this.iterator = PrivateMaxEntriesMap.this.data.values().iterator();
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override // java.util.Iterator
        public V next() {
            Node<K, V> next = this.iterator.next();
            this.current = next;
            return next.getValue();
        }

        @Override // java.util.Iterator
        public void remove() {
            PrivateMaxEntriesMap.checkState(this.current != null);
            PrivateMaxEntriesMap.this.remove(this.current.key);
            this.current = null;
        }
    }

    public final class Values extends AbstractCollection<V> {
        public Values() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            PrivateMaxEntriesMap.this.clear();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object obj) {
            return PrivateMaxEntriesMap.this.containsValue(obj);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<V> iterator() {
            return new ValueIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public int size() {
            return PrivateMaxEntriesMap.this.size();
        }
    }

    public static final class WeightedValue<V> {
        final V value;
        final int weight;

        public WeightedValue(V v10, int i10) {
            this.weight = i10;
            this.value = v10;
        }

        public boolean contains(Object obj) {
            V v10 = this.value;
            return obj == v10 || v10.equals(obj);
        }

        public boolean isAlive() {
            return this.weight > 0;
        }
    }

    public final class WriteThroughEntry extends AbstractMap.SimpleEntry<K, V> {
        static final long serialVersionUID = 1;

        public WriteThroughEntry(Node<K, V> node) {
            super(node.key, node.getValue());
        }

        @Override // java.util.AbstractMap.SimpleEntry, java.util.Map.Entry
        public V setValue(V v10) {
            PrivateMaxEntriesMap.this.put(getKey(), v10);
            return (V) super.setValue(v10);
        }

        public Object writeReplace() {
            return new AbstractMap.SimpleEntry(this);
        }
    }

    static {
        int iAvailableProcessors = Runtime.getRuntime().availableProcessors();
        NCPU = iAvailableProcessors;
        int iMin = Math.min(4, ceilingNextPowerOfTwo(iAvailableProcessors));
        NUMBER_OF_READ_BUFFERS = iMin;
        READ_BUFFERS_MASK = iMin - 1;
    }

    public PrivateMaxEntriesMap(Builder<K, V> builder) {
        int i10 = builder.concurrencyLevel;
        this.concurrencyLevel = i10;
        this.capacity = new AtomicLong(Math.min(builder.capacity, MAXIMUM_CAPACITY));
        this.data = new ConcurrentHashMap(builder.initialCapacity, 0.75f, i10);
        this.evictionLock = new ReentrantLock();
        this.weightedSize = new AtomicLong();
        this.evictionDeque = new LinkedDeque<>();
        this.writeBuffer = new ConcurrentLinkedQueue();
        this.drainStatus = new AtomicReference<>(DrainStatus.IDLE);
        int i11 = NUMBER_OF_READ_BUFFERS;
        this.readBufferReadCount = new long[i11];
        this.readBufferWriteCount = new AtomicLongArray(i11);
        this.readBufferDrainAtWriteCount = new AtomicLongArray(i11);
        this.readBuffers = new AtomicReferenceArray<>(i11 * 16);
    }

    public static int ceilingNextPowerOfTwo(int i10) {
        return 1 << (32 - Integer.numberOfLeadingZeros(i10 - 1));
    }

    public static void checkArgument(boolean z2) {
        if (!z2) {
            throw new IllegalArgumentException();
        }
    }

    public static void checkNotNull(Object obj) {
        obj.getClass();
    }

    public static void checkState(boolean z2) {
        if (!z2) {
            throw new IllegalStateException();
        }
    }

    private static int readBufferIndex(int i10, int i11) {
        return (i10 * 16) + i11;
    }

    private void readObject(ObjectInputStream objectInputStream) throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required");
    }

    public void afterRead(Node<K, V> node) {
        int bufferIndex = readBufferIndex();
        drainOnReadIfNeeded(bufferIndex, recordRead(bufferIndex, node));
    }

    public void afterWrite(Runnable runnable) {
        this.writeBuffer.add(runnable);
        this.drainStatus.lazySet(DrainStatus.REQUIRED);
        tryToDrainBuffers();
    }

    public void applyRead(Node<K, V> node) {
        if (this.evictionDeque.contains((Linked<?>) node)) {
            this.evictionDeque.moveToBack(node);
        }
    }

    public long capacity() {
        return this.capacity.get();
    }

    @Override // java.util.AbstractMap, java.util.Map
    public void clear() {
        this.evictionLock.lock();
        while (true) {
            try {
                Node<K, V> node = (Node) this.evictionDeque.poll();
                if (node == null) {
                    break;
                }
                this.data.remove(node.key, node);
                makeDead(node);
            } catch (Throwable th2) {
                this.evictionLock.unlock();
                throw th2;
            }
        }
        for (int i10 = 0; i10 < this.readBuffers.length(); i10++) {
            this.readBuffers.lazySet(i10, null);
        }
        while (true) {
            Runnable runnablePoll = this.writeBuffer.poll();
            if (runnablePoll == null) {
                this.evictionLock.unlock();
                return;
            }
            runnablePoll.run();
        }
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean containsKey(Object obj) {
        return this.data.containsKey(obj);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean containsValue(Object obj) {
        checkNotNull(obj);
        Iterator<Node<K, V>> it = this.data.values().iterator();
        while (it.hasNext()) {
            if (it.next().getValue().equals(obj)) {
                return true;
            }
        }
        return false;
    }

    public void drainBuffers() {
        drainReadBuffers();
        drainWriteBuffer();
    }

    public void drainOnReadIfNeeded(int i10, long j10) {
        if (this.drainStatus.get().shouldDrainBuffers(j10 - this.readBufferDrainAtWriteCount.get(i10) < 4)) {
            tryToDrainBuffers();
        }
    }

    public void drainReadBuffer(int i10) {
        int bufferIndex;
        Node<K, V> node;
        long j10 = this.readBufferWriteCount.get(i10);
        for (int i11 = 0; i11 < 8 && (node = this.readBuffers.get((bufferIndex = readBufferIndex(i10, (int) (this.readBufferReadCount[i10] & 15))))) != null; i11++) {
            this.readBuffers.lazySet(bufferIndex, null);
            applyRead(node);
            long[] jArr = this.readBufferReadCount;
            jArr[i10] = jArr[i10] + 1;
        }
        this.readBufferDrainAtWriteCount.lazySet(i10, j10);
    }

    public void drainReadBuffers() {
        int id2 = (int) Thread.currentThread().getId();
        int i10 = NUMBER_OF_READ_BUFFERS + id2;
        while (id2 < i10) {
            drainReadBuffer(READ_BUFFERS_MASK & id2);
            id2++;
        }
    }

    public void drainWriteBuffer() {
        Runnable runnablePoll;
        for (int i10 = 0; i10 < 16 && (runnablePoll = this.writeBuffer.poll()) != null; i10++) {
            runnablePoll.run();
        }
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> set = this.entrySet;
        if (set != null) {
            return set;
        }
        EntrySet entrySet = new EntrySet();
        this.entrySet = entrySet;
        return entrySet;
    }

    public void evict() {
        Node<K, V> node;
        while (hasOverflowed() && (node = (Node) this.evictionDeque.poll()) != null) {
            this.data.remove(node.key, node);
            makeDead(node);
        }
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V get(Object obj) {
        Node<K, V> node = this.data.get(obj);
        if (node == null) {
            return null;
        }
        afterRead(node);
        return node.getValue();
    }

    public boolean hasOverflowed() {
        return this.weightedSize.get() > this.capacity.get();
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Set<K> keySet() {
        Set<K> set = this.keySet;
        if (set != null) {
            return set;
        }
        KeySet keySet = new KeySet();
        this.keySet = keySet;
        return keySet;
    }

    public void makeDead(Node<K, V> node) {
        WeightedValue weightedValue;
        do {
            weightedValue = (WeightedValue) node.get();
        } while (!node.compareAndSet(weightedValue, new WeightedValue(weightedValue.value, 0)));
        AtomicLong atomicLong = this.weightedSize;
        atomicLong.lazySet(atomicLong.get() - ((long) Math.abs(weightedValue.weight)));
    }

    public void makeRetired(Node<K, V> node) {
        WeightedValue weightedValue;
        do {
            weightedValue = (WeightedValue) node.get();
            if (!weightedValue.isAlive()) {
                return;
            }
        } while (!node.compareAndSet(weightedValue, new WeightedValue(weightedValue.value, -weightedValue.weight)));
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V put(K k, V v10) {
        return put(k, v10, false);
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public V putIfAbsent(K k, V v10) {
        return put(k, v10, true);
    }

    public long recordRead(int i10, Node<K, V> node) {
        long j10 = this.readBufferWriteCount.get(i10);
        this.readBufferWriteCount.lazySet(i10, 1 + j10);
        this.readBuffers.lazySet(readBufferIndex(i10, (int) (15 & j10)), node);
        return j10;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V remove(Object obj) {
        Node<K, V> nodeRemove = this.data.remove(obj);
        if (nodeRemove == null) {
            return null;
        }
        makeRetired(nodeRemove);
        afterWrite(new RemovalTask(nodeRemove));
        return nodeRemove.getValue();
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public V replace(K k, V v10) {
        WeightedValue weightedValue;
        checkNotNull(k);
        checkNotNull(v10);
        WeightedValue weightedValue2 = new WeightedValue(v10, 1);
        Node<K, V> node = this.data.get(k);
        if (node == null) {
            return null;
        }
        do {
            weightedValue = (WeightedValue) node.get();
            if (!weightedValue.isAlive()) {
                return null;
            }
        } while (!node.compareAndSet(weightedValue, weightedValue2));
        int i10 = 1 - weightedValue.weight;
        if (i10 == 0) {
            afterRead(node);
        } else {
            afterWrite(new UpdateTask(node, i10));
        }
        return weightedValue.value;
    }

    public void setCapacity(long j10) {
        checkArgument(j10 >= 0);
        this.evictionLock.lock();
        try {
            this.capacity.lazySet(Math.min(j10, MAXIMUM_CAPACITY));
            drainBuffers();
            evict();
        } finally {
            this.evictionLock.unlock();
        }
    }

    @Override // java.util.AbstractMap, java.util.Map
    public int size() {
        return this.data.size();
    }

    public void tryToDrainBuffers() {
        if (this.evictionLock.tryLock()) {
            try {
                AtomicReference<DrainStatus> atomicReference = this.drainStatus;
                DrainStatus drainStatus = DrainStatus.PROCESSING;
                atomicReference.lazySet(drainStatus);
                drainBuffers();
                AtomicReference<DrainStatus> atomicReference2 = this.drainStatus;
                DrainStatus drainStatus2 = DrainStatus.IDLE;
                while (!atomicReference2.compareAndSet(drainStatus, drainStatus2) && atomicReference2.get() == drainStatus) {
                }
            } finally {
                AtomicReference<DrainStatus> atomicReference3 = this.drainStatus;
                DrainStatus drainStatus3 = DrainStatus.PROCESSING;
                DrainStatus drainStatus4 = DrainStatus.IDLE;
                while (!atomicReference3.compareAndSet(drainStatus3, drainStatus4) && atomicReference3.get() == drainStatus3) {
                }
                this.evictionLock.unlock();
            }
        }
    }

    public boolean tryToRetire(Node<K, V> node, WeightedValue<V> weightedValue) {
        if (weightedValue.isAlive()) {
            return node.compareAndSet(weightedValue, new WeightedValue(weightedValue.value, -weightedValue.weight));
        }
        return false;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Collection<V> values() {
        Collection<V> collection = this.values;
        if (collection != null) {
            return collection;
        }
        Values values = new Values();
        this.values = values;
        return values;
    }

    public Object writeReplace() {
        return new SerializationProxy(this);
    }

    public static int readBufferIndex() {
        return ((int) Thread.currentThread().getId()) & READ_BUFFERS_MASK;
    }

    public V put(K k, V v10, boolean z2) {
        checkNotNull(k);
        checkNotNull(v10);
        WeightedValue weightedValue = new WeightedValue(v10, 1);
        Node<K, V> node = new Node<>(k, weightedValue);
        while (true) {
            Node<K, V> nodePutIfAbsent = this.data.putIfAbsent(node.key, node);
            if (nodePutIfAbsent == null) {
                afterWrite(new AddTask(node, 1));
                return null;
            }
            if (z2) {
                afterRead(nodePutIfAbsent);
                return nodePutIfAbsent.getValue();
            }
            while (true) {
                WeightedValue weightedValue2 = (WeightedValue) nodePutIfAbsent.get();
                if (!weightedValue2.isAlive()) {
                    break;
                }
                if (nodePutIfAbsent.compareAndSet(weightedValue2, weightedValue)) {
                    int i10 = 1 - weightedValue2.weight;
                    if (i10 == 0) {
                        afterRead(nodePutIfAbsent);
                    } else {
                        afterWrite(new UpdateTask(nodePutIfAbsent, i10));
                    }
                    return weightedValue2.value;
                }
            }
        }
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public boolean remove(Object obj, Object obj2) {
        Node<K, V> node = this.data.get(obj);
        if (node != null && obj2 != null) {
            WeightedValue<V> weightedValue = (WeightedValue) node.get();
            while (weightedValue.contains(obj2)) {
                if (tryToRetire(node, weightedValue)) {
                    if (!this.data.remove(obj, node)) {
                        break;
                    }
                    afterWrite(new RemovalTask(node));
                    return true;
                }
                weightedValue = (WeightedValue) node.get();
                if (!weightedValue.isAlive()) {
                    break;
                }
            }
        }
        return false;
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public boolean replace(K k, V v10, V v11) {
        WeightedValue weightedValue;
        checkNotNull(k);
        checkNotNull(v10);
        checkNotNull(v11);
        WeightedValue weightedValue2 = new WeightedValue(v11, 1);
        Node<K, V> node = this.data.get(k);
        if (node == null) {
            return false;
        }
        do {
            weightedValue = (WeightedValue) node.get();
            if (!weightedValue.isAlive() || !weightedValue.contains(v10)) {
                return false;
            }
        } while (!node.compareAndSet(weightedValue, weightedValue2));
        int i10 = 1 - weightedValue.weight;
        if (i10 == 0) {
            afterRead(node);
        } else {
            afterWrite(new UpdateTask(node, i10));
        }
        return true;
    }
}
