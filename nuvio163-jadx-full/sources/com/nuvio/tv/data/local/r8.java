package com.nuvio.tv.data.local;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes.dex */
public final class r8 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final r8 f9536b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final r8 f9537c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final r8 f9538e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final /* synthetic */ r8[] f9539i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f9540m;

    static {
        r8 r8Var = new r8("MANUAL", 0);
        f9536b = r8Var;
        r8 r8Var2 = new r8("FIRST_STREAM", 1);
        f9537c = r8Var2;
        r8 r8Var3 = new r8("REGEX_MATCH", 2);
        f9538e = r8Var3;
        r8[] r8VarArr = {r8Var, r8Var2, r8Var3};
        f9539i = r8VarArr;
        f9540m = EnumEntriesKt.enumEntries(r8VarArr);
    }

    public static r8 valueOf(String str) {
        return (r8) Enum.valueOf(r8.class, str);
    }

    public static r8[] values() {
        return (r8[]) f9539i.clone();
    }
}
