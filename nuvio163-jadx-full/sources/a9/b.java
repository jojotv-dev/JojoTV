package a9;

import java.util.Arrays;

/* JADX INFO: loaded from: classes4.dex */
public final class b {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final byte[] f611a;

    public b(byte[] bArr) {
        if (bArr == null || bArr.length != 16) {
            throw new IllegalArgumentException("A lease key must be exactly 16 bytes");
        }
        this.f611a = (byte[]) bArr.clone();
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof b)) {
            return false;
        }
        return Arrays.equals(this.f611a, ((b) obj).f611a);
    }

    public final int hashCode() {
        return Arrays.hashCode(this.f611a);
    }

    public final String toString() {
        StringBuilder sb2 = new StringBuilder("LeaseKey{");
        byte[] bArr = this.f611a;
        StringBuilder sb3 = new StringBuilder();
        for (byte b5 : bArr) {
            char[] cArr = k9.a.f28008a;
            sb3.append(cArr[(b5 >> 4) & 15]);
            sb3.append(cArr[b5 & 15]);
        }
        sb2.append(sb3.toString());
        sb2.append("}");
        return sb2.toString();
    }
}
