package org.bouncycastle.crypto.digests;

import kotlin.jvm.internal.ByteCompanionObject;

/* JADX INFO: loaded from: classes5.dex */
public abstract class b implements ek.o, rn.e {
    private static final int BYTE_LENGTH = 64;
    private long byteCount;
    protected final ek.f purpose;
    private final byte[] xBuf;
    private int xBufOff;

    public b(ek.f fVar) {
        this.xBuf = new byte[4];
        this.purpose = fVar;
        this.xBufOff = 0;
    }

    public void copyIn(b bVar) {
        byte[] bArr = bVar.xBuf;
        System.arraycopy(bArr, 0, this.xBuf, 0, bArr.length);
        this.xBufOff = bVar.xBufOff;
        this.byteCount = bVar.byteCount;
    }

    public void finish() {
        long j10 = this.byteCount << 3;
        byte b5 = ByteCompanionObject.MIN_VALUE;
        while (true) {
            update(b5);
            if (this.xBufOff == 0) {
                processLength(j10);
                processBlock();
                return;
            }
            b5 = 0;
        }
    }

    @Override // ek.o
    public int getByteLength() {
        return 64;
    }

    public void populateState(byte[] bArr) {
        System.arraycopy(this.xBuf, 0, bArr, 0, this.xBufOff);
        rn.d.p(this.xBufOff, 4, bArr);
        rn.d.v(this.byteCount, bArr, 8);
    }

    public abstract void processBlock();

    public abstract void processLength(long j10);

    public abstract void processWord(byte[] bArr, int i10);

    @Override // ek.n
    public void reset() {
        this.byteCount = 0L;
        this.xBufOff = 0;
        int i10 = 0;
        while (true) {
            byte[] bArr = this.xBuf;
            if (i10 >= bArr.length) {
                return;
            }
            bArr[i10] = 0;
            i10++;
        }
    }

    @Override // ek.n
    public void update(byte b5) {
        byte[] bArr = this.xBuf;
        int i10 = this.xBufOff;
        int i11 = i10 + 1;
        this.xBufOff = i11;
        bArr[i10] = b5;
        if (i11 == bArr.length) {
            processWord(bArr, 0);
            this.xBufOff = 0;
        }
        this.byteCount++;
    }

    public b(b bVar) {
        this.xBuf = new byte[4];
        this.purpose = bVar.purpose;
        copyIn(bVar);
    }

    @Override // ek.n
    public void update(byte[] bArr, int i10, int i11) {
        int i12 = 0;
        int iMax = Math.max(0, i11);
        if (this.xBufOff != 0) {
            int i13 = 0;
            while (true) {
                if (i13 >= iMax) {
                    i12 = i13;
                    break;
                }
                byte[] bArr2 = this.xBuf;
                int i14 = this.xBufOff;
                int i15 = i14 + 1;
                this.xBufOff = i15;
                int i16 = i13 + 1;
                bArr2[i14] = bArr[i13 + i10];
                if (i15 == 4) {
                    processWord(bArr2, 0);
                    this.xBufOff = 0;
                    i12 = i16;
                    break;
                }
                i13 = i16;
            }
        }
        int i17 = iMax - 3;
        while (i12 < i17) {
            processWord(bArr, i10 + i12);
            i12 += 4;
        }
        while (i12 < iMax) {
            byte[] bArr3 = this.xBuf;
            int i18 = this.xBufOff;
            this.xBufOff = i18 + 1;
            bArr3[i18] = bArr[i12 + i10];
            i12++;
        }
        this.byteCount += (long) iMax;
    }
}
