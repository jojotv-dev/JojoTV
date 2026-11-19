package jcifs.ntlmssp.av;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/* JADX INFO: loaded from: classes5.dex */
public class AvTargetName extends AvPair {
    private static final Charset UTF16LE = StandardCharsets.UTF_16LE;

    public AvTargetName(byte[] bArr) {
        super(9, bArr);
    }

    private static byte[] encode(String str) {
        return str.getBytes(UTF16LE);
    }

    public String getTargetName() {
        return new String(getRaw(), UTF16LE);
    }

    public AvTargetName(String str) {
        this(encode(str));
    }
}
