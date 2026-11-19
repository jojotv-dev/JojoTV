package gc;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes.dex */
public final class e0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final e0 f18879b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final e0 f18880c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final e0 f18881e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final e0 f18882i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final /* synthetic */ e0[] f18883m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f18884n;

    static {
        e0 e0Var = new e0("VALID", 0);
        f18879b = e0Var;
        e0 e0Var2 = new e0("INVALID_SESSION", 1);
        f18880c = e0Var2;
        e0 e0Var3 = new e0("EXPIRED_ACCESS_TOKEN", 2);
        f18881e = e0Var3;
        e0 e0Var4 = new e0("TRANSIENT_FAILURE", 3);
        f18882i = e0Var4;
        e0[] e0VarArr = {e0Var, e0Var2, e0Var3, e0Var4};
        f18883m = e0VarArr;
        f18884n = EnumEntriesKt.enumEntries(e0VarArr);
    }

    public static e0 valueOf(String str) {
        return (e0) Enum.valueOf(e0.class, str);
    }

    public static e0[] values() {
        return (e0[]) f18883m.clone();
    }
}
