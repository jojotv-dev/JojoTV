package e1;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes3.dex */
public final class h {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final h f13667b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final h f13668c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final h f13669e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final /* synthetic */ h[] f13670i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f13671m;

    static {
        h hVar = new h("Focused", 0);
        f13667b = hVar;
        h hVar2 = new h("UnfocusedEmpty", 1);
        f13668c = hVar2;
        h hVar3 = new h("UnfocusedNotEmpty", 2);
        f13669e = hVar3;
        h[] hVarArr = {hVar, hVar2, hVar3};
        f13670i = hVarArr;
        f13671m = EnumEntriesKt.enumEntries(hVarArr);
    }

    public static h valueOf(String str) {
        return (h) Enum.valueOf(h.class, str);
    }

    public static h[] values() {
        return (h[]) f13670i.clone();
    }
}
