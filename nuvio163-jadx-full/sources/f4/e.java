package f4;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.textclassifier.TextClassificationManager;
import android.view.textclassifier.TextClassifier;
import kotlin.NoWhenBranchMatchedException;
import x0.w;

/* JADX INFO: loaded from: classes.dex */
public abstract class e {
    public static Typeface a(Typeface typeface, int i10, boolean z2) {
        return Typeface.create(typeface, i10, z2);
    }

    public static Handler b(Looper looper) {
        return Handler.createAsync(looper);
    }

    public static TextClassifier c(Context context, w wVar) {
        String str;
        TextClassificationManager textClassificationManager = (TextClassificationManager) context.getSystemService(TextClassificationManager.class);
        int iOrdinal = wVar.ordinal();
        if (iOrdinal == 0) {
            str = "edittext";
        } else {
            if (iOrdinal != 1) {
                throw new NoWhenBranchMatchedException();
            }
            str = "textview";
        }
        androidx.media.a.g();
        return textClassificationManager.createTextClassificationSession(androidx.media.a.c(context.getPackageName(), str).build());
    }

    public static void d(View view) {
        view.resetPivot();
    }

    public static void e(int i10, View view) {
        view.setOutlineAmbientShadowColor(i10);
    }

    public static void f(int i10, View view) {
        view.setOutlineSpotShadowColor(i10);
    }
}
