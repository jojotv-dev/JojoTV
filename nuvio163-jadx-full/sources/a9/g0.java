package a9;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes4.dex */
public final class g0 implements k9.c {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final g0 f647b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final /* synthetic */ g0[] f648c;

    static {
        g0 g0Var = new g0("SHA_512", 0);
        f647b = g0Var;
        f648c = new g0[]{g0Var};
    }

    public static g0 valueOf(String str) {
        return (g0) Enum.valueOf(g0.class, str);
    }

    public static g0[] values() {
        return (g0[]) f648c.clone();
    }

    @Override // k9.c
    public final long getValue() {
        return 1L;
    }
}
