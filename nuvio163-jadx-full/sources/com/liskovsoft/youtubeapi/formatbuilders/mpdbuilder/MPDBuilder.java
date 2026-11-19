package com.liskovsoft.youtubeapi.formatbuilders.mpdbuilder;

import com.liskovsoft.mediaserviceinterfaces.data.MediaFormat;
import com.liskovsoft.mediaserviceinterfaces.data.MediaSubtitle;
import java.io.InputStream;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public interface MPDBuilder {
    public static final String AUDIO_MP4 = "audio/mp4";
    public static final String VIDEO_MP4 = "video/mp4";

    void append(MediaFormat mediaFormat);

    void append(MediaSubtitle mediaSubtitle);

    void append(List<MediaSubtitle> list);

    InputStream build();

    boolean isDynamic();

    boolean isEmpty();

    void limitAudioCodec(String str);

    void limitVideoCodec(String str);
}
