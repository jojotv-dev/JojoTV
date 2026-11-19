package e8;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes4.dex */
public final class k {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final k f14008b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final k f14009c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final /* synthetic */ k[] f14010e;

    static {
        k kVar = new k("SUCCESS", 0);
        f14008b = kVar;
        k kVar2 = new k("END_OF_BODY", 1);
        f14009c = kVar2;
        f14010e = new k[]{kVar, kVar2};
    }

    public static k valueOf(String str) {
        return (k) Enum.valueOf(k.class, str);
    }

    public static k[] values() {
        return (k[]) f14010e.clone();
    }
}
