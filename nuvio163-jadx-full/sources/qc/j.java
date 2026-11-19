package qc;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes4.dex */
public final class j {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final j f45833b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final j f45834c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final j f45835e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final /* synthetic */ j[] f45836i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f45837m;

    static {
        j jVar = new j("Wifi", 0);
        f45833b = jVar;
        j jVar2 = new j("Ethernet", 1);
        f45834c = jVar2;
        j jVar3 = new j("Offline", 2);
        f45835e = jVar3;
        j[] jVarArr = {jVar, jVar2, jVar3};
        f45836i = jVarArr;
        f45837m = EnumEntriesKt.enumEntries(jVarArr);
    }

    public static j valueOf(String str) {
        return (j) Enum.valueOf(j.class, str);
    }

    public static j[] values() {
        return (j[]) f45836i.clone();
    }
}
