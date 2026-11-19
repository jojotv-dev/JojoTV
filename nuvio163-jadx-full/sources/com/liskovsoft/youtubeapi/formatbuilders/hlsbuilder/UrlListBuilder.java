package com.liskovsoft.youtubeapi.formatbuilders.hlsbuilder;

import com.liskovsoft.mediaserviceinterfaces.data.MediaFormat;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public interface UrlListBuilder {
    void append(MediaFormat mediaFormat);

    List<String> buildUriList();

    boolean isEmpty();
}
