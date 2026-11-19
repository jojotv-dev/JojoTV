package kf;

import com.nuvio.tv.domain.model.ContinueWatchingSortMode;

/* JADX INFO: loaded from: classes.dex */
public abstract /* synthetic */ class a7 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final /* synthetic */ int[] f28384a;

    static {
        int[] iArr = new int[ContinueWatchingSortMode.values().length];
        try {
            iArr[ContinueWatchingSortMode.DEFAULT.ordinal()] = 1;
        } catch (NoSuchFieldError unused) {
        }
        try {
            iArr[ContinueWatchingSortMode.SPLIT_UPCOMING.ordinal()] = 2;
        } catch (NoSuchFieldError unused2) {
        }
        try {
            iArr[ContinueWatchingSortMode.STREAMING_STYLE.ordinal()] = 3;
        } catch (NoSuchFieldError unused3) {
        }
        f28384a = iArr;
    }
}
