package jcifs.dcerpc.ndr;

/* JADX INFO: loaded from: classes5.dex */
public class NdrLong extends NdrObject {
    public int value;

    public NdrLong(int i10) {
        this.value = i10;
    }

    @Override // jcifs.dcerpc.ndr.NdrObject
    public void decode(NdrBuffer ndrBuffer) {
        this.value = ndrBuffer.dec_ndr_long();
    }

    @Override // jcifs.dcerpc.ndr.NdrObject
    public void encode(NdrBuffer ndrBuffer) {
        ndrBuffer.enc_ndr_long(this.value);
    }
}
