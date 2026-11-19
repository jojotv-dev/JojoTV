package t3;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes.dex */
public final class n {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final n f57171b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final n f57172c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final /* synthetic */ n[] f57173e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f57174i;

    static {
        n nVar = new n("Ltr", 0);
        f57171b = nVar;
        n nVar2 = new n("Rtl", 1);
        f57172c = nVar2;
        n[] nVarArr = {nVar, nVar2};
        f57173e = nVarArr;
        f57174i = EnumEntriesKt.enumEntries(nVarArr);
    }

    public static n valueOf(String str) {
        return (n) Enum.valueOf(n.class, str);
    }

    public static n[] values() {
        return (n[]) f57173e.clone();
    }
}
