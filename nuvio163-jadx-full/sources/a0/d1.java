package a0;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes.dex */
public final class d1 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final d1 f85b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final d1 f86c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final d1 f87e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final /* synthetic */ d1[] f88i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f89m;

    static {
        d1 d1Var = new d1("Yes", 0);
        f85b = d1Var;
        d1 d1Var2 = new d1("No", 1);
        f86c = d1Var2;
        d1 d1Var3 = new d1("NotInitialized", 2);
        f87e = d1Var3;
        d1[] d1VarArr = {d1Var, d1Var2, d1Var3};
        f88i = d1VarArr;
        f89m = EnumEntriesKt.enumEntries(d1VarArr);
    }

    public static d1 valueOf(String str) {
        return (d1) Enum.valueOf(d1.class, str);
    }

    public static d1[] values() {
        return (d1[]) f88i.clone();
    }
}
