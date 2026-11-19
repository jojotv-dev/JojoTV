package com.liskovsoft.youtubeapi.dearrow;

import androidx.media3.container.NalUnitUtil;
import kotlin.Metadata;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\bÀ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000¨\u0006\u0007"}, d2 = {"Lcom/liskovsoft/youtubeapi/dearrow/DeArrowApiHelper;", "", "<init>", "()V", "BRANDING_URL", "", "THUMBNAIL_URL", "youtubeapi_release"}, k = 1, mv = {2, 3, 0}, xi = NalUnitUtil.H265_NAL_UNIT_TYPE_UNSPECIFIED)
public final class DeArrowApiHelper {
    public static final String BRANDING_URL = "https://sponsor.ajay.app/api/branding";
    public static final DeArrowApiHelper INSTANCE = new DeArrowApiHelper();
    public static final String THUMBNAIL_URL = "https://dearrow-thumb.ajay.app/api/v1/getThumbnail";

    private DeArrowApiHelper() {
    }
}
