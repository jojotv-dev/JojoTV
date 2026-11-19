package wk;

import androidx.media3.extractor.text.ttml.TtmlNode;
import java.security.BasicPermission;
import java.security.Permission;
import java.util.StringTokenizer;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import rn.i;

/* JADX INFO: loaded from: classes5.dex */
public final class c extends BasicPermission {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f62636b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f62637c;

    public c(String str) {
        super(BouncyCastleProvider.PROVIDER_NAME, str);
        this.f62636b = str;
        StringTokenizer stringTokenizer = new StringTokenizer(i.d(str), " ,");
        int i10 = 0;
        while (stringTokenizer.hasMoreTokens()) {
            String strNextToken = stringTokenizer.nextToken();
            if (strNextToken.equals("threadlocalecimplicitlyca")) {
                i10 |= 1;
            } else if (strNextToken.equals("ecimplicitlyca")) {
                i10 |= 2;
            } else if (strNextToken.equals("threadlocaldhdefaultparams")) {
                i10 |= 4;
            } else if (strNextToken.equals("dhdefaultparams")) {
                i10 |= 8;
            } else if (strNextToken.equals("acceptableeccurves")) {
                i10 |= 16;
            } else if (strNextToken.equals("additionalecparameters")) {
                i10 |= 32;
            } else if (strNextToken.equals(TtmlNode.COMBINE_ALL)) {
                i10 = 63;
            }
        }
        if (i10 == 0) {
            throw new IllegalArgumentException("unknown permissions passed to mask");
        }
        this.f62637c = i10;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof c) {
            c cVar = (c) obj;
            if (this.f62637c == cVar.f62637c && getName().equals(cVar.getName())) {
                return true;
            }
        }
        return false;
    }

    @Override // java.security.BasicPermission, java.security.Permission
    public final String getActions() {
        return this.f62636b;
    }

    public final int hashCode() {
        return getName().hashCode() + this.f62637c;
    }

    @Override // java.security.BasicPermission, java.security.Permission
    public final boolean implies(Permission permission) {
        if (!(permission instanceof c) || !getName().equals(permission.getName())) {
            return false;
        }
        int i10 = ((c) permission).f62637c;
        return (this.f62637c & i10) == i10;
    }
}
