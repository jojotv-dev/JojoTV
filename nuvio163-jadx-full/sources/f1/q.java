package f1;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes3.dex */
public final class q {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final q f15990b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final q f15991c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final q f15992e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final /* synthetic */ q[] f15993i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f15994m;

    /* JADX INFO: Fake field, exist only in values array */
    q EF0;

    static {
        q qVar = new q("CornerExtraExtraLarge", 0);
        q qVar2 = new q("CornerExtraLarge", 1);
        q qVar3 = new q("CornerExtraLargeIncreased", 2);
        q qVar4 = new q("CornerExtraLargeTop", 3);
        q qVar5 = new q("CornerExtraSmall", 4);
        f15990b = qVar5;
        q qVar6 = new q("CornerExtraSmallTop", 5);
        q qVar7 = new q("CornerFull", 6);
        f15991c = qVar7;
        q qVar8 = new q("CornerLarge", 7);
        q qVar9 = new q("CornerLargeEnd", 8);
        q qVar10 = new q("CornerLargeIncreased", 9);
        q qVar11 = new q("CornerLargeStart", 10);
        q qVar12 = new q("CornerLargeTop", 11);
        q qVar13 = new q("CornerMedium", 12);
        f15992e = qVar13;
        q[] qVarArr = {qVar, qVar2, qVar3, qVar4, qVar5, qVar6, qVar7, qVar8, qVar9, qVar10, qVar11, qVar12, qVar13, new q("CornerNone", 13), new q("CornerSmall", 14)};
        f15993i = qVarArr;
        f15994m = EnumEntriesKt.enumEntries(qVarArr);
    }

    public static q valueOf(String str) {
        return (q) Enum.valueOf(q.class, str);
    }

    public static q[] values() {
        return (q[]) f15993i.clone();
    }
}
