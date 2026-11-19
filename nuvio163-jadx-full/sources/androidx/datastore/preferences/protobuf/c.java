package androidx.datastore.preferences.protobuf;

/* JADX INFO: loaded from: classes.dex */
public abstract class c {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final Class f2420a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final boolean f2421b;

    static {
        Class<?> cls;
        Class<?> cls2 = null;
        try {
            cls = Class.forName("libcore.io.Memory");
        } catch (Throwable unused) {
            cls = null;
        }
        f2420a = cls;
        try {
            cls2 = Class.forName("org.robolectric.Robolectric");
        } catch (Throwable unused2) {
        }
        f2421b = cls2 != null;
    }

    public static boolean a() {
        return (f2420a == null || f2421b) ? false : true;
    }
}
