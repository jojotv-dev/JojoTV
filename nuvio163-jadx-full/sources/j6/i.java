package j6;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes.dex */
public final class i {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final i f27200b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final i f27201c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final i f27202e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final i f27203i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final i f27204m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public static final i f27205n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public static final /* synthetic */ i[] f27206o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f27207p;

    static {
        i iVar = new i("Verbose", 0);
        f27200b = iVar;
        i iVar2 = new i("Debug", 1);
        f27201c = iVar2;
        i iVar3 = new i("Info", 2);
        f27202e = iVar3;
        i iVar4 = new i("Warn", 3);
        f27203i = iVar4;
        i iVar5 = new i("Error", 4);
        f27204m = iVar5;
        i iVar6 = new i("Assert", 5);
        f27205n = iVar6;
        i[] iVarArr = {iVar, iVar2, iVar3, iVar4, iVar5, iVar6};
        f27206o = iVarArr;
        f27207p = EnumEntriesKt.enumEntries(iVarArr);
    }

    public static i valueOf(String str) {
        return (i) Enum.valueOf(i.class, str);
    }

    public static i[] values() {
        return (i[]) f27206o.clone();
    }
}
