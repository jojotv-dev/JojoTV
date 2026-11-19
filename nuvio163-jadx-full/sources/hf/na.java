package hf;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes.dex */
public final class na {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final na f22363b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final na f22364c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final na f22365e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final na f22366i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final na f22367m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public static final na f22368n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public static final /* synthetic */ na[] f22369o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f22370p;

    static {
        na naVar = new na("HERO", 0);
        f22363b = naVar;
        na naVar2 = new na("EPISODE", 1);
        f22364c = naVar2;
        na naVar3 = new na("CAST_MEMBER", 2);
        f22365e = naVar3;
        na naVar4 = new na("MORE_LIKE_THIS", 3);
        f22366i = naVar4;
        na naVar5 = new na("TRAILER", 4);
        na naVar6 = new na("COLLECTION", 5);
        f22367m = naVar6;
        na naVar7 = new na("COMPANY_OR_NETWORK", 6);
        f22368n = naVar7;
        na[] naVarArr = {naVar, naVar2, naVar3, naVar4, naVar5, naVar6, naVar7};
        f22369o = naVarArr;
        f22370p = EnumEntriesKt.enumEntries(naVarArr);
    }

    public static na valueOf(String str) {
        return (na) Enum.valueOf(na.class, str);
    }

    public static na[] values() {
        return (na[]) f22369o.clone();
    }
}
