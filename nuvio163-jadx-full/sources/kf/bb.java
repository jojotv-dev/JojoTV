package kf;

import com.nuvio.tv.domain.model.ContinueWatchingCardStyle;

/* JADX INFO: loaded from: classes.dex */
public abstract /* synthetic */ class bb {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final /* synthetic */ int[] f28493a;

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
        f28493a = iArr;
    }
}
