package androidx.datastore.preferences.protobuf;

/* JADX INFO: loaded from: classes.dex */
public abstract class u0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final t0 f2570a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final t0 f2571b;

    static {
        t0 t0Var = null;
        try {
            t0Var = (t0) Class.forName("androidx.datastore.preferences.protobuf.NewInstanceSchemaFull").getDeclaredConstructor(null).newInstance(null);
        } catch (Exception unused) {
        }
        f2570a = t0Var;
        f2571b = new t0();
    }
}
