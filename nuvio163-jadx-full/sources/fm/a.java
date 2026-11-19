package fm;

import ek.h;
import h1.f;
import org.bouncycastle.crypto.digests.j;
import org.bouncycastle.crypto.digests.l;
import org.bouncycastle.crypto.digests.m;
import org.bouncycastle.crypto.digests.o;

/* JADX INFO: loaded from: classes5.dex */
public final class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f18107a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public int f18108b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f18109c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public int f18110d;

    public String toString() {
        String str;
        switch (this.f18107a) {
            case 1:
                StringBuilder sb2 = new StringBuilder();
                int i10 = this.f18108b;
                if (i10 == 1) {
                    str = "DELETE";
                } else if (i10 == 2) {
                    str = "EQUAL";
                } else if (i10 == 3) {
                    str = "INSERT";
                } else if (i10 == 4) {
                    str = "REPLACE";
                } else {
                    if (i10 != 5) {
                        throw null;
                    }
                    str = "KEEP";
                }
                sb2.append(str);
                sb2.append("(");
                sb2.append(this.f18109c);
                sb2.append(",");
                return f.j(this.f18110d, ")", sb2);
            case 2:
                StringBuilder sb3 = new StringBuilder("(");
                sb3.append(this.f18108b);
                sb3.append(",");
                sb3.append(this.f18109c);
                sb3.append(",");
                return f.j(this.f18110d, ")", sb3);
            default:
                return super.toString();
        }
    }

    public a(int i10, boolean z2, boolean z10) {
        this.f18107a = 0;
        if (z2) {
            new j();
            new m();
            ((h) ek.j.f15768d.get()).getClass();
        } else {
            new o(128);
            new l(256);
            new l(512);
        }
        this.f18108b = z10 ? 12 : 13;
        int i11 = ((this.f18108b * 256) / 8) * i10;
        int i12 = (i10 * 320) + 32;
        this.f18109c = i12;
        this.f18110d = i11 + i12 + 64;
    }
}
