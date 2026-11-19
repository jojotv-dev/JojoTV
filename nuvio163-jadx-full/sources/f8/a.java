package f8;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes.dex */
public final class a {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final a f16214b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final a f16215c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final a f16216e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final a f16217i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final a f16218m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public static final a f16219n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public static final a f16220o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public static final /* synthetic */ a[] f16221p;

    static {
        a aVar = new a("ERROR_CORRECTION", 0);
        f16214b = aVar;
        a aVar2 = new a("CHARACTER_SET", 1);
        f16215c = aVar2;
        a aVar3 = new a("DATA_MATRIX_SHAPE", 2);
        a aVar4 = new a("DATA_MATRIX_COMPACT", 3);
        a aVar5 = new a("MIN_SIZE", 4);
        a aVar6 = new a("MAX_SIZE", 5);
        a aVar7 = new a("MARGIN", 6);
        f16216e = aVar7;
        a aVar8 = new a("PDF417_COMPACT", 7);
        a aVar9 = new a("PDF417_COMPACTION", 8);
        a aVar10 = new a("PDF417_DIMENSIONS", 9);
        a aVar11 = new a("PDF417_AUTO_ECI", 10);
        a aVar12 = new a("AZTEC_LAYERS", 11);
        a aVar13 = new a("QR_VERSION", 12);
        f16217i = aVar13;
        a aVar14 = new a("QR_MASK_PATTERN", 13);
        f16218m = aVar14;
        a aVar15 = new a("QR_COMPACT", 14);
        f16219n = aVar15;
        a aVar16 = new a("GS1_FORMAT", 15);
        f16220o = aVar16;
        f16221p = new a[]{aVar, aVar2, aVar3, aVar4, aVar5, aVar6, aVar7, aVar8, aVar9, aVar10, aVar11, aVar12, aVar13, aVar14, aVar15, aVar16, new a("FORCE_CODE_SET", 16), new a("FORCE_C40", 17), new a("CODE128_COMPACT", 18)};
    }

    public static a valueOf(String str) {
        return (a) Enum.valueOf(a.class, str);
    }

    public static a[] values() {
        return (a[]) f16221p.clone();
    }
}
