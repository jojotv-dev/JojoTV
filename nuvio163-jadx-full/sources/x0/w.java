package x0;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes.dex */
public final class w {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final w f62946b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final w f62947c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final /* synthetic */ w[] f62948e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f62949i;

    static {
        w wVar = new w("EditableText", 0);
        f62946b = wVar;
        w wVar2 = new w("StaticText", 1);
        f62947c = wVar2;
        w[] wVarArr = {wVar, wVar2};
        f62948e = wVarArr;
        f62949i = EnumEntriesKt.enumEntries(wVarArr);
    }

    public static w valueOf(String str) {
        return (w) Enum.valueOf(w.class, str);
    }

    public static w[] values() {
        return (w[]) f62948e.clone();
    }
}
