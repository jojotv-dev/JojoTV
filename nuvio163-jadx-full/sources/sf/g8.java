package sf;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes4.dex */
public final class g8 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final g8 f54987b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final g8 f54988c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final g8 f54989e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final g8 f54990i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final g8 f54991m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public static final g8 f54992n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public static final g8 f54993o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public static final /* synthetic */ g8[] f54994p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f54995q;

    static {
        g8 g8Var = new g8("ALL", 0);
        f54987b = g8Var;
        g8 g8Var2 = new g8("HUB", 1);
        f54988c = g8Var2;
        g8 g8Var3 = new g8("MAIN", 2);
        f54989e = g8Var3;
        g8 g8Var4 = new g8("GUIDE", 3);
        f54990i = g8Var4;
        g8 g8Var5 = new g8("PLAYBACK", 4);
        f54991m = g8Var5;
        g8 g8Var6 = new g8("ON_DEMAND", 5);
        f54992n = g8Var6;
        g8 g8Var7 = new g8("INTEGRATION", 6);
        f54993o = g8Var7;
        g8[] g8VarArr = {g8Var, g8Var2, g8Var3, g8Var4, g8Var5, g8Var6, g8Var7};
        f54994p = g8VarArr;
        f54995q = EnumEntriesKt.enumEntries(g8VarArr);
    }

    public static g8 valueOf(String str) {
        return (g8) Enum.valueOf(g8.class, str);
    }

    public static g8[] values() {
        return (g8[]) f54994p.clone();
    }
}
