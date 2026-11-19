package x3;

import java.util.UUID;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;

/* JADX INFO: loaded from: classes3.dex */
public final class d extends Lambda implements Function0 {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final d f63354c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final d f63355e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final d f63356i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final d f63357m;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f63358b;

    static {
        int i10 = 0;
        f63354c = new d(i10, 0);
        f63355e = new d(i10, 1);
        f63356i = new d(i10, 2);
        f63357m = new d(i10, 3);
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ d(int i10, int i11) {
        super(i10);
        this.f63358b = i11;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        switch (this.f63358b) {
            case 0:
                return UUID.randomUUID();
            case 1:
                return Boolean.FALSE;
            case 2:
                return "DEFAULT_TEST_TAG";
            default:
                return UUID.randomUUID();
        }
    }
}
