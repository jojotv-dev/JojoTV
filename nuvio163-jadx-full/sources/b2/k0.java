package b2;

import kotlin.NoWhenBranchMatchedException;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes.dex */
public final class k0 implements j0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final k0 f4240b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final k0 f4241c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final k0 f4242e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final /* synthetic */ k0[] f4243i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f4244m;

    static {
        k0 k0Var = new k0("Active", 0);
        f4240b = k0Var;
        k0 k0Var2 = new k0("ActiveParent", 1);
        f4241c = k0Var2;
        k0 k0Var3 = new k0("Captured", 2);
        k0 k0Var4 = new k0("Inactive", 3);
        f4242e = k0Var4;
        k0[] k0VarArr = {k0Var, k0Var2, k0Var3, k0Var4};
        f4243i = k0VarArr;
        f4244m = EnumEntriesKt.enumEntries(k0VarArr);
    }

    public static k0 valueOf(String str) {
        return (k0) Enum.valueOf(k0.class, str);
    }

    public static k0[] values() {
        return (k0[]) f4243i.clone();
    }

    public final boolean a() {
        int iOrdinal = ordinal();
        if (iOrdinal == 0 || iOrdinal == 1 || iOrdinal == 2) {
            return true;
        }
        if (iOrdinal == 3) {
            return false;
        }
        throw new NoWhenBranchMatchedException();
    }

    public final boolean b() {
        int iOrdinal = ordinal();
        if (iOrdinal != 0) {
            if (iOrdinal == 1) {
                return false;
            }
            if (iOrdinal != 2) {
                if (iOrdinal == 3) {
                    return false;
                }
                throw new NoWhenBranchMatchedException();
            }
        }
        return true;
    }
}
