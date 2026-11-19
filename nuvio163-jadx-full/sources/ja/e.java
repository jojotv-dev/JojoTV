package ja;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes4.dex */
public final class e {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final e f27252b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final e f27253c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final e f27254e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final e f27255i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final e f27256m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public static final /* synthetic */ e[] f27257n;

    static {
        e eVar = new e("DEFAULT_PATH_LEAF_TO_NULL", 0);
        f27252b = eVar;
        e eVar2 = new e("ALWAYS_RETURN_LIST", 1);
        f27253c = eVar2;
        e eVar3 = new e("AS_PATH_LIST", 2);
        f27254e = eVar3;
        e eVar4 = new e("SUPPRESS_EXCEPTIONS", 3);
        f27255i = eVar4;
        e eVar5 = new e("REQUIRE_PROPERTIES", 4);
        f27256m = eVar5;
        f27257n = new e[]{eVar, eVar2, eVar3, eVar4, eVar5};
    }

    public static e valueOf(String str) {
        return (e) Enum.valueOf(e.class, str);
    }

    public static e[] values() {
        return (e[]) f27257n.clone();
    }
}
