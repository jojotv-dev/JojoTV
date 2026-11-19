package ue;

import com.nuvio.tv.domain.model.MemberTier;

/* JADX INFO: loaded from: classes4.dex */
public abstract /* synthetic */ class b4 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final /* synthetic */ int[] f58479a;

    static {
        int[] iArr = new int[MemberTier.values().length];
        try {
            iArr[MemberTier.SUPPORTER.ordinal()] = 1;
        } catch (NoSuchFieldError unused) {
        }
        try {
            iArr[MemberTier.SUPPORTER_PLUS.ordinal()] = 2;
        } catch (NoSuchFieldError unused2) {
        }
        f58479a = iArr;
    }
}
