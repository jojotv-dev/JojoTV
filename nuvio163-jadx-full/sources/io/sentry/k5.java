package io.sentry;

import java.util.concurrent.CopyOnWriteArraySet;

/* JADX INFO: loaded from: classes.dex */
public final class k5 {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static volatile k5 f25689c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final io.sentry.util.a f25690d = new io.sentry.util.a();

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static volatile Boolean f25691e = null;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public static final io.sentry.util.a f25692f = new io.sentry.util.a();

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final CopyOnWriteArraySet f25693a = new CopyOnWriteArraySet();

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final CopyOnWriteArraySet f25694b = new CopyOnWriteArraySet();

    public static k5 d() {
        if (f25689c == null) {
            io.sentry.util.a aVar = f25690d;
            aVar.g();
            try {
                if (f25689c == null) {
                    f25689c = new k5();
                }
                aVar.close();
            } catch (Throwable th2) {
                try {
                    aVar.close();
                } catch (Throwable th3) {
                    th2.addSuppressed(th3);
                }
                throw th2;
            }
        }
        return f25689c;
    }

    public final void a(String str) {
        io.sentry.util.b.p(str, "integration is required.");
        this.f25693a.add(str);
    }

    public final void b(String str, String str2) {
        this.f25694b.add(new io.sentry.protocol.x(str, str2));
        io.sentry.util.a aVar = f25692f;
        aVar.g();
        try {
            f25691e = null;
            aVar.close();
        } catch (Throwable th2) {
            try {
                aVar.close();
            } catch (Throwable th3) {
                th2.addSuppressed(th3);
            }
            throw th2;
        }
    }

    public final boolean c(ILogger iLogger) {
        Boolean bool = f25691e;
        if (bool != null) {
            return bool.booleanValue();
        }
        io.sentry.util.a aVar = f25692f;
        aVar.g();
        try {
            boolean z2 = false;
            for (io.sentry.protocol.x xVar : this.f25694b) {
                if (xVar.f26036b.startsWith("maven:io.sentry:") && !"8.47.0".equalsIgnoreCase(xVar.f26037c)) {
                    iLogger.k(m5.ERROR, "The Sentry SDK has been configured with mixed versions. Expected %s to match core SDK version %s but was %s", xVar.f26036b, "8.47.0", xVar.f26037c);
                    z2 = true;
                }
            }
            if (z2) {
                m5 m5Var = m5.ERROR;
                iLogger.k(m5Var, "^^^^^^^^^^^^^^^^^^^^^^^^^^^^", new Object[0]);
                iLogger.k(m5Var, "^^^^^^^^^^^^^^^^^^^^^^^^^^^^", new Object[0]);
                iLogger.k(m5Var, "^^^^^^^^^^^^^^^^^^^^^^^^^^^^", new Object[0]);
                iLogger.k(m5Var, "^^^^^^^^^^^^^^^^^^^^^^^^^^^^", new Object[0]);
            }
            f25691e = Boolean.valueOf(z2);
            aVar.close();
            return z2;
        } catch (Throwable th2) {
            try {
                aVar.close();
            } catch (Throwable th3) {
                th2.addSuppressed(th3);
            }
            throw th2;
        }
    }
}
