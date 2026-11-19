package o;

import android.os.Build;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
public abstract class l3 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static boolean f43240a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static Method f43241b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final boolean f43242c;

    static {
        f43242c = Build.VERSION.SDK_INT >= 27;
    }
}
