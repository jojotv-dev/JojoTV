package k5;

import android.view.Choreographer;
import android.view.ViewTreeObserver;
import java.lang.reflect.Field;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes3.dex */
public abstract class b implements ViewTreeObserver.OnPreDrawListener {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final Field f27873b;

    static {
        Field declaredField = Choreographer.class.getDeclaredField("mLastFrameTimeNanos");
        Intrinsics.checkNotNullExpressionValue(declaredField, "getDeclaredField(...)");
        f27873b = declaredField;
        declaredField.setAccessible(true);
    }
}
