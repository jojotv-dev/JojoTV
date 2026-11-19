package androidx.datastore.preferences.protobuf;

/* JADX INFO: loaded from: classes.dex */
public abstract class r {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final q f2538a = new q();

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final q f2539b;

    static {
        q qVar = null;
        try {
            qVar = (q) Class.forName("androidx.datastore.preferences.protobuf.ExtensionSchemaFull").getDeclaredConstructor(null).newInstance(null);
        } catch (Exception unused) {
        }
        f2539b = qVar;
    }
}
