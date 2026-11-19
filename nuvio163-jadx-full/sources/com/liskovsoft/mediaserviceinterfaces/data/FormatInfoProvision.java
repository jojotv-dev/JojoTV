package com.liskovsoft.mediaserviceinterfaces.data;

/* JADX INFO: Access modifiers changed from: package-private */
/* JADX INFO: loaded from: classes4.dex */
public interface FormatInfoProvision {
    String getClickTrackingParams();

    String getEventId();

    String getOfParam();

    String getVisitorMonitoringData();

    boolean isAuth();

    boolean isCacheActual();

    boolean isSynced();

    void setClickTrackingParams(String str);

    void sync(MediaItemFormatInfo mediaItemFormatInfo);
}
