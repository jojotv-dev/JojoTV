package sf;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes4.dex */
public final class yb {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final yb f56577b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final yb f56578c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final yb f56579e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final yb f56580i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final yb f56581m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public static final /* synthetic */ yb[] f56582n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f56583o;

    static {
        yb ybVar = new yb("Idle", 0);
        f56577b = ybVar;
        yb ybVar2 = new yb("TestingLatency", 1);
        f56578c = ybVar2;
        yb ybVar3 = new yb("TestingDownload", 2);
        f56579e = ybVar3;
        yb ybVar4 = new yb("Done", 3);
        f56580i = ybVar4;
        yb ybVar5 = new yb("Error", 4);
        f56581m = ybVar5;
        yb[] ybVarArr = {ybVar, ybVar2, ybVar3, ybVar4, ybVar5};
        f56582n = ybVarArr;
        f56583o = EnumEntriesKt.enumEntries(ybVarArr);
    }

    public static yb valueOf(String str) {
        return (yb) Enum.valueOf(yb.class, str);
    }

    public static yb[] values() {
        return (yb[]) f56582n.clone();
    }
}
