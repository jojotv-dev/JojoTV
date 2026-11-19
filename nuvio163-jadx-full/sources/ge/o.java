package ge;

import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class o {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final float f19269a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final float f19270b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final Function3 f19271c;

    public o(float f10, float f11, Function3 draw) {
        Intrinsics.checkNotNullParameter(draw, "draw");
        this.f19269a = f10;
        this.f19270b = f11;
        this.f19271c = draw;
    }
}
