package jcifs.pac;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/* JADX INFO: loaded from: classes5.dex */
public class PacSignature {
    public static final int ETYPE_AES128_CTS_HMAC_SHA1_96 = 17;
    public static final int ETYPE_AES256_CTS_HMAC_SHA1_96 = 18;
    public static final int ETYPE_ARCFOUR_HMAC = 23;
    public static final int HMAC_SHA1_96_AES128 = 15;
    public static final int HMAC_SHA1_96_AES256 = 16;
    public static final int KERB_CHECKSUM_HMAC_MD5 = -138;
    private byte[] checksum;
    private int type;

    public PacSignature(byte[] bArr) throws PACDecodingException {
        try {
            PacDataInputStream pacDataInputStream = new PacDataInputStream(new DataInputStream(new ByteArrayInputStream(bArr)));
            int i10 = pacDataInputStream.readInt();
            this.type = i10;
            if (i10 == -138) {
                this.checksum = new byte[16];
            } else if (i10 == 15 || i10 == 16) {
                this.checksum = new byte[12];
            } else {
                this.checksum = new byte[pacDataInputStream.available()];
            }
            pacDataInputStream.readFully(this.checksum);
        } catch (IOException e10) {
            throw new PACDecodingException("Malformed PAC signature", e10);
        }
    }

    public byte[] getChecksum() {
        return this.checksum;
    }

    public int getType() {
        return this.type;
    }
}
