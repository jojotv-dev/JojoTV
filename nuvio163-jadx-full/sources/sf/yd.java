package sf;

import com.nuvio.tv.domain.model.ProfileHomePresentation;

/* JADX INFO: loaded from: classes4.dex */
public abstract /* synthetic */ class yd {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final /* synthetic */ int[] f56599a;

    static {
        int[] iArr = new int[ProfileHomePresentation.values().length];
        try {
            iArr[ProfileHomePresentation.ORIGINAL.ordinal()] = 1;
        } catch (NoSuchFieldError unused) {
        }
        try {
            iArr[ProfileHomePresentation.LAUNCHER.ordinal()] = 2;
        } catch (NoSuchFieldError unused2) {
        }
        f56599a = iArr;
    }
}
