package ze;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes4.dex */
public final class o {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final o f66246b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final o f66247c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final /* synthetic */ o[] f66248e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f66249i;

    static {
        o oVar = new o("MyListsAccount", 0);
        f66246b = oVar;
        o oVar2 = new o("Sources", 1);
        o oVar3 = new o("Categories", 2);
        o oVar4 = new o("Playlists", 3);
        o oVar5 = new o("ListChannels", 4);
        o oVar6 = new o("Reload", 5);
        o oVar7 = new o("PreviewSound", 6);
        f66247c = oVar7;
        o[] oVarArr = {oVar, oVar2, oVar3, oVar4, oVar5, oVar6, oVar7};
        f66248e = oVarArr;
        f66249i = EnumEntriesKt.enumEntries(oVarArr);
    }

    public static o valueOf(String str) {
        return (o) Enum.valueOf(o.class, str);
    }

    public static o[] values() {
        return (o[]) f66248e.clone();
    }
}
