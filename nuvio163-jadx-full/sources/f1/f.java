package f1;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes3.dex */
public final class f {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final f f15858b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final f f15859c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final f f15860e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final f f15861i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final f f15862m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public static final f f15863n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public static final f f15864o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public static final f f15865p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public static final f f15866q;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    public static final f f15867r;

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    public static final f f15868s;

    /* JADX INFO: renamed from: t, reason: collision with root package name */
    public static final /* synthetic */ f[] f15869t;

    /* JADX INFO: renamed from: u, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f15870u;

    /* JADX INFO: Fake field, exist only in values array */
    f EF1;

    static {
        f fVar = new f("Background", 0);
        f fVar2 = new f("Error", 1);
        f15858b = fVar2;
        f fVar3 = new f("ErrorContainer", 2);
        f fVar4 = new f("InverseOnSurface", 3);
        f fVar5 = new f("InversePrimary", 4);
        f fVar6 = new f("InverseSurface", 5);
        f fVar7 = new f("OnBackground", 6);
        f fVar8 = new f("OnError", 7);
        f fVar9 = new f("OnErrorContainer", 8);
        f fVar10 = new f("OnPrimary", 9);
        f15859c = fVar10;
        f fVar11 = new f("OnPrimaryContainer", 10);
        f15860e = fVar11;
        f fVar12 = new f("OnPrimaryFixed", 11);
        f fVar13 = new f("OnPrimaryFixedVariant", 12);
        f fVar14 = new f("OnSecondary", 13);
        f fVar15 = new f("OnSecondaryContainer", 14);
        f fVar16 = new f("OnSecondaryFixed", 15);
        f fVar17 = new f("OnSecondaryFixedVariant", 16);
        f fVar18 = new f("OnSurface", 17);
        f15861i = fVar18;
        f fVar19 = new f("OnSurfaceVariant", 18);
        f15862m = fVar19;
        f fVar20 = new f("OnTertiary", 19);
        f fVar21 = new f("OnTertiaryContainer", 20);
        f fVar22 = new f("OnTertiaryFixed", 21);
        f fVar23 = new f("OnTertiaryFixedVariant", 22);
        f fVar24 = new f("Outline", 23);
        f15863n = fVar24;
        f fVar25 = new f("OutlineVariant", 24);
        f fVar26 = new f("Primary", 25);
        f15864o = fVar26;
        f fVar27 = new f("PrimaryContainer", 26);
        f fVar28 = new f("PrimaryFixed", 27);
        f fVar29 = new f("PrimaryFixedDim", 28);
        f fVar30 = new f("Scrim", 29);
        f fVar31 = new f("Secondary", 30);
        f fVar32 = new f("SecondaryContainer", 31);
        f fVar33 = new f("SecondaryFixed", 32);
        f fVar34 = new f("SecondaryFixedDim", 33);
        f fVar35 = new f("Surface", 34);
        f15865p = fVar35;
        f fVar36 = new f("SurfaceBright", 35);
        f fVar37 = new f("SurfaceContainer", 36);
        f15866q = fVar37;
        f fVar38 = new f("SurfaceContainerHigh", 37);
        f fVar39 = new f("SurfaceContainerHighest", 38);
        f15867r = fVar39;
        f fVar40 = new f("SurfaceContainerLow", 39);
        f fVar41 = new f("SurfaceContainerLowest", 40);
        f fVar42 = new f("SurfaceDim", 41);
        f fVar43 = new f("SurfaceTint", 42);
        f fVar44 = new f("SurfaceVariant", 43);
        f15868s = fVar44;
        f[] fVarArr = {fVar, fVar2, fVar3, fVar4, fVar5, fVar6, fVar7, fVar8, fVar9, fVar10, fVar11, fVar12, fVar13, fVar14, fVar15, fVar16, fVar17, fVar18, fVar19, fVar20, fVar21, fVar22, fVar23, fVar24, fVar25, fVar26, fVar27, fVar28, fVar29, fVar30, fVar31, fVar32, fVar33, fVar34, fVar35, fVar36, fVar37, fVar38, fVar39, fVar40, fVar41, fVar42, fVar43, fVar44, new f("Tertiary", 44), new f("TertiaryContainer", 45), new f("TertiaryFixed", 46), new f("TertiaryFixedDim", 47)};
        f15869t = fVarArr;
        f15870u = EnumEntriesKt.enumEntries(fVarArr);
    }

    public static f valueOf(String str) {
        return (f) Enum.valueOf(f.class, str);
    }

    public static f[] values() {
        return (f[]) f15869t.clone();
    }
}
