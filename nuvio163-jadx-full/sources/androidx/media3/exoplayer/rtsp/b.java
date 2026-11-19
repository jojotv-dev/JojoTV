package androidx.media3.exoplayer.rtsp;

import java.util.Comparator;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class b implements Comparator {
    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        return RtpPacketReorderingQueue.lambda$new$0((RtpPacketReorderingQueue.RtpPacketContainer) obj, (RtpPacketReorderingQueue.RtpPacketContainer) obj2);
    }
}
