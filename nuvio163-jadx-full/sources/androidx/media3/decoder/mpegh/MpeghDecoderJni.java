package androidx.media3.decoder.mpegh;

import java.nio.ByteBuffer;

/* JADX INFO: loaded from: classes3.dex */
public class MpeghDecoderJni {
    private long decoderHandle;

    public native void destroy();

    public native void flush();

    public native void flushAndGet();

    public native int getNumChannels();

    public native long getPts();

    public native int getSamplerate();

    public native int getSamples(ByteBuffer byteBuffer, int i10);

    public native void init(int i10, byte[] bArr, int i11);

    public native void process(ByteBuffer byteBuffer, int i10, long j10);
}
