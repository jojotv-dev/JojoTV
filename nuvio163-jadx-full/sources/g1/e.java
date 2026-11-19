package g1;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class e implements f3 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final com.liskovsoft.sharedutils.helpers.a f18395b = new com.liskovsoft.sharedutils.helpers.a(15);

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final e f18396c = new e(1);

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final e f18397d = new e(2);

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final e f18398e = new e(3);

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public static final e f18399f = new e(4);

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f18400a;

    public /* synthetic */ e(int i10) {
        this.f18400a = i10;
    }

    @Override // g1.f3
    public boolean a(Object obj, Object obj2) {
        switch (this.f18400a) {
            case 1:
                return false;
            case 2:
                return obj == obj2;
            default:
                return Intrinsics.areEqual(obj, obj2);
        }
    }

    public String toString() {
        switch (this.f18400a) {
            case 1:
                return "NeverEqualPolicy";
            case 2:
                return "ReferentialEqualityPolicy";
            case 3:
            default:
                return super.toString();
            case 4:
                return "StructuralEqualityPolicy";
            case 5:
                return "Empty";
        }
    }
}
