package re;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes4.dex */
public final class l {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final l f53086b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final l f53087c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final l f53088e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final l f53089i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final l f53090m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public static final l f53091n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public static final l f53092o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public static final l f53093p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public static final l f53094q;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    public static final l f53095r;

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    public static final l f53096s;

    /* JADX INFO: renamed from: t, reason: collision with root package name */
    public static final l f53097t;

    /* JADX INFO: renamed from: u, reason: collision with root package name */
    public static final l f53098u;

    /* JADX INFO: renamed from: v, reason: collision with root package name */
    public static final l f53099v;

    /* JADX INFO: renamed from: w, reason: collision with root package name */
    public static final l f53100w;

    /* JADX INFO: renamed from: x, reason: collision with root package name */
    public static final l f53101x;

    /* JADX INFO: renamed from: y, reason: collision with root package name */
    public static final /* synthetic */ l[] f53102y;

    /* JADX INFO: renamed from: z, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f53103z;

    static {
        l lVar = new l("InvalidUrl", 0);
        f53086b = lVar;
        l lVar2 = new l("NoChannels", 1);
        f53087c = lVar2;
        l lVar3 = new l("LoadFailed", 2);
        f53088e = lVar3;
        l lVar4 = new l("FileEmpty", 3);
        f53089i = lVar4;
        l lVar5 = new l("FileNoChannels", 4);
        f53090m = lVar5;
        l lVar6 = new l("StalkerRequired", 5);
        f53091n = lVar6;
        l lVar7 = new l("StalkerInvalidUrl", 6);
        f53092o = lVar7;
        l lVar8 = new l("StalkerNoChannels", 7);
        f53093p = lVar8;
        l lVar9 = new l("StalkerFailed", 8);
        f53094q = lVar9;
        l lVar10 = new l("StalkerToken", 9);
        f53095r = lVar10;
        l lVar11 = new l("StalkerIncomplete", 10);
        f53096s = lVar11;
        l lVar12 = new l("XtreamRequired", 11);
        f53097t = lVar12;
        l lVar13 = new l("XtreamInvalidUrl", 12);
        f53098u = lVar13;
        l lVar14 = new l("XtreamNoChannels", 13);
        f53099v = lVar14;
        l lVar15 = new l("XtreamFailed", 14);
        f53100w = lVar15;
        l lVar16 = new l("GuideInvalidUrl", 15);
        f53101x = lVar16;
        l[] lVarArr = {lVar, lVar2, lVar3, lVar4, lVar5, lVar6, lVar7, lVar8, lVar9, lVar10, lVar11, lVar12, lVar13, lVar14, lVar15, lVar16};
        f53102y = lVarArr;
        f53103z = EnumEntriesKt.enumEntries(lVarArr);
    }

    public static l valueOf(String str) {
        return (l) Enum.valueOf(l.class, str);
    }

    public static l[] values() {
        return (l[]) f53102y.clone();
    }
}
