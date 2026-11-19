package x0;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes.dex */
public final class h {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final h f62765b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final h f62766c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final h f62767e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final /* synthetic */ h[] f62768i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f62769m;

    static {
        h hVar = new h("BEFORE", 0);
        f62765b = hVar;
        h hVar2 = new h("ON", 1);
        f62766c = hVar2;
        h hVar3 = new h("AFTER", 2);
        f62767e = hVar3;
        h[] hVarArr = {hVar, hVar2, hVar3};
        f62768i = hVarArr;
        f62769m = EnumEntriesKt.enumEntries(hVarArr);
    }

    public static h valueOf(String str) {
        return (h) Enum.valueOf(h.class, str);
    }

    public static h[] values() {
        return (h[]) f62768i.clone();
    }
}
