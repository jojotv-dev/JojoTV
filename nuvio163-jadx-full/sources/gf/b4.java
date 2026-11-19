package gf;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes4.dex */
public final class b4 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final b4 f19366b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final b4 f19367c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final b4 f19368e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final b4 f19369i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final b4 f19370m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public static final b4 f19371n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public static final b4 f19372o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public static final b4 f19373p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public static final /* synthetic */ b4[] f19374q;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f19375r;

    static {
        b4 b4Var = new b4("PRESETS", 0);
        f19366b = b4Var;
        b4 b4Var2 = new b4("LIST", 1);
        f19367c = b4Var2;
        b4 b4Var3 = new b4("PRODUCTION", 2);
        f19368e = b4Var3;
        b4 b4Var4 = new b4("NETWORK", 3);
        f19369i = b4Var4;
        b4 b4Var5 = new b4("COLLECTION", 4);
        f19370m = b4Var5;
        b4 b4Var6 = new b4("PERSON", 5);
        f19371n = b4Var6;
        b4 b4Var7 = new b4("DIRECTOR", 6);
        f19372o = b4Var7;
        b4 b4Var8 = new b4("DISCOVER", 7);
        f19373p = b4Var8;
        b4[] b4VarArr = {b4Var, b4Var2, b4Var3, b4Var4, b4Var5, b4Var6, b4Var7, b4Var8};
        f19374q = b4VarArr;
        f19375r = EnumEntriesKt.enumEntries(b4VarArr);
    }

    public static b4 valueOf(String str) {
        return (b4) Enum.valueOf(b4.class, str);
    }

    public static b4[] values() {
        return (b4[]) f19374q.clone();
    }
}
