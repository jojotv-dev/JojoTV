package pp;

import a0.i;
import java.io.Serializable;

/* JADX INFO: loaded from: classes5.dex */
public final class a implements Serializable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f45258b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f45259c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final int f45260e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final int f45261i;

    public a(String str, int i10, int i11, int i12) {
        this.f45258b = str;
        this.f45259c = i10;
        this.f45260e = i11;
        if (i12 == 0) {
            throw new NullPointerException("estimatedResolutionLevel is null");
        }
        this.f45261i = i12;
    }

    public final String toString() {
        String str;
        StringBuilder sbR = i.r(this.f45259c, "ImageSuffix {suffix=", this.f45258b, ", height=", ", width=");
        sbR.append(this.f45260e);
        sbR.append(", resolutionLevel=");
        int i10 = this.f45261i;
        if (i10 == 1) {
            str = "HIGH";
        } else if (i10 == 2) {
            str = "MEDIUM";
        } else if (i10 != 3) {
            str = i10 != 4 ? "null" : "UNKNOWN";
        } else {
            str = "LOW";
        }
        sbR.append(str);
        sbR.append("}");
        return sbR.toString();
    }
}
