package w2;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes.dex */
public final class f0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final f0 f61663b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final f0 f61664c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final f0 f61665e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final f0 f61666i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final f0 f61667m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public static final /* synthetic */ f0[] f61668n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f61669o;

    static {
        f0 f0Var = new f0("Measuring", 0);
        f61663b = f0Var;
        f0 f0Var2 = new f0("LookaheadMeasuring", 1);
        f61664c = f0Var2;
        f0 f0Var3 = new f0("LayingOut", 2);
        f61665e = f0Var3;
        f0 f0Var4 = new f0("LookaheadLayingOut", 3);
        f61666i = f0Var4;
        f0 f0Var5 = new f0("Idle", 4);
        f61667m = f0Var5;
        f0[] f0VarArr = {f0Var, f0Var2, f0Var3, f0Var4, f0Var5};
        f61668n = f0VarArr;
        f61669o = EnumEntriesKt.enumEntries(f0VarArr);
    }

    public static f0 valueOf(String str) {
        return (f0) Enum.valueOf(f0.class, str);
    }

    public static f0[] values() {
        return (f0[]) f61668n.clone();
    }
}
