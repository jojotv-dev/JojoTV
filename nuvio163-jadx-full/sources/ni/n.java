package ni;

/* JADX INFO: loaded from: classes3.dex */
public abstract class n {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final boolean f42814a;

    static {
        boolean z2;
        try {
            Class.forName("java.lang.ClassValue");
            z2 = true;
        } catch (Throwable unused) {
            z2 = false;
        }
        f42814a = z2;
    }
}
