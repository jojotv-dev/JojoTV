package a0;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes.dex */
public final class w {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final w f456b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final w f457c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final w f458e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final /* synthetic */ w[] f459i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f460m;

    static {
        w wVar = new w("Yes", 0);
        f456b = wVar;
        w wVar2 = new w("No", 1);
        f457c = wVar2;
        w wVar3 = new w("NotInitialized", 2);
        f458e = wVar3;
        w[] wVarArr = {wVar, wVar2, wVar3};
        f459i = wVarArr;
        f460m = EnumEntriesKt.enumEntries(wVarArr);
    }

    public static w valueOf(String str) {
        return (w) Enum.valueOf(w.class, str);
    }

    public static w[] values() {
        return (w[]) f459i.clone();
    }
}
