package androidx.media3.exoplayer.rtsp;

import androidx.media3.datasource.DataSource;

/* JADX INFO: loaded from: classes3.dex */
interface RtpDataChannel extends DataSource {

    public interface Factory {
        RtpDataChannel createAndOpenDataChannel(int i10);

        default Factory createFallbackDataChannelFactory() {
            return null;
        }
    }

    RtspMessageChannel.InterleavedBinaryDataListener getInterleavedBinaryDataListener();

    int getLocalPort();

    String getTransport();

    boolean needsClosingOnLoadCompletion();
}
