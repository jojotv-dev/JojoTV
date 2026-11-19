package e3;

import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;

/* JADX INFO: loaded from: classes.dex */
public final class o extends Lambda implements Function0 {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final o f13886c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final o f13887e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final o f13888i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final o f13889m;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f13890b;

    static {
        int i10 = 0;
        f13886c = new o(i10, 0);
        f13887e = new o(i10, 1);
        f13888i = new o(i10, 2);
        f13889m = new o(i10, 3);
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ o(int i10, int i11) {
        super(i10);
        this.f13890b = i11;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        switch (this.f13890b) {
            case 0:
                return null;
            case 1:
                return Float.valueOf(0.0f);
            case 2:
                return Float.valueOf(0.0f);
            default:
                return Boolean.FALSE;
        }
    }
}
