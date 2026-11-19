package e3;

import kotlin.jvm.functions.Function0;

/* JADX INFO: loaded from: classes.dex */
public final class k {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Function0 f13837a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Function0 f13838b;

    public k(Function0 function0, Function0 function1) {
        this.f13837a = function0;
        this.f13838b = function1;
    }

    public final String toString() {
        return "ScrollAxisRange(value=" + ((Number) this.f13837a.invoke()).floatValue() + ", maxValue=" + ((Number) this.f13838b.invoke()).floatValue() + ", reverseScrolling=false)";
    }
}
