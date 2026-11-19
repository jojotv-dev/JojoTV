package com.liskovsoft.youtubeapi.browse.v1.models.guide;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class Guide {

    @JsonPath({"$.items[*].guideSectionRenderer.items[*].guideEntryRenderer"})
    private List<GuideItem> mItems;
    private String mSuggestToken;

    @JsonPath({"$.responseContext.serviceTrackingParams[*]"})
    private List<TrackingParam> mTrackingParams;

    public List<GuideItem> getItems() {
        return this.mItems;
    }

    public String getSuggestToken() {
        String str = this.mSuggestToken;
        if (str != null) {
            return str;
        }
        List<TrackingParam> list = this.mTrackingParams;
        if (list != null) {
            for (TrackingParam trackingParam : list) {
                if (TrackingParam.SERVICE_SUGGEST.equals(trackingParam.getService())) {
                    if (trackingParam.getParams() == null) {
                        break;
                    }
                    for (TrackingParam.Param param : trackingParam.getParams()) {
                        if (TrackingParam.Param.KEY_SUGGEST_TOKEN.equals(param.getKey())) {
                            this.mSuggestToken = param.getValue();
                            break;
                        }
                    }
                    break;
                }
            }
        }
        return this.mSuggestToken;
    }
}
