package ue;

import com.nuvio.tv.domain.model.RottenTomatoesStatus;

/* JADX INFO: loaded from: classes4.dex */
public abstract /* synthetic */ class y3 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final /* synthetic */ int[] f59656a;

    static {
        int[] iArr = new int[RottenTomatoesStatus.values().length];
        try {
            iArr[RottenTomatoesStatus.VERIFIED_HOT.ordinal()] = 1;
        } catch (NoSuchFieldError unused) {
        }
        try {
            iArr[RottenTomatoesStatus.STALE.ordinal()] = 2;
        } catch (NoSuchFieldError unused2) {
        }
        try {
            iArr[RottenTomatoesStatus.CERTIFIED_FRESH.ordinal()] = 3;
        } catch (NoSuchFieldError unused3) {
        }
        try {
            iArr[RottenTomatoesStatus.ROTTEN.ordinal()] = 4;
        } catch (NoSuchFieldError unused4) {
        }
        f59656a = iArr;
    }
}
