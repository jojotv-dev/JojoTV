package o;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.media3.extractor.text.ttml.TtmlNode;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes3.dex */
public abstract class k1 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final boolean f43213a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final Method f43214b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final Field f43215c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final Field f43216d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final Field f43217e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public static final Field f43218f;

    /* JADX WARN: Code duplicated, block: B:25:0x004a  */
    /* JADX WARN: Code duplicated, block: B:26:0x0057  */
    static {
        Method method;
        Field field;
        Field field2;
        Field field3;
        Field field4;
        boolean z2;
        try {
            Class<?> cls = Class.forName("android.graphics.Insets");
            method = Drawable.class.getMethod("getOpticalInsets", null);
            try {
                field = cls.getField(TtmlNode.LEFT);
                try {
                    field2 = cls.getField("top");
                    try {
                        field3 = cls.getField(TtmlNode.RIGHT);
                        try {
                            field4 = cls.getField("bottom");
                            z2 = true;
                        } catch (ClassNotFoundException | NoSuchFieldException | NoSuchMethodException unused) {
                            field4 = null;
                            z2 = false;
                        }
                    } catch (ClassNotFoundException | NoSuchFieldException | NoSuchMethodException unused2) {
                        field3 = null;
                    }
                } catch (ClassNotFoundException unused3) {
                    field2 = null;
                    field3 = field2;
                    field4 = null;
                    z2 = false;
                    if (z2) {
                        f43214b = method;
                        f43215c = field;
                        f43216d = field2;
                        f43217e = field3;
                        f43218f = field4;
                        f43213a = true;
                        return;
                    }
                    f43214b = null;
                    f43215c = null;
                    f43216d = null;
                    f43217e = null;
                    f43218f = null;
                    f43213a = false;
                } catch (NoSuchFieldException unused4) {
                    field2 = null;
                    field3 = field2;
                    field4 = null;
                    z2 = false;
                    if (z2) {
                        f43214b = method;
                        f43215c = field;
                        f43216d = field2;
                        f43217e = field3;
                        f43218f = field4;
                        f43213a = true;
                        return;
                    }
                    f43214b = null;
                    f43215c = null;
                    f43216d = null;
                    f43217e = null;
                    f43218f = null;
                    f43213a = false;
                } catch (NoSuchMethodException unused5) {
                    field2 = null;
                    field3 = field2;
                    field4 = null;
                    z2 = false;
                    if (z2) {
                        f43214b = method;
                        f43215c = field;
                        f43216d = field2;
                        f43217e = field3;
                        f43218f = field4;
                        f43213a = true;
                        return;
                    }
                    f43214b = null;
                    f43215c = null;
                    f43216d = null;
                    f43217e = null;
                    f43218f = null;
                    f43213a = false;
                }
            } catch (ClassNotFoundException unused6) {
                field = null;
                field2 = field;
                field3 = field2;
                field4 = null;
                z2 = false;
                if (z2) {
                    f43214b = method;
                    f43215c = field;
                    f43216d = field2;
                    f43217e = field3;
                    f43218f = field4;
                    f43213a = true;
                    return;
                }
                f43214b = null;
                f43215c = null;
                f43216d = null;
                f43217e = null;
                f43218f = null;
                f43213a = false;
            } catch (NoSuchFieldException unused7) {
                field = null;
                field2 = field;
                field3 = field2;
                field4 = null;
                z2 = false;
                if (z2) {
                    f43214b = method;
                    f43215c = field;
                    f43216d = field2;
                    f43217e = field3;
                    f43218f = field4;
                    f43213a = true;
                    return;
                }
                f43214b = null;
                f43215c = null;
                f43216d = null;
                f43217e = null;
                f43218f = null;
                f43213a = false;
            } catch (NoSuchMethodException unused8) {
                field = null;
                field2 = field;
                field3 = field2;
                field4 = null;
                z2 = false;
                if (z2) {
                    f43214b = method;
                    f43215c = field;
                    f43216d = field2;
                    f43217e = field3;
                    f43218f = field4;
                    f43213a = true;
                    return;
                }
                f43214b = null;
                f43215c = null;
                f43216d = null;
                f43217e = null;
                f43218f = null;
                f43213a = false;
            }
        } catch (ClassNotFoundException unused9) {
            method = null;
            field = null;
        } catch (NoSuchFieldException unused10) {
            method = null;
            field = null;
        } catch (NoSuchMethodException unused11) {
            method = null;
            field = null;
        }
        if (z2) {
            f43214b = method;
            f43215c = field;
            f43216d = field2;
            f43217e = field3;
            f43218f = field4;
            f43213a = true;
            return;
        }
        f43214b = null;
        f43215c = null;
        f43216d = null;
        f43217e = null;
        f43218f = null;
        f43213a = false;
    }

    public static Rect a(Drawable drawable) {
        if (Build.VERSION.SDK_INT < 29 && f43213a) {
            try {
                Object objInvoke = f43214b.invoke(drawable, null);
                if (objInvoke != null) {
                    return new Rect(f43215c.getInt(objInvoke), f43216d.getInt(objInvoke), f43217e.getInt(objInvoke), f43218f.getInt(objInvoke));
                }
            } catch (IllegalAccessException | InvocationTargetException unused) {
            }
        }
        return m1.f43246c;
    }
}
