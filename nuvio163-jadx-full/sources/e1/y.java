package e1;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes3.dex */
public final class y {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final /* synthetic */ y[] f13723b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f13724c;

    /* JADX INFO: Fake field, exist only in values array */
    y EF5;

    static {
        y[] yVarArr = {new y("Filled", 0), new y("Outlined", 1)};
        f13723b = yVarArr;
        f13724c = EnumEntriesKt.enumEntries(yVarArr);
    }

    public static y valueOf(String str) {
        return (y) Enum.valueOf(y.class, str);
    }

    public static y[] values() {
        return (y[]) f13723b.clone();
    }
}
