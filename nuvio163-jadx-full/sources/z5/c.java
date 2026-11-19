package z5;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes3.dex */
public final class c {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final c f65563b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final c f65564c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final c f65565e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final c f65566i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final c f65567m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public static final c f65568n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public static final c f65569o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public static final /* synthetic */ c[] f65570p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f65571q;

    /* JADX INFO: Fake field, exist only in values array */
    c EF1;

    static {
        c cVar = new c("Background", 0);
        c cVar2 = new c("Error", 1);
        c cVar3 = new c("ErrorContainer", 2);
        c cVar4 = new c("InverseOnSurface", 3);
        c cVar5 = new c("InversePrimary", 4);
        c cVar6 = new c("InverseSurface", 5);
        c cVar7 = new c("OnBackground", 6);
        c cVar8 = new c("OnError", 7);
        c cVar9 = new c("OnErrorContainer", 8);
        c cVar10 = new c("OnPrimary", 9);
        f65563b = cVar10;
        c cVar11 = new c("OnPrimaryContainer", 10);
        f65564c = cVar11;
        c cVar12 = new c("OnSecondary", 11);
        c cVar13 = new c("OnSecondaryContainer", 12);
        c cVar14 = new c("OnSurface", 13);
        f65565e = cVar14;
        c cVar15 = new c("OnSurfaceVariant", 14);
        c cVar16 = new c("OnTertiary", 15);
        c cVar17 = new c("OnTertiaryContainer", 16);
        c cVar18 = new c("Border", 17);
        f65566i = cVar18;
        c cVar19 = new c("BorderVariant", 18);
        c cVar20 = new c("Primary", 19);
        f65567m = cVar20;
        c cVar21 = new c("PrimaryContainer", 20);
        c cVar22 = new c("Scrim", 21);
        c cVar23 = new c("Secondary", 22);
        c cVar24 = new c("SecondaryContainer", 23);
        c cVar25 = new c("Surface", 24);
        f65568n = cVar25;
        c cVar26 = new c("SurfaceTint", 25);
        c cVar27 = new c("SurfaceVariant", 26);
        f65569o = cVar27;
        c[] cVarArr = {cVar, cVar2, cVar3, cVar4, cVar5, cVar6, cVar7, cVar8, cVar9, cVar10, cVar11, cVar12, cVar13, cVar14, cVar15, cVar16, cVar17, cVar18, cVar19, cVar20, cVar21, cVar22, cVar23, cVar24, cVar25, cVar26, cVar27, new c("Tertiary", 27), new c("TertiaryContainer", 28)};
        f65570p = cVarArr;
        f65571q = EnumEntriesKt.enumEntries(cVarArr);
    }

    public static c valueOf(String str) {
        return (c) Enum.valueOf(c.class, str);
    }

    public static c[] values() {
        return (c[]) f65570p.clone();
    }
}
