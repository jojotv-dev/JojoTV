package jcifs.pac;

/* JADX INFO: loaded from: classes5.dex */
public class PacCredentialType {
    private static final int MINIMAL_BUFFER_SIZE = 32;
    private byte[] credentialType;

    public PacCredentialType(byte[] bArr) throws PACDecodingException {
        this.credentialType = bArr;
        if (!isCredentialTypeCorrect()) {
            throw new PACDecodingException("Invalid PAC credential type");
        }
    }

    public boolean isCredentialTypeCorrect() {
        byte[] bArr = this.credentialType;
        return bArr != null && bArr.length < 32;
    }
}
