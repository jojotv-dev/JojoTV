package rc;

import java.util.concurrent.atomic.AtomicLong;

/* JADX INFO: loaded from: classes.dex */
public abstract class h {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final AtomicLong f51582a = new AtomicLong(0);

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static volatile Integer f51583b;

    public static void a() {
        f51582a.set(0L);
        f51583b = null;
    }
}
