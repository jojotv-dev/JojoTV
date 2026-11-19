package y4;

import android.text.Editable;
import w4.w;

/* JADX INFO: loaded from: classes3.dex */
public final class a extends Editable.Factory {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final Object f64616a = new Object();

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static volatile a f64617b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static Class f64618c;

    @Override // android.text.Editable.Factory
    public final Editable newEditable(CharSequence charSequence) {
        Class cls = f64618c;
        return cls != null ? new w(cls, charSequence) : super.newEditable(charSequence);
    }
}
