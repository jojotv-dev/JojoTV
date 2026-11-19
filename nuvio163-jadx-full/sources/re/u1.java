package re;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes4.dex */
public final class u1 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final u1 f53280b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final u1 f53281c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final u1 f53282e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final /* synthetic */ u1[] f53283i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f53284m;

    static {
        u1 u1Var = new u1("M3u", 0);
        f53280b = u1Var;
        u1 u1Var2 = new u1("Stalker", 1);
        f53281c = u1Var2;
        u1 u1Var3 = new u1("Xtream", 2);
        f53282e = u1Var3;
        u1[] u1VarArr = {u1Var, u1Var2, u1Var3};
        f53283i = u1VarArr;
        f53284m = EnumEntriesKt.enumEntries(u1VarArr);
    }

    public static u1 valueOf(String str) {
        return (u1) Enum.valueOf(u1.class, str);
    }

    public static u1[] values() {
        return (u1[]) f53283i.clone();
    }
}
