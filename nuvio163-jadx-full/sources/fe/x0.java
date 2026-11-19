package fe;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes4.dex */
public final class x0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final x0 f17792b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final x0 f17793c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final /* synthetic */ x0[] f17794e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f17795i;

    static {
        x0 x0Var = new x0("MOVIE", 0);
        f17792b = x0Var;
        x0 x0Var2 = new x0("SERIES", 1);
        f17793c = x0Var2;
        x0[] x0VarArr = {x0Var, x0Var2};
        f17794e = x0VarArr;
        f17795i = EnumEntriesKt.enumEntries(x0VarArr);
    }

    public static x0 valueOf(String str) {
        return (x0) Enum.valueOf(x0.class, str);
    }

    public static x0[] values() {
        return (x0[]) f17794e.clone();
    }
}
