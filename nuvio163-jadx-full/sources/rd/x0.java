package rd;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes.dex */
public final class x0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final x0 f52681b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final x0 f52682c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final x0 f52683e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final x0 f52684i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final x0 f52685m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public static final x0 f52686n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public static final /* synthetic */ x0[] f52687o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f52688p;

    static {
        x0 x0Var = new x0("MISSING_CLIENT_ID", 0);
        f52681b = x0Var;
        x0 x0Var2 = new x0("INVALID_PIN_RESPONSE", 1);
        f52682c = x0Var2;
        x0 x0Var3 = new x0("PIN_EXPIRED", 2);
        f52683e = x0Var3;
        x0 x0Var4 = new x0("PIN_INVALIDATED", 3);
        f52684i = x0Var4;
        x0 x0Var5 = new x0("AUTHORIZATION_REVOKED", 4);
        f52685m = x0Var5;
        x0 x0Var6 = new x0("NETWORK", 5);
        f52686n = x0Var6;
        x0[] x0VarArr = {x0Var, x0Var2, x0Var3, x0Var4, x0Var5, x0Var6};
        f52687o = x0VarArr;
        f52688p = EnumEntriesKt.enumEntries(x0VarArr);
    }

    public static x0 valueOf(String str) {
        return (x0) Enum.valueOf(x0.class, str);
    }

    public static x0[] values() {
        return (x0[]) f52687o.clone();
    }
}
