package io.github.jan.supabase.auth;

import androidx.media3.container.NalUnitUtil;
import kotlin.Metadata;

/* JADX INFO: loaded from: classes5.dex */
@Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\bÀ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000¨\u0006\b"}, d2 = {"Lio/github/jan/supabase/auth/PKCEConstants;", "", "<init>", "()V", "VERIFIER_LENGTH", "", "CHALLENGE_METHOD", "", "auth-kt_release"}, k = 1, mv = {2, 2, 0}, xi = NalUnitUtil.H265_NAL_UNIT_TYPE_UNSPECIFIED)
public final class PKCEConstants {
    public static final String CHALLENGE_METHOD = "s256";
    public static final PKCEConstants INSTANCE = new PKCEConstants();
    public static final int VERIFIER_LENGTH = 64;

    private PKCEConstants() {
    }
}
