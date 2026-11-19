package nf;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes4.dex */
public final class a {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final a f42038b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final a f42039c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final a f42040e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final a f42041i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final a f42042m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public static final /* synthetic */ a[] f42043n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f42044o;

    static {
        a aVar = new a("SUBTITLE_UNAVAILABLE", 0);
        f42038b = aVar;
        a aVar2 = new a("NO_SUBTITLE_TRACKS", 1);
        f42039c = aVar2;
        a aVar3 = new a("NO_USABLE_REFERENCE", 2);
        f42040e = aVar3;
        a aVar4 = new a("NO_FIT", 3);
        f42041i = aVar4;
        a aVar5 = new a("INCONCLUSIVE", 4);
        f42042m = aVar5;
        a[] aVarArr = {aVar, aVar2, aVar3, aVar4, aVar5};
        f42043n = aVarArr;
        f42044o = EnumEntriesKt.enumEntries(aVarArr);
    }

    public static a valueOf(String str) {
        return (a) Enum.valueOf(a.class, str);
    }

    public static a[] values() {
        return (a[]) f42043n.clone();
    }
}
