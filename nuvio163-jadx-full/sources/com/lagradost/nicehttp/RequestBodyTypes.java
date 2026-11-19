package com.lagradost.nicehttp;

import androidx.media3.container.NalUnitUtil;
import com.fasterxml.jackson.core.JsonFactory;
import kotlin.Metadata;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000¨\u0006\u0007"}, d2 = {"Lcom/lagradost/nicehttp/RequestBodyTypes;", "", "<init>", "()V", JsonFactory.FORMAT_NAME_JSON, "", "TEXT", "library"}, k = 1, mv = {2, 2, 0}, xi = NalUnitUtil.H265_NAL_UNIT_TYPE_UNSPECIFIED)
public final class RequestBodyTypes {
    public static final RequestBodyTypes INSTANCE = new RequestBodyTypes();
    public static final String JSON = "application/json;charset=utf-8";
    public static final String TEXT = "text/plain;charset=utf-8";

    private RequestBodyTypes() {
    }
}
