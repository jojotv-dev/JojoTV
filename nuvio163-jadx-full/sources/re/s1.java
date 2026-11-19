package re;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes4.dex */
public final class s1 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final s1 f53244b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final s1 f53245c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final s1 f53246e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final s1 f53247i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final /* synthetic */ s1[] f53248m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f53249n;

    static {
        s1 s1Var = new s1("None", 0);
        f53244b = s1Var;
        s1 s1Var2 = new s1("Loading", 1);
        f53245c = s1Var2;
        s1 s1Var3 = new s1("Loaded", 2);
        f53246e = s1Var3;
        s1 s1Var4 = new s1("Failed", 3);
        f53247i = s1Var4;
        s1[] s1VarArr = {s1Var, s1Var2, s1Var3, s1Var4};
        f53248m = s1VarArr;
        f53249n = EnumEntriesKt.enumEntries(s1VarArr);
    }

    public static s1 valueOf(String str) {
        return (s1) Enum.valueOf(s1.class, str);
    }

    public static s1[] values() {
        return (s1[]) f53248m.clone();
    }
}
