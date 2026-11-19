package com.fasterxml.jackson.core.util;

import java.util.concurrent.ConcurrentHashMap;

/* JADX INFO: loaded from: classes3.dex */
public final class InternCache extends ConcurrentHashMap<String, String> {
    private static final int MAX_ENTRIES = 180;
    public static final InternCache instance = new InternCache();
    private static final long serialVersionUID = 1;
    private final Object lock;

    public InternCache() {
        this(MAX_ENTRIES, 0.8f, 4);
    }

    public String intern(String str) {
        String str2 = get(str);
        if (str2 != null) {
            return str2;
        }
        if (size() >= MAX_ENTRIES) {
            synchronized (this.lock) {
                try {
                    if (size() >= MAX_ENTRIES) {
                        clear();
                    }
                } catch (Throwable th2) {
                    throw th2;
                }
            }
        }
        String strIntern = str.intern();
        put(strIntern, strIntern);
        return strIntern;
    }

    public InternCache(int i10, float f10, int i11) {
        super(i10, f10, i11);
        this.lock = new Object();
    }
}
