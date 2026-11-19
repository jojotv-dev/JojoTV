package androidx.recyclerview.widget;

import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public final class u {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public int f4098a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public boolean f4099b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public boolean f4100c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public boolean f4101d;

    public void a(int i10, int i11, boolean z2) {
        String str;
        if (i10 == 0 && i11 == 0) {
            return;
        }
        if (!z2 && 1 <= i10 && i10 < 15) {
            this.f4101d = true;
            return;
        }
        if (!z2 && i10 == 15) {
            this.f4101d = false;
            return;
        }
        if (16 <= i10 && i10 < 32) {
            boolean z10 = (i10 & 8) != 0;
            if (z2) {
                this.f4099b = z10;
                return;
            } else {
                this.f4100c = z10;
                this.f4101d = false;
                return;
            }
        }
        if ((z2 || !this.f4101d) && i10 >= 32) {
            List list = ke.o.f28218a;
            int i12 = this.f4098a;
            if (z2) {
                str = this.f4099b ? "cc2" : "cc1";
            } else {
                str = this.f4100c ? "cc4" : "cc3";
            }
            ke.o.a(i12, str);
        }
    }
}
