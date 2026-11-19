package sf;

import com.nuvio.tv.domain.model.AppTheme;
import com.nuvio.tv.domain.model.SettingsUiStyle;

/* JADX INFO: loaded from: classes4.dex */
public abstract /* synthetic */ class ki {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final /* synthetic */ int[] f55444a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final /* synthetic */ int[] f55445b;

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
        f55444a = iArr;
        int[] iArr2 = new int[AppTheme.values().length];
        try {
            iArr2[AppTheme.CUSTOM.ordinal()] = 1;
        } catch (NoSuchFieldError unused4) {
        }
        try {
            iArr2[AppTheme.GOLD.ordinal()] = 2;
        } catch (NoSuchFieldError unused5) {
        }
        try {
            iArr2[AppTheme.JADE.ordinal()] = 3;
        } catch (NoSuchFieldError unused6) {
        }
        try {
            iArr2[AppTheme.ROSE_GOLD.ordinal()] = 4;
        } catch (NoSuchFieldError unused7) {
        }
        try {
            iArr2[AppTheme.ARCTIC_BLUE.ordinal()] = 5;
        } catch (NoSuchFieldError unused8) {
        }
        try {
            iArr2[AppTheme.GRAPHITE.ordinal()] = 6;
        } catch (NoSuchFieldError unused9) {
        }
        try {
            iArr2[AppTheme.CRIMSON.ordinal()] = 7;
        } catch (NoSuchFieldError unused10) {
        }
        try {
            iArr2[AppTheme.OCEAN.ordinal()] = 8;
        } catch (NoSuchFieldError unused11) {
        }
        try {
            iArr2[AppTheme.VIOLET.ordinal()] = 9;
        } catch (NoSuchFieldError unused12) {
        }
        try {
            iArr2[AppTheme.EMERALD.ordinal()] = 10;
        } catch (NoSuchFieldError unused13) {
        }
        try {
            iArr2[AppTheme.AMBER.ordinal()] = 11;
        } catch (NoSuchFieldError unused14) {
        }
        try {
            iArr2[AppTheme.ROSE.ordinal()] = 12;
        } catch (NoSuchFieldError unused15) {
        }
        try {
            iArr2[AppTheme.WHITE.ordinal()] = 13;
        } catch (NoSuchFieldError unused16) {
        }
        f55445b = iArr2;
    }
}
