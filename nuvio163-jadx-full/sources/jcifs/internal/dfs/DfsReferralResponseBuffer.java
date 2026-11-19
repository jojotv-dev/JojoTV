package jcifs.internal.dfs;

import java.util.Arrays;
import jcifs.Decodable;
import jcifs.internal.util.SMBUtil;

/* JADX INFO: loaded from: classes5.dex */
public class DfsReferralResponseBuffer implements Decodable {
    private int numReferrals;
    private int pathConsumed;
    private Referral[] referrals;
    private int tflags;

    @Override // jcifs.Decodable
    public int decode(byte[] bArr, int i10, int i11) {
        this.pathConsumed = SMBUtil.readInt2(bArr, i10) / 2;
        this.numReferrals = SMBUtil.readInt2(bArr, i10 + 2);
        this.tflags = SMBUtil.readInt2(bArr, i10 + 4);
        int iDecode = i10 + 8;
        this.referrals = new Referral[this.numReferrals];
        for (int i12 = 0; i12 < this.numReferrals; i12++) {
            this.referrals[i12] = new Referral();
            iDecode += this.referrals[i12].decode(bArr, iDecode, i11);
        }
        return iDecode - i10;
    }

    public final int getNumReferrals() {
        return this.numReferrals;
    }

    public final int getPathConsumed() {
        return this.pathConsumed;
    }

    public final Referral[] getReferrals() {
        return this.referrals;
    }

    public final int getTflags() {
        return this.tflags;
    }

    public String toString() {
        return "pathConsumed=" + this.pathConsumed + ",numReferrals=" + this.numReferrals + ",flags=" + this.tflags + ",referrals=" + Arrays.toString(this.referrals);
    }
}
