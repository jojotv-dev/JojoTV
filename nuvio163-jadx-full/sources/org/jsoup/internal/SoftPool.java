package org.jsoup.internal;

import java.lang.ref.SoftReference;
import java.util.ArrayDeque;
import java.util.function.Supplier;

/* JADX INFO: loaded from: classes5.dex */
public class SoftPool<T> {
    static final int MaxIdle = 12;
    private final Supplier<T> initializer;
    final ThreadLocal<SoftReference<ArrayDeque<T>>> threadLocalStack = new ad.b(new e(1), 1);

    public SoftPool(Supplier<T> supplier) {
        this.initializer = supplier;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ SoftReference lambda$new$0() {
        return new SoftReference(new ArrayDeque());
    }

    public T borrow() {
        ArrayDeque<T> stack = getStack();
        return !stack.isEmpty() ? stack.pop() : this.initializer.get();
    }

    public ArrayDeque<T> getStack() {
        ArrayDeque<T> arrayDeque = this.threadLocalStack.get().get();
        if (arrayDeque != null) {
            return arrayDeque;
        }
        ArrayDeque<T> arrayDeque2 = new ArrayDeque<>();
        this.threadLocalStack.set(new SoftReference<>(arrayDeque2));
        return arrayDeque2;
    }

    public void release(T t10) {
        ArrayDeque<T> stack = getStack();
        if (stack.size() < 12) {
            stack.push(t10);
        }
    }
}
