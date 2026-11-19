package jcifs.smb;

import java.io.Serializable;
import jcifs.netbios.UniAddress;
import jcifs.util.Hexdump;

/* JADX INFO: loaded from: classes5.dex */
public final class NtlmChallenge implements Serializable {
    private static final long serialVersionUID = 2484853610174848092L;
    public byte[] challenge;

    /* JADX INFO: renamed from: dc, reason: collision with root package name */
    public UniAddress f27278dc;

    public NtlmChallenge(byte[] bArr, UniAddress uniAddress) {
        this.challenge = bArr;
        this.f27278dc = uniAddress;
    }

    public String toString() {
        StringBuilder sb2 = new StringBuilder("NtlmChallenge[challenge=0x");
        byte[] bArr = this.challenge;
        sb2.append(Hexdump.toHexString(bArr, 0, bArr.length * 2));
        sb2.append(",dc=");
        sb2.append(this.f27278dc.toString());
        sb2.append("]");
        return sb2.toString();
    }
}
