package t2;

/* JADX INFO: loaded from: classes.dex */
public abstract class a {
    public static final void a(String str) {
        throw new IllegalArgumentException(str);
    }

    public static final void b(String str) {
        throw new IllegalStateException(str);
    }

    public static final Void c(String str) {
        throw new IllegalStateException(str);
    }

    public static final void d(String str) {
        throw new IndexOutOfBoundsException(str);
    }
}
