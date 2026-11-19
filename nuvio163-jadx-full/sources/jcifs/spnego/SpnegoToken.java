package jcifs.spnego;

/* JADX INFO: loaded from: classes5.dex */
public abstract class SpnegoToken {
    private byte[] mechanismListMIC;
    private byte[] mechanismToken;

    public byte[] getMechanismListMIC() {
        return this.mechanismListMIC;
    }

    public byte[] getMechanismToken() {
        return this.mechanismToken;
    }

    public abstract void parse(byte[] bArr);

    public void setMechanismListMIC(byte[] bArr) {
        this.mechanismListMIC = bArr;
    }

    public void setMechanismToken(byte[] bArr) {
        this.mechanismToken = bArr;
    }

    public abstract byte[] toByteArray();
}
