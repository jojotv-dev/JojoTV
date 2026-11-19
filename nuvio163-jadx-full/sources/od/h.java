package od;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes4.dex */
public final class h {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final h f43729b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final h f43730c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final h f43731e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final h f43732i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final h f43733m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public static final h f43734n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public static final h f43735o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public static final h f43736p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public static final h f43737q;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    public static final h f43738r;

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    public static final h f43739s;

    /* JADX INFO: renamed from: t, reason: collision with root package name */
    public static final /* synthetic */ h[] f43740t;

    /* JADX INFO: renamed from: u, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f43741u;

    static {
        h hVar = new h("INVALID_URL", 0);
        f43729b = hVar;
        h hVar2 = new h("OFFICIAL_SERVER", 1);
        f43730c = hVar2;
        h hVar3 = new h("CONNECTION_FAILED", 2);
        f43731e = hVar3;
        h hVar4 = new h("HTTP_ERROR", 3);
        f43732i = hVar4;
        h hVar5 = new h("RESPONSE_TOO_LARGE", 4);
        f43733m = hVar5;
        h hVar6 = new h("INVALID_DOCUMENT", 5);
        f43734n = hVar6;
        h hVar7 = new h("UNSUPPORTED_VERSION", 6);
        f43735o = hVar7;
        h hVar8 = new h("WRONG_SERVICE", 7);
        f43736p = hVar8;
        h hVar9 = new h("NOT_SELF_HOSTED", 8);
        f43737q = hVar9;
        h hVar10 = new h("MISSING_CONFIGURATION", 9);
        f43738r = hVar10;
        h hVar11 = new h("NO_SUPPORTED_AUTH", 10);
        f43739s = hVar11;
        h[] hVarArr = {hVar, hVar2, hVar3, hVar4, hVar5, hVar6, hVar7, hVar8, hVar9, hVar10, hVar11};
        f43740t = hVarArr;
        f43741u = EnumEntriesKt.enumEntries(hVarArr);
    }

    public static h valueOf(String str) {
        return (h) Enum.valueOf(h.class, str);
    }

    public static h[] values() {
        return (h[]) f43740t.clone();
    }
}
