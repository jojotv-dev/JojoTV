package o;

import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes3.dex */
public abstract class o1 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final Method f43264a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final Method f43265b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final Method f43266c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final boolean f43267d;

    static {
        try {
            Class cls = Integer.TYPE;
            Class cls2 = Boolean.TYPE;
            Class cls3 = Float.TYPE;
            Method declaredMethod = AbsListView.class.getDeclaredMethod("positionSelector", cls, View.class, cls2, cls3, cls3);
            f43264a = declaredMethod;
            declaredMethod.setAccessible(true);
            Method declaredMethod2 = AdapterView.class.getDeclaredMethod("setSelectedPositionInt", cls);
            f43265b = declaredMethod2;
            declaredMethod2.setAccessible(true);
            Method declaredMethod3 = AdapterView.class.getDeclaredMethod("setNextSelectedPositionInt", cls);
            f43266c = declaredMethod3;
            declaredMethod3.setAccessible(true);
            f43267d = true;
        } catch (NoSuchMethodException e10) {
            e10.printStackTrace();
        }
    }
}
