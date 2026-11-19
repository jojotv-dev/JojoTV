package jcifs.dcerpc.ndr;

/* JADX INFO: loaded from: classes5.dex */
public class NdrShort extends NdrObject {
    public int value;

    public NdrShort(int i10) {
        this.value = i10 & 255;
    }

    @Override // jcifs.dcerpc.ndr.NdrObject
    public void decode(NdrBuffer ndrBuffer) {
        this.value = ndrBuffer.dec_ndr_short();
    }

    @Override // jcifs.dcerpc.ndr.NdrObject
    public void encode(NdrBuffer ndrBuffer) {
        ndrBuffer.enc_ndr_short(this.value);
    }
}
