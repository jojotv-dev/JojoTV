package sf;

import com.nuvio.tv.domain.model.ContinueWatchingCardStyle;
import com.nuvio.tv.domain.model.ContinueWatchingSortMode;

/* JADX INFO: loaded from: classes4.dex */
public abstract /* synthetic */ class e3 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final /* synthetic */ int[] f54774a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final /* synthetic */ int[] f54775b;

    static {
        int[] iArr = new int[ContinueWatchingSortMode.values().length];
        try {
            iArr[ContinueWatchingSortMode.DEFAULT.ordinal()] = 1;
        } catch (NoSuchFieldError unused) {
        }
        try {
            iArr[ContinueWatchingSortMode.STREAMING_STYLE.ordinal()] = 2;
        } catch (NoSuchFieldError unused2) {
        }
        try {
            iArr[ContinueWatchingSortMode.SPLIT_UPCOMING.ordinal()] = 3;
        } catch (NoSuchFieldError unused3) {
        }
        f54774a = iArr;
        int[] iArr2 = new int[ContinueWatchingCardStyle.values().length];
        try {
            iArr2[ContinueWatchingCardStyle.CARD.ordinal()] = 1;
        } catch (NoSuchFieldError unused4) {
        }
        try {
            iArr2[ContinueWatchingCardStyle.WIDE.ordinal()] = 2;
        } catch (NoSuchFieldError unused5) {
        }
        try {
            iArr2[ContinueWatchingCardStyle.POSTER.ordinal()] = 3;
        } catch (NoSuchFieldError unused6) {
        }
        f54775b = iArr2;
    }
}
