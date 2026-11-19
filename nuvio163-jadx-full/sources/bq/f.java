package bq;

import android.util.Log;
import java.lang.reflect.Method;
import java.util.HashMap;

/* JADX INFO: loaded from: classes5.dex */
public abstract class f {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final HashMap f6314a = new HashMap();

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final Method f6315b;

    static {
        new HashMap();
        try {
            f6315b = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
        } catch (ClassNotFoundException | NoSuchMethodException e10) {
            Log.w("SystemServiceHelper", Log.getStackTraceString(e10));
        }
    }
}
