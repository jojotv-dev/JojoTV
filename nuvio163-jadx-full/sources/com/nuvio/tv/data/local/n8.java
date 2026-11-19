package com.nuvio.tv.data.local;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes.dex */
public final class n8 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final n8 f9086b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final n8 f9087c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final /* synthetic */ n8[] f9088e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f9089i;

    static {
        n8 n8Var = new n8("NUVIO", 0);
        f9086b = n8Var;
        n8 n8Var2 = new n8("TRAKT", 1);
        f9087c = n8Var2;
        n8[] n8VarArr = {n8Var, n8Var2};
        f9088e = n8VarArr;
        f9089i = EnumEntriesKt.enumEntries(n8VarArr);
    }

    public static n8 valueOf(String str) {
        return (n8) Enum.valueOf(n8.class, str);
    }

    public static n8[] values() {
        return (n8[]) f9088e.clone();
    }
}
