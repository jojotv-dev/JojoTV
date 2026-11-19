package bg;

import android.content.SharedPreferences;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class b implements a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final SharedPreferences f6242a;

    public b(SharedPreferences delegate) {
        Intrinsics.checkNotNullParameter(delegate, "delegate");
        this.f6242a = delegate;
    }

    public final String a(String key) {
        Intrinsics.checkNotNullParameter(key, "key");
        SharedPreferences sharedPreferences = this.f6242a;
        if (sharedPreferences.contains(key)) {
            return sharedPreferences.getString(key, "");
        }
        return null;
    }

    public final void b(String key, String value) {
        Intrinsics.checkNotNullParameter(key, "key");
        Intrinsics.checkNotNullParameter(value, "value");
        SharedPreferences.Editor editorPutString = this.f6242a.edit().putString(key, value);
        Intrinsics.checkNotNullExpressionValue(editorPutString, "putString(...)");
        editorPutString.apply();
    }

    public final void c(String key) {
        Intrinsics.checkNotNullParameter(key, "key");
        SharedPreferences.Editor editorRemove = this.f6242a.edit().remove(key);
        Intrinsics.checkNotNullExpressionValue(editorRemove, "remove(...)");
        editorRemove.apply();
    }
}
