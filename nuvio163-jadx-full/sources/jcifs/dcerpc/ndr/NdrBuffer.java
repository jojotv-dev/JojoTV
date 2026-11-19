package jcifs.dcerpc.ndr;

import java.util.HashMap;
import java.util.Map;
import jcifs.util.Encdec;
import jcifs.util.Strings;

/* JADX INFO: loaded from: classes5.dex */
public class NdrBuffer {
    public byte[] buf;
    public int index;
    private int referent;
    private Map<Object, Entry> referents;
    public int start;
    public int length = 0;
    public NdrBuffer deferred = this;

    public static class Entry {
        private final Object obj;
        final int referent;

        public Entry(int i10, Object obj) {
            this.referent = i10;
            this.obj = obj;
        }
    }

    public NdrBuffer(byte[] bArr, int i10) {
        this.buf = bArr;
        this.index = i10;
        this.start = i10;
    }

    private int getDceReferent(Object obj) {
        if (this.referents == null) {
            this.referents = new HashMap();
            this.referent = 1;
        }
        Entry entry = this.referents.get(obj);
        if (entry == null) {
            int i10 = this.referent;
            this.referent = i10 + 1;
            entry = new Entry(i10, obj);
            this.referents.put(obj, entry);
        }
        return entry.referent;
    }

    public void advance(int i10) {
        int i11 = this.index + i10;
        this.index = i11;
        int i12 = this.start;
        int i13 = i11 - i12;
        NdrBuffer ndrBuffer = this.deferred;
        if (i13 > ndrBuffer.length) {
            ndrBuffer.length = i11 - i12;
        }
    }

    public int align(int i10, byte b5) {
        int iAlign = align(i10);
        for (int i11 = iAlign; i11 > 0; i11--) {
            this.buf[this.index - i11] = b5;
        }
        return iAlign;
    }

    public long dec_ndr_hyper() {
        align(8);
        long jDec_uint64le = Encdec.dec_uint64le(this.buf, this.index);
        advance(8);
        return jDec_uint64le;
    }

    public int dec_ndr_long() {
        align(4);
        int iDec_uint32le = Encdec.dec_uint32le(this.buf, this.index);
        advance(4);
        return iDec_uint32le;
    }

    public int dec_ndr_short() {
        align(2);
        short sDec_uint16le = Encdec.dec_uint16le(this.buf, this.index);
        advance(2);
        return sDec_uint16le;
    }

    public int dec_ndr_small() {
        int i10 = this.buf[this.index] & 255;
        advance(1);
        return i10;
    }

    public String dec_ndr_string() throws NdrException {
        String strFromUNIBytes;
        align(4);
        int i10 = this.index;
        int iDec_uint32le = Encdec.dec_uint32le(this.buf, i10);
        int i11 = i10 + 12;
        if (iDec_uint32le != 0) {
            int i12 = (iDec_uint32le - 1) * 2;
            if (i12 < 0 || i12 > 65535) {
                throw new NdrException(NdrException.INVALID_CONFORMANCE);
            }
            strFromUNIBytes = Strings.fromUNIBytes(this.buf, i11, i12);
            i11 += i12 + 2;
        } else {
            strFromUNIBytes = null;
        }
        advance(i11 - this.index);
        return strFromUNIBytes;
    }

    public NdrBuffer derive(int i10) {
        NdrBuffer ndrBuffer = new NdrBuffer(this.buf, this.start);
        ndrBuffer.index = i10;
        ndrBuffer.deferred = this.deferred;
        return ndrBuffer;
    }

    public void enc_ndr_hyper(long j10) {
        align(8);
        Encdec.enc_uint64le(j10, this.buf, this.index);
        advance(8);
    }

    public void enc_ndr_long(int i10) {
        align(4);
        Encdec.enc_uint32le(i10, this.buf, this.index);
        advance(4);
    }

    public void enc_ndr_referent(Object obj, int i10) {
        if (obj == null) {
            enc_ndr_long(0);
            return;
        }
        if (i10 != 1) {
            if (i10 == 2) {
                enc_ndr_long(getDceReferent(obj));
                return;
            } else if (i10 != 3) {
                return;
            }
        }
        enc_ndr_long(System.identityHashCode(obj));
    }

    public void enc_ndr_short(int i10) {
        align(2);
        Encdec.enc_uint16le((short) i10, this.buf, this.index);
        advance(2);
    }

    public void enc_ndr_small(int i10) {
        this.buf[this.index] = (byte) (i10 & 255);
        advance(1);
    }

    public void enc_ndr_string(String str) {
        align(4);
        int i10 = this.index;
        int length = str.length();
        int i11 = length + 1;
        Encdec.enc_uint32le(i11, this.buf, i10);
        Encdec.enc_uint32le(0, this.buf, i10 + 4);
        Encdec.enc_uint32le(i11, this.buf, i10 + 8);
        int i12 = i10 + 12;
        int i13 = length * 2;
        System.arraycopy(Strings.getUNIBytes(str), 0, this.buf, i12, i13);
        int i14 = i12 + i13;
        byte[] bArr = this.buf;
        bArr[i14] = 0;
        bArr[i14 + 1] = 0;
        advance((i14 + 2) - this.index);
    }

    public byte[] getBuffer() {
        return this.buf;
    }

    public int getCapacity() {
        return this.buf.length - this.start;
    }

    public int getIndex() {
        return this.index;
    }

    public int getLength() {
        return this.deferred.length;
    }

    public int getTailSpace() {
        return this.buf.length - this.index;
    }

    public void readOctetArray(byte[] bArr, int i10, int i11) {
        System.arraycopy(this.buf, this.index, bArr, i10, i11);
        advance(i11);
    }

    public void reset() {
        this.index = this.start;
        this.length = 0;
        this.deferred = this;
    }

    public void setIndex(int i10) {
        this.index = i10;
    }

    public void setLength(int i10) {
        this.deferred.length = i10;
    }

    public String toString() {
        return "start=" + this.start + ",index=" + this.index + ",length=" + getLength();
    }

    public void writeOctetArray(byte[] bArr, int i10, int i11) {
        System.arraycopy(bArr, i10, this.buf, this.index, i11);
        advance(i11);
    }

    public int align(int i10) {
        int i11 = i10 - 1;
        int i12 = this.index - this.start;
        int i13 = ((~i11) & (i12 + i11)) - i12;
        advance(i13);
        return i13;
    }
}
