package f1;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes3.dex */
public final class x {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final /* synthetic */ x[] f16110b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f16111c;

    /* JADX INFO: Fake field, exist only in values array */
    x EF5;

    static {
        x[] xVarArr = {new x("BodyLarge", 0), new x("BodyMedium", 1), new x("BodySmall", 2), new x("DisplayLarge", 3), new x("DisplayMedium", 4), new x("DisplaySmall", 5), new x("HeadlineLarge", 6), new x("HeadlineMedium", 7), new x("HeadlineSmall", 8), new x("LabelLarge", 9), new x("LabelMedium", 10), new x("LabelSmall", 11), new x("TitleLarge", 12), new x("TitleMedium", 13), new x("TitleSmall", 14), new x("BodyLargeEmphasized", 15), new x("BodyMediumEmphasized", 16), new x("BodySmallEmphasized", 17), new x("DisplayLargeEmphasized", 18), new x("DisplayMediumEmphasized", 19), new x("DisplaySmallEmphasized", 20), new x("HeadlineLargeEmphasized", 21), new x("HeadlineMediumEmphasized", 22), new x("HeadlineSmallEmphasized", 23), new x("LabelLargeEmphasized", 24), new x("LabelMediumEmphasized", 25), new x("LabelSmallEmphasized", 26), new x("TitleLargeEmphasized", 27), new x("TitleMediumEmphasized", 28), new x("TitleSmallEmphasized", 29)};
        f16110b = xVarArr;
        f16111c = EnumEntriesKt.enumEntries(xVarArr);
    }

    public static x valueOf(String str) {
        return (x) Enum.valueOf(x.class, str);
    }

    public static x[] values() {
        return (x[]) f16110b.clone();
    }
}
