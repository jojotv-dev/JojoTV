package sf;

import com.nuvio.tv.domain.model.MemberTier;

/* JADX INFO: loaded from: classes4.dex */
public abstract /* synthetic */ class qh {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final /* synthetic */ int[] f56007a;

    static {
        int[] iArr = new int[sh.values().length];
        try {
            iArr[0] = 1;
        } catch (NoSuchFieldError unused) {
        }
        try {
            sh shVar = sh.f56151b;
            iArr[1] = 2;
        } catch (NoSuchFieldError unused2) {
        }
        try {
            sh shVar2 = sh.f56151b;
            iArr[2] = 3;
        } catch (NoSuchFieldError unused3) {
        }
        int[] iArr2 = new int[MemberTier.values().length];
        try {
            iArr2[MemberTier.SUPPORTER.ordinal()] = 1;
        } catch (NoSuchFieldError unused4) {
        }
        try {
            iArr2[MemberTier.SUPPORTER_PLUS.ordinal()] = 2;
        } catch (NoSuchFieldError unused5) {
        }
        f56007a = iArr2;
    }
}
