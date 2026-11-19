package rh;

/* JADX INFO: loaded from: classes5.dex */
public abstract class c {
    static {
        new b("No further exceptions");
    }

    public static RuntimeException a(Throwable th2) {
        if (th2 instanceof Error) {
            throw ((Error) th2);
        }
        return th2 instanceof RuntimeException ? (RuntimeException) th2 : new RuntimeException(th2);
    }
}
