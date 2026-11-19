package gc;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes4.dex */
public final class o0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final o0 f19008b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final o0 f19009c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final o0 f19010e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final /* synthetic */ o0[] f19011i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f19012m;

    static {
        o0 o0Var = new o0("REFRESHED", 0);
        f19008b = o0Var;
        o0 o0Var2 = new o0("INVALID_SESSION", 1);
        f19009c = o0Var2;
        o0 o0Var3 = new o0("TRANSIENT_FAILURE", 2);
        f19010e = o0Var3;
        o0[] o0VarArr = {o0Var, o0Var2, o0Var3};
        f19011i = o0VarArr;
        f19012m = EnumEntriesKt.enumEntries(o0VarArr);
    }

    public static o0 valueOf(String str) {
        return (o0) Enum.valueOf(o0.class, str);
    }

    public static o0[] values() {
        return (o0[]) f19011i.clone();
    }
}
