package com.nuvio.tv.data.remote.supabase;

import androidx.media3.container.NalUnitUtil;
import kotlin.Metadata;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000\u001a\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\u001a\u0018\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00042\u0006\u0010\t\u001a\u00020\u0004H\u0000\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0005\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000¨\u0006\n"}, d2 = {"MemberAvatarBucket", "", "MemberAvatarTag", "AvatarCatalogRefreshIntervalMs", "", "BundledAvatarCatalogFile", "isAvatarCatalogRefreshDue", "", "lastRefreshAtMs", "nowMs", "app_fullRelease"}, k = 2, mv = {2, 3, 0}, xi = NalUnitUtil.H265_NAL_UNIT_TYPE_UNSPECIFIED)
public final class AvatarRepositoryKt {
    private static final long AvatarCatalogRefreshIntervalMs = 900000;
    private static final String BundledAvatarCatalogFile = "custom_avatar_catalog.json";
    private static final String MemberAvatarBucket = "membership-profile-avatars";
    private static final String MemberAvatarTag = "MemberAvatars";

    public static final boolean isAvatarCatalogRefreshDue(long j10, long j11) {
        return j10 <= 0 || j11 < j10 || j11 - j10 >= AvatarCatalogRefreshIntervalMs;
    }
}
