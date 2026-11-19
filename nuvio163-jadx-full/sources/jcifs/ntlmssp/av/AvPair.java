package jcifs.ntlmssp.av;

/* JADX INFO: loaded from: classes5.dex */
public class AvPair {
    public static final int MsvAvChannelBindings = 10;
    public static final int MsvAvEOL = 0;
    public static final int MsvAvFlags = 6;
    public static final int MsvAvSingleHost = 8;
    public static final int MsvAvTargetName = 9;
    public static final int MsvAvTimestamp = 7;
    private final byte[] raw;
    private final int type;

    public AvPair(int i10, byte[] bArr) {
        this.type = i10;
        this.raw = bArr;
    }

    public final byte[] getRaw() {
        return this.raw;
    }

    public final int getType() {
        return this.type;
    }
}
