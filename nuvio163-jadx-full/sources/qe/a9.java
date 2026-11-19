package qe;

import com.liskovsoft.youtubeapi.lounge.models.commands.CommandItem;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes4.dex */
public final class a9 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final a9 f48519b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final a9 f48520c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final a9 f48521e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final a9 f48522i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final a9 f48523m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public static final a9 f48524n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public static final a9 f48525o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public static final a9 f48526p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public static final /* synthetic */ a9[] f48527q;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f48528r;

    static {
        a9 a9Var = new a9("TIMELINE", 0);
        f48519b = a9Var;
        a9 a9Var2 = new a9(CommandItem.KEY_BACK, 1);
        f48520c = a9Var2;
        a9 a9Var3 = new a9("PLAY", 2);
        f48521e = a9Var3;
        a9 a9Var4 = new a9("FORWARD", 3);
        f48522i = a9Var4;
        a9 a9Var5 = new a9("LIVE", 4);
        a9 a9Var6 = new a9("CC", 5);
        a9 a9Var7 = new a9("AUDIO", 6);
        a9 a9Var8 = new a9("SIZE", 7);
        a9 a9Var9 = new a9("SLEEP", 8);
        a9 a9Var10 = new a9("PIN", 9);
        f48523m = a9Var10;
        a9 a9Var11 = new a9("BELL", 10);
        f48524n = a9Var11;
        a9 a9Var12 = new a9("BACKUPS", 11);
        f48525o = a9Var12;
        a9 a9Var13 = new a9("MORE", 12);
        f48526p = a9Var13;
        a9[] a9VarArr = {a9Var, a9Var2, a9Var3, a9Var4, a9Var5, a9Var6, a9Var7, a9Var8, a9Var9, a9Var10, a9Var11, a9Var12, a9Var13};
        f48527q = a9VarArr;
        f48528r = EnumEntriesKt.enumEntries(a9VarArr);
    }

    public static a9 valueOf(String str) {
        return (a9) Enum.valueOf(a9.class, str);
    }

    public static a9[] values() {
        return (a9[]) f48527q.clone();
    }
}
