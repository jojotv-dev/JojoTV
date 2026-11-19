package jcifs.dcerpc;

import io.ktor.util.internal.a;
import jcifs.dcerpc.ndr.NdrBuffer;
import jcifs.dcerpc.ndr.NdrException;
import jcifs.dcerpc.ndr.NdrObject;

/* JADX INFO: loaded from: classes5.dex */
public class rpc {

    public static class policy_handle extends NdrObject {
        public int type;
        public uuid_t uuid;

        @Override // jcifs.dcerpc.ndr.NdrObject
        public void decode(NdrBuffer ndrBuffer) {
            ndrBuffer.align(4);
            this.type = ndrBuffer.dec_ndr_long();
            ndrBuffer.align(4);
            if (this.uuid == null) {
                this.uuid = new uuid_t();
            }
            this.uuid.time_low = ndrBuffer.dec_ndr_long();
            this.uuid.time_mid = (short) ndrBuffer.dec_ndr_short();
            this.uuid.time_hi_and_version = (short) ndrBuffer.dec_ndr_short();
            this.uuid.clock_seq_hi_and_reserved = (byte) ndrBuffer.dec_ndr_small();
            this.uuid.clock_seq_low = (byte) ndrBuffer.dec_ndr_small();
            int i10 = ndrBuffer.index;
            ndrBuffer.advance(6);
            uuid_t uuid_tVar = this.uuid;
            if (uuid_tVar.node == null) {
                uuid_tVar.node = new byte[6];
            }
            NdrBuffer ndrBufferDerive = ndrBuffer.derive(i10);
            for (int i11 = 0; i11 < 6; i11++) {
                this.uuid.node[i11] = (byte) ndrBufferDerive.dec_ndr_small();
            }
        }

        @Override // jcifs.dcerpc.ndr.NdrObject
        public void encode(NdrBuffer ndrBuffer) throws NdrException {
            ndrBuffer.align(4);
            ndrBuffer.enc_ndr_long(this.type);
            uuid_t uuid_tVar = this.uuid;
            if (uuid_tVar == null) {
                throw new NdrException(NdrException.NO_NULL_REF);
            }
            ndrBuffer.enc_ndr_long(uuid_tVar.time_low);
            ndrBuffer.enc_ndr_short(this.uuid.time_mid);
            ndrBuffer.enc_ndr_short(this.uuid.time_hi_and_version);
            ndrBuffer.enc_ndr_small(this.uuid.clock_seq_hi_and_reserved);
            ndrBuffer.enc_ndr_small(this.uuid.clock_seq_low);
            int i10 = ndrBuffer.index;
            ndrBuffer.advance(6);
            NdrBuffer ndrBufferDerive = ndrBuffer.derive(i10);
            for (int i11 = 0; i11 < 6; i11++) {
                ndrBufferDerive.enc_ndr_small(this.uuid.node[i11]);
            }
        }
    }

    public static class sid_t extends NdrObject {
        public byte[] identifier_authority;
        public byte revision;
        public int[] sub_authority;
        public byte sub_authority_count;

        @Override // jcifs.dcerpc.ndr.NdrObject
        public void decode(NdrBuffer ndrBuffer) throws NdrException {
            ndrBuffer.align(4);
            int iDec_ndr_long = ndrBuffer.dec_ndr_long();
            this.revision = (byte) ndrBuffer.dec_ndr_small();
            this.sub_authority_count = (byte) ndrBuffer.dec_ndr_small();
            int i10 = ndrBuffer.index;
            ndrBuffer.advance(6);
            int i11 = ndrBuffer.index;
            ndrBuffer.advance(iDec_ndr_long * 4);
            if (this.identifier_authority == null) {
                this.identifier_authority = new byte[6];
            }
            NdrBuffer ndrBufferDerive = ndrBuffer.derive(i10);
            for (int i12 = 0; i12 < 6; i12++) {
                this.identifier_authority[i12] = (byte) ndrBufferDerive.dec_ndr_small();
            }
            if (this.sub_authority == null) {
                if (iDec_ndr_long < 0 || iDec_ndr_long > 65535) {
                    throw new NdrException(NdrException.INVALID_CONFORMANCE);
                }
                this.sub_authority = new int[iDec_ndr_long];
            }
            NdrBuffer ndrBufferDerive2 = ndrBufferDerive.derive(i11);
            for (int i13 = 0; i13 < iDec_ndr_long; i13++) {
                this.sub_authority[i13] = ndrBufferDerive2.dec_ndr_long();
            }
        }

