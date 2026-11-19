package af;

import com.nuvio.tv.domain.model.HomeLayout;

/* JADX INFO: loaded from: classes4.dex */
public abstract /* synthetic */ class i0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final /* synthetic */ int[] f1098a;

    static {
        int[] iArr = new int[HomeLayout.values().length];
        try {
            iArr[HomeLayout.CLASSIC.ordinal()] = 1;
        } catch (NoSuchFieldError unused) {
        }
        try {
            iArr[HomeLayout.GRID.ordinal()] = 2;
        } catch (NoSuchFieldError unused2) {
        }
        try {
            iArr[HomeLayout.MODERN.ordinal()] = 3;
        } catch (NoSuchFieldError unused3) {
        }
        f1098a = iArr;
    }
}
