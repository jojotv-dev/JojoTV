package b2;

import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Lambda;

/* JADX INFO: loaded from: classes.dex */
public final class r extends Lambda implements Function1 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f4261b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ int f4262c;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ r(int i10, int i11) {
        super(1);
        this.f4261b = i11;
        this.f4262c = i10;
    }

    @Override // kotlin.jvm.functions.Function1
    public final Object invoke(Object obj) {
        switch (this.f4261b) {
            case 0:
                return Boolean.valueOf(((o0) obj).e1(this.f4262c));
            case 1:
                return Boolean.valueOf(((o0) obj).e1(this.f4262c));
            case 2:
                return Boolean.valueOf(((o0) obj).X0(this.f4262c));
            case 3:
                return Boolean.valueOf(((o0) obj).e1(this.f4262c));
            default:
                return Boolean.valueOf(((o0) obj).e1(this.f4262c));
        }
    }
}
