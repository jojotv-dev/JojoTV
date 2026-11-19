package he;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes4.dex */
public final class g {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final g f20884b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final g f20885c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final g f20886e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final /* synthetic */ g[] f20887i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f20888m;

    static {
        g gVar = new g("PLAYLIST", 0);
        f20884b = gVar;
        g gVar2 = new g("NUMBER", 1);
        f20885c = gVar2;
        g gVar3 = new g("NAME", 2);
        f20886e = gVar3;
        g[] gVarArr = {gVar, gVar2, gVar3};
        f20887i = gVarArr;
        f20888m = EnumEntriesKt.enumEntries(gVarArr);
    }

    public static g valueOf(String str) {
        return (g) Enum.valueOf(g.class, str);
    }

    public static g[] values() {
        return (g[]) f20887i.clone();
    }
}
