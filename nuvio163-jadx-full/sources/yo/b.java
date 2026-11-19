package yo;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes5.dex */
public final class b {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final b f65422b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final b f65423c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final b f65424e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final b f65425i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final /* synthetic */ b[] f65426m;

    static {
        b bVar = new b("AUDIO", 0);
        f65422b = bVar;
        b bVar2 = new b("VIDEO", 1);
        f65423c = bVar2;
        b bVar3 = new b("LIVE", 2);
        f65424e = bVar3;
        b bVar4 = new b("COMMENTS", 3);
        f65425i = bVar4;
        f65426m = new b[]{bVar, bVar2, bVar3, bVar4};
    }

    public static b valueOf(String str) {
        return (b) Enum.valueOf(b.class, str);
    }

    public static b[] values() {
        return (b[]) f65426m.clone();
    }
}
