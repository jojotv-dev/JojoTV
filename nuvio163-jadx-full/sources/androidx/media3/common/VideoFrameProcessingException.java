package androidx.media3.common;

import androidx.media3.common.util.UnstableApi;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public final class VideoFrameProcessingException extends Exception {
    public final long presentationTimeUs;

    public VideoFrameProcessingException(String str) {
        this(str, C.TIME_UNSET);
    }

    public static VideoFrameProcessingException from(Exception exc) {
        return from(exc, C.TIME_UNSET);
    }

    public VideoFrameProcessingException(String str, long j10) {
        super(str);
        this.presentationTimeUs = j10;
    }

    public static VideoFrameProcessingException from(Exception exc, long j10) {
        return exc instanceof VideoFrameProcessingException ? (VideoFrameProcessingException) exc : new VideoFrameProcessingException(exc, j10);
    }

    public VideoFrameProcessingException(String str, Throwable th2) {
        this(str, th2, C.TIME_UNSET);
    }

    public VideoFrameProcessingException(String str, Throwable th2, long j10) {
        super(str, th2);
        this.presentationTimeUs = j10;
    }

    public VideoFrameProcessingException(Throwable th2) {
        this(th2, C.TIME_UNSET);
    }

    public VideoFrameProcessingException(Throwable th2, long j10) {
        super(th2);
        this.presentationTimeUs = j10;
    }
}