        @Override // jcifs.dcerpc.ndr.NdrObject
        public void encode(NdrBuffer ndrBuffer) {
            ndrBuffer.align(4);
            byte b5 = this.sub_authority_count;
            ndrBuffer.enc_ndr_long(b5);
            ndrBuffer.enc_ndr_small(this.revision);
            ndrBuffer.enc_ndr_small(this.sub_authority_count);
            int i10 = ndrBuffer.index;
            ndrBuffer.advance(6);
            int i11 = ndrBuffer.index;
            NdrBuffer ndrBufferN = a.n(b5, 4, ndrBuffer, i10);
            for (int i12 = 0; i12 < 6; i12++) {
                ndrBufferN.enc_ndr_small(this.identifier_authority[i12]);
            }
            NdrBuffer ndrBufferDerive = ndrBufferN.derive(i11);
            for (int i13 = 0; i13 < b5; i13++) {
                ndrBufferDerive.enc_ndr_long(this.sub_authority[i13]);
            }
        }
    }

    public static class unicode_string extends NdrObject {
        public short[] buffer;
        public short length;
        public short maximum_length;

        @Override // jcifs.dcerpc.ndr.NdrObject
        public void decode(NdrBuffer ndrBuffer) throws NdrException {
            ndrBuffer.align(4);
            this.length = (short) ndrBuffer.dec_ndr_short();
            this.maximum_length = (short) ndrBuffer.dec_ndr_short();
            if (ndrBuffer.dec_ndr_long() != 0) {
                NdrBuffer ndrBuffer2 = ndrBuffer.deferred;
                int iDec_ndr_long = ndrBuffer2.dec_ndr_long();
                ndrBuffer2.dec_ndr_long();
                int iDec_ndr_long2 = ndrBuffer2.dec_ndr_long();
                int i10 = ndrBuffer2.index;
                ndrBuffer2.advance(iDec_ndr_long2 * 2);
                if (this.buffer == null) {
                    if (iDec_ndr_long < 0 || iDec_ndr_long > 65535) {
                        throw new NdrException(NdrException.INVALID_CONFORMANCE);
                    }
                    this.buffer = new short[iDec_ndr_long];
                }
                NdrBuffer ndrBufferDerive = ndrBuffer2.derive(i10);
                for (int i11 = 0; i11 < iDec_ndr_long2; i11++) {
                    this.buffer[i11] = (short) ndrBufferDerive.dec_ndr_short();
                }
            }
        }

        @Override // jcifs.dcerpc.ndr.NdrObject
        public void encode(NdrBuffer ndrBuffer) {
            ndrBuffer.align(4);
            ndrBuffer.enc_ndr_short(this.length);
            ndrBuffer.enc_ndr_short(this.maximum_length);
            ndrBuffer.enc_ndr_referent(this.buffer, 1);
            if (this.buffer != null) {
                NdrBuffer ndrBuffer2 = ndrBuffer.deferred;
                int i10 = this.length / 2;
                ndrBuffer2.enc_ndr_long(this.maximum_length / 2);
                ndrBuffer2.enc_ndr_long(0);
                ndrBuffer2.enc_ndr_long(i10);
                NdrBuffer ndrBufferN = a.n(i10, 2, ndrBuffer2, ndrBuffer2.index);
                for (int i11 = 0; i11 < i10; i11++) {
                    ndrBufferN.enc_ndr_short(this.buffer[i11]);
                }
            }
        }
    }

    public static class uuid_t extends NdrObject {
        public byte clock_seq_hi_and_reserved;
        public byte clock_seq_low;
        public byte[] node;
        public short time_hi_and_version;
        public int time_low;
        public short time_mid;

        @Override // jcifs.dcerpc.ndr.NdrObject
        public void decode(NdrBuffer ndrBuffer) {
            ndrBuffer.align(4);
            this.time_low = ndrBuffer.dec_ndr_long();
            this.time_mid = (short) ndrBuffer.dec_ndr_short();
            this.time_hi_and_version = (short) ndrBuffer.dec_ndr_short();
            this.clock_seq_hi_and_reserved = (byte) ndrBuffer.dec_ndr_small();
            this.clock_seq_low = (byte) ndrBuffer.dec_ndr_small();
            int i10 = ndrBuffer.index;
            ndrBuffer.advance(6);
            if (this.node == null) {
                this.node = new byte[6];
            }
            NdrBuffer ndrBufferDerive = ndrBuffer.derive(i10);
            for (int i11 = 0; i11 < 6; i11++) {
                this.node[i11] = (byte) ndrBufferDerive.dec_ndr_small();
            }
        }

        @Override // jcifs.dcerpc.ndr.NdrObject
        public void encode(NdrBuffer ndrBuffer) {
            ndrBuffer.align(4);
            ndrBuffer.enc_ndr_long(this.time_low);
            ndrBuffer.enc_ndr_short(this.time_mid);
            ndrBuffer.enc_ndr_short(this.time_hi_and_version);
            ndrBuffer.enc_ndr_small(this.clock_seq_hi_and_reserved);
            ndrBuffer.enc_ndr_small(this.clock_seq_low);
            int i10 = ndrBuffer.index;
            ndrBuffer.advance(6);
            NdrBuffer ndrBufferDerive = ndrBuffer.derive(i10);
            for (int i11 = 0; i11 < 6; i11++) {
                ndrBufferDerive.enc_ndr_small(this.node[i11]);
            }
        }
    }
}
