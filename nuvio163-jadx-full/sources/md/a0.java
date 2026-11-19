package md;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes4.dex */
public final class a0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final a0 f35003b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final a0 f35004c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final a0 f35005e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final a0 f35006i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final a0 f35007m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public static final a0 f35008n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public static final /* synthetic */ a0[] f35009o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f35010p;

    static {
        a0 a0Var = new a0("AUTH_REQUIRED", 0);
        f35003b = a0Var;
        a0 a0Var2 = new a0("FORBIDDEN", 1);
        a0 a0Var3 = new a0("UNREACHABLE", 2);
        f35004c = a0Var3;
        a0 a0Var4 = new a0("UNSUPPORTED", 3);
        f35005e = a0Var4;
        a0 a0Var5 = new a0("NOT_FOUND", 4);
        f35006i = a0Var5;
        a0 a0Var6 = new a0("INCOMPLETE", 5);
        f35007m = a0Var6;
        a0 a0Var7 = new a0("FAILED", 6);
        f35008n = a0Var7;
        a0[] a0VarArr = {a0Var, a0Var2, a0Var3, a0Var4, a0Var5, a0Var6, a0Var7};
        f35009o = a0VarArr;
        f35010p = EnumEntriesKt.enumEntries(a0VarArr);
    }

    public static a0 valueOf(String str) {
        return (a0) Enum.valueOf(a0.class, str);
    }

    public static a0[] values() {
        return (a0[]) f35009o.clone();
    }
}
