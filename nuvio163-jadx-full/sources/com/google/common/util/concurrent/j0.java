package com.google.common.util.concurrent;

import java.util.logging.Logger;

/* JADX INFO: loaded from: classes.dex */
public final class j0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Object f7547a = new Object();

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f7548b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public volatile Logger f7549c;

    public j0(Class cls) {
        this.f7548b = cls.getName();
    }

    public final Logger a() {
        Logger logger = this.f7549c;
        if (logger != null) {
            return logger;
        }
        synchronized (this.f7547a) {
            try {
                Logger logger2 = this.f7549c;
                if (logger2 != null) {
                    return logger2;
                }
                Logger logger3 = Logger.getLogger(this.f7548b);
                this.f7549c = logger3;
                return logger3;
            } catch (Throwable th2) {
                throw th2;
            }
        }
    }
}
