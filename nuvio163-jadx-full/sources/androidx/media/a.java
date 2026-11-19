package androidx.media;

import android.view.textclassifier.TextClassification;
import android.view.textclassifier.TextClassificationContext;
import android.view.textclassifier.TextSelection;

/* JADX INFO: loaded from: classes3.dex */
public abstract /* synthetic */ class a {
    public static /* synthetic */ android.media.session.MediaSessionManager.RemoteUserInfo a(int i10, int i11, String str) {
        return new android.media.session.MediaSessionManager.RemoteUserInfo(str, i10, i11);
    }

    public static /* synthetic */ TextClassification.Request.Builder b(CharSequence charSequence, int i10, int i11) {
        return new TextClassification.Request.Builder(charSequence, i10, i11);
    }

    public static /* synthetic */ TextClassificationContext.Builder c(String str, String str2) {
        return new TextClassificationContext.Builder(str, str2);
    }

    public static /* synthetic */ TextSelection.Request.Builder d(CharSequence charSequence, int i10, int i11) {
        return new TextSelection.Request.Builder(charSequence, i10, i11);
    }

    public static /* synthetic */ void e() {
    }

    public static /* synthetic */ void f() {
    }

    public static /* synthetic */ void g() {
    }
}
