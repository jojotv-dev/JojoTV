package ue;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes.dex */
public final class i5 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final i5 f58861b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final i5 f58862c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final i5 f58863e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final /* synthetic */ i5[] f58864i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f58865m;

    static {
        i5 i5Var = new i5("LOADING", 0);
        f58861b = i5Var;
        i5 i5Var2 = new i5("SUCCESS", 1);
        f58862c = i5Var2;
        i5 i5Var3 = new i5("ERROR", 2);
        f58863e = i5Var3;
        i5[] i5VarArr = {i5Var, i5Var2, i5Var3};
        f58864i = i5VarArr;
        f58865m = EnumEntriesKt.enumEntries(i5VarArr);
    }

    public static i5 valueOf(String str) {
        return (i5) Enum.valueOf(i5.class, str);
    }

    public static i5[] values() {
        return (i5[]) f58864i.clone();
    }
}
