package m0;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes.dex */
public final class p0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final p0 f34521b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final p0 f34522c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final p0 f34523e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final /* synthetic */ p0[] f34524i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f34525m;

    static {
        p0 p0Var = new p0("Cursor", 0);
        f34521b = p0Var;
        p0 p0Var2 = new p0("SelectionStart", 1);
        f34522c = p0Var2;
        p0 p0Var3 = new p0("SelectionEnd", 2);
        f34523e = p0Var3;
        p0[] p0VarArr = {p0Var, p0Var2, p0Var3};
        f34524i = p0VarArr;
        f34525m = EnumEntriesKt.enumEntries(p0VarArr);
    }

    public static p0 valueOf(String str) {
        return (p0) Enum.valueOf(p0.class, str);
    }

    public static p0[] values() {
        return (p0[]) f34524i.clone();
    }
}
