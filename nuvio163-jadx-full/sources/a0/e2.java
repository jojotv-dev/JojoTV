package a0;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes.dex */
public final class e2 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final e2 f100b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final e2 f101c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final /* synthetic */ e2[] f102e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f103i;

    static {
        e2 e2Var = new e2("Vertical", 0);
        f100b = e2Var;
        e2 e2Var2 = new e2("Horizontal", 1);
        f101c = e2Var2;
        e2[] e2VarArr = {e2Var, e2Var2};
        f102e = e2VarArr;
        f103i = EnumEntriesKt.enumEntries(e2VarArr);
    }

    public static e2 valueOf(String str) {
        return (e2) Enum.valueOf(e2.class, str);
    }

    public static e2[] values() {
        return (e2[]) f102e.clone();
    }
}
