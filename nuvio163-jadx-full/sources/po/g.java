package po;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes5.dex */
public final class g {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final g f45245b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final g f45246c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final /* synthetic */ g[] f45247e;

    static {
        g gVar = new g("PROPAGATE", 0);
        f45245b = gVar;
        g gVar2 = new g("CANCEL", 1);
        f45246c = gVar2;
        f45247e = new g[]{gVar, gVar2};
    }

    public static g valueOf(String str) {
        return (g) Enum.valueOf(g.class, str);
    }

    public static g[] values() {
        return (g[]) f45247e.clone();
    }
}
