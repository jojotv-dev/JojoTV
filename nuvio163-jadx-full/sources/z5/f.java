package z5;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes3.dex */
public final class f {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final f f65599b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final /* synthetic */ f[] f65600c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f65601e;

    /* JADX INFO: Fake field, exist only in values array */
    f EF0;

    static {
        f fVar = new f("CornerExtraLarge", 0);
        f fVar2 = new f("CornerExtraLargeTop", 1);
        f fVar3 = new f("CornerExtraSmall", 2);
        f fVar4 = new f("CornerExtraSmallTop", 3);
        f fVar5 = new f("CornerFull", 4);
        f65599b = fVar5;
        f[] fVarArr = {fVar, fVar2, fVar3, fVar4, fVar5, new f("CornerLarge", 5), new f("CornerLargeEnd", 6), new f("CornerLargeTop", 7), new f("CornerMedium", 8), new f("CornerNone", 9), new f("CornerSmall", 10)};
        f65600c = fVarArr;
        f65601e = EnumEntriesKt.enumEntries(fVarArr);
    }

    public static f valueOf(String str) {
        return (f) Enum.valueOf(f.class, str);
    }

    public static f[] values() {
        return (f[]) f65600c.clone();
    }
}
