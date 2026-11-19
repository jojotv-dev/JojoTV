package ld;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes4.dex */
public final class p {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final p f32808b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final p f32809c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final p f32810e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final p f32811i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final p f32812m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public static final p f32813n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public static final /* synthetic */ p[] f32814o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f32815p;

    static {
        p pVar = new p("MISSING_CLIENT_ID", 0);
        f32808b = pVar;
        p pVar2 = new p("INVALID_RESPONSE", 1);
        f32809c = pVar2;
        p pVar3 = new p("INSUFFICIENT_SCOPE", 2);
        f32810e = pVar3;
        p pVar4 = new p("CODE_EXPIRED", 3);
        f32811i = pVar4;
        p pVar5 = new p("ACCESS_DENIED", 4);
        f32812m = pVar5;
        p pVar6 = new p("AUTHORIZATION_REVOKED", 5);
        f32813n = pVar6;
        p[] pVarArr = {pVar, pVar2, pVar3, pVar4, pVar5, pVar6};
        f32814o = pVarArr;
        f32815p = EnumEntriesKt.enumEntries(pVarArr);
    }

    public static p valueOf(String str) {
        return (p) Enum.valueOf(p.class, str);
    }

    public static p[] values() {
        return (p[]) f32814o.clone();
    }
}
