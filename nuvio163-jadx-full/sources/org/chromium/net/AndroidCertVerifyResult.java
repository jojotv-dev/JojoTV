package org.chromium.net;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.chromium.base.annotations.CalledByNative;

/* JADX INFO: loaded from: classes5.dex */
public class AndroidCertVerifyResult {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f44139a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final boolean f44140b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final List f44141c;

    public AndroidCertVerifyResult(boolean z2, List list) {
        this.f44139a = 0;
        this.f44140b = z2;
        this.f44141c = new ArrayList(list);
    }

    @CalledByNative
    public byte[][] getCertificateChainEncoded() {
        List list = this.f44141c;
        byte[][] bArr = new byte[list.size()][];
        for (int i10 = 0; i10 < list.size(); i10++) {
            try {
                bArr[i10] = ((X509Certificate) list.get(i10)).getEncoded();
            } catch (CertificateEncodingException unused) {
                return new byte[0][];
            }
        }
        return bArr;
    }

    @CalledByNative
    public int getStatus() {
        return this.f44139a;
    }

    @CalledByNative
    public boolean isIssuedByKnownRoot() {
        return this.f44140b;
    }

    public AndroidCertVerifyResult(int i10) {
        this.f44139a = i10;
        this.f44140b = false;
        this.f44141c = Collections.EMPTY_LIST;
    }
}
