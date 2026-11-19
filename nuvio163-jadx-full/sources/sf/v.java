package sf;

import com.nuvio.tv.domain.model.AppIconOption;

/* JADX INFO: loaded from: classes4.dex */
public abstract /* synthetic */ class v {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final /* synthetic */ int[] f56318a;

    static {
        int[] iArr = new int[AppIconOption.values().length];
        try {
            iArr[AppIconOption.ORIGINAL.ordinal()] = 1;
        } catch (NoSuchFieldError unused) {
        }
        try {
            iArr[AppIconOption.ARCTIC_BLUE.ordinal()] = 2;
        } catch (NoSuchFieldError unused2) {
        }
        try {
            iArr[AppIconOption.EMERALD.ordinal()] = 3;
        } catch (NoSuchFieldError unused3) {
        }
        try {
            iArr[AppIconOption.ROSE_GOLD.ordinal()] = 4;
        } catch (NoSuchFieldError unused4) {
        }
        try {
            iArr[AppIconOption.COPPER.ordinal()] = 5;
        } catch (NoSuchFieldError unused5) {
        }
        try {
            iArr[AppIconOption.GRAPHITE.ordinal()] = 6;
        } catch (NoSuchFieldError unused6) {
        }
        f56318a = iArr;
    }
}
