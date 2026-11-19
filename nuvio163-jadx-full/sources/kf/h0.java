package kf;

import com.nuvio.tv.domain.model.ContinueWatchingCardStyle;

/* JADX INFO: loaded from: classes4.dex */
public abstract /* synthetic */ class h0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final /* synthetic */ int[] f29146a;

    static {
        int[] iArr = new int[ContinueWatchingCardStyle.values().length];
        try {
            iArr[ContinueWatchingCardStyle.POSTER.ordinal()] = 1;
        } catch (NoSuchFieldError unused) {
        }
        try {
            iArr[ContinueWatchingCardStyle.WIDE.ordinal()] = 2;
        } catch (NoSuchFieldError unused2) {
        }
        try {
            iArr[ContinueWatchingCardStyle.CARD.ordinal()] = 3;
        } catch (NoSuchFieldError unused3) {
        }
        f29146a = iArr;
    }
}
