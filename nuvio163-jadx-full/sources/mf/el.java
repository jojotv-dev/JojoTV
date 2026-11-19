package mf;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes4.dex */
public final class el {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final el f36169b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final el f36170c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final /* synthetic */ el[] f36171e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f36172i;

    static {
        el elVar = new el("INTERNAL", 0);
        f36169b = elVar;
        el elVar2 = new el("ADDON", 1);
        f36170c = elVar2;
        el[] elVarArr = {elVar, elVar2};
        f36171e = elVarArr;
        f36172i = EnumEntriesKt.enumEntries(elVarArr);
    }

    public static el valueOf(String str) {
        return (el) Enum.valueOf(el.class, str);
    }

    public static el[] values() {
        return (el[]) f36171e.clone();
    }
}
