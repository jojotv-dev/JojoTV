package sf;

import com.nuvio.tv.domain.model.SettingsUiStyle;

/* JADX INFO: loaded from: classes4.dex */
public abstract /* synthetic */ class pe {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final /* synthetic */ int[] f55904a;

    static {
        int[] iArr = new int[SettingsUiStyle.values().length];
        try {
            iArr[SettingsUiStyle.CLASSIC.ordinal()] = 1;
        } catch (NoSuchFieldError unused) {
        }
        try {
            iArr[SettingsUiStyle.ZEN.ordinal()] = 2;
        } catch (NoSuchFieldError unused2) {
        }
        try {
            iArr[SettingsUiStyle.HORIZON.ordinal()] = 3;
        } catch (NoSuchFieldError unused3) {
        }
        f55904a = iArr;
    }
}
