package sf;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes4.dex */
public final class sh {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final sh f56151b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final sh f56152c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final sh f56153e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final /* synthetic */ sh[] f56154i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f56155m;

    static {
        sh shVar = new sh("Supporters", 0);
        f56151b = shVar;
        sh shVar2 = new sh("Sponsors", 1);
        f56152c = shVar2;
        sh shVar3 = new sh("Contributors", 2);
        f56153e = shVar3;
        sh[] shVarArr = {shVar, shVar2, shVar3};
        f56154i = shVarArr;
        f56155m = EnumEntriesKt.enumEntries(shVarArr);
    }

    public static sh valueOf(String str) {
        return (sh) Enum.valueOf(sh.class, str);
    }

    public static sh[] values() {
        return (sh[]) f56154i.clone();
    }
}
