package sf;

import com.nuvio.tv.domain.model.DiscoverLocation;
import com.nuvio.tv.domain.model.HomeLayout;

/* JADX INFO: loaded from: classes4.dex */
public abstract /* synthetic */ class j3 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final /* synthetic */ int[] f55289a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final /* synthetic */ int[] f55290b;

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
        f55289a = iArr;
        int[] iArr2 = new int[DiscoverLocation.values().length];
        try {
            iArr2[DiscoverLocation.OFF.ordinal()] = 1;
        } catch (NoSuchFieldError unused4) {
        }
        try {
            iArr2[DiscoverLocation.IN_SEARCH.ordinal()] = 2;
        } catch (NoSuchFieldError unused5) {
        }
        try {
            iArr2[DiscoverLocation.IN_SIDEBAR.ordinal()] = 3;
        } catch (NoSuchFieldError unused6) {
        }
        f55290b = iArr2;
    }
}
