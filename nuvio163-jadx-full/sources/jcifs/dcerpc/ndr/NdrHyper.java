package jcifs.dcerpc.ndr;

/* JADX INFO: loaded from: classes5.dex */
public class NdrHyper extends NdrObject {
    public long value;

    public NdrHyper(long j10) {
        this.value = j10;
    }

    @Override // jcifs.dcerpc.ndr.NdrObject
    public void decode(NdrBuffer ndrBuffer) {
        this.value = ndrBuffer.dec_ndr_hyper();
    }

    @Override // jcifs.dcerpc.ndr.NdrObject
    public void encode(NdrBuffer ndrBuffer) {
        ndrBuffer.enc_ndr_hyper(this.value);
    }
}
