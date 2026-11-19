package cf;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes4.dex */
public final class d {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final d f6945b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final d f6946c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final /* synthetic */ d[] f6947e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f6948i;

    static {
        d dVar = new d("CatalogOrder", 0);
        f6945b = dVar;
        d dVar2 = new d("Collections", 1);
        f6946c = dVar2;
        d[] dVarArr = {dVar, dVar2};
        f6947e = dVarArr;
        f6948i = EnumEntriesKt.enumEntries(dVarArr);
    }

    public static d valueOf(String str) {
        return (d) Enum.valueOf(d.class, str);
    }

    public static d[] values() {
        return (d[]) f6947e.clone();
    }
}
