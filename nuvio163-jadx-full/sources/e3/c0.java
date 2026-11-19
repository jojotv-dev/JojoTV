package e3;

import kotlin.jvm.functions.Function2;

/* JADX INFO: loaded from: classes.dex */
public final class c0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f13817a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Function2 f13818b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final boolean f13819c;

    public c0(String str, Function2 function2) {
        this.f13817a = str;
        this.f13818b = function2;
    }

    public final String toString() {
        return "AccessibilityKey: " + this.f13817a;
    }

    public /* synthetic */ c0(String str) {
        this(str, l.C);
    }

    public c0(String str, int i10) {
        this(str);
        this.f13819c = true;
    }

    public c0(String str, boolean z2, Function2 function2) {
        this(str, function2);
        this.f13819c = z2;
    }
}
