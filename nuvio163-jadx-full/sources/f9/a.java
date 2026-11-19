package f9;

import i4.e;
import i4.f;

/* JADX INFO: loaded from: classes4.dex */
public final class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public boolean f16222a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public Object f16223b;

    public a(e eVar, boolean z2) {
        this.f16223b = eVar;
        this.f16222a = z2;
    }

    public boolean a() {
        return this.f16222a;
    }

    /* JADX WARN: Code duplicated, block: B:21:0x0039  */
    public boolean b(CharSequence charSequence, int i10) {
        if (charSequence == null || i10 < 0 || charSequence.length() - i10 < 0) {
            throw new IllegalArgumentException();
        }
        e eVar = (e) this.f16223b;
        if (eVar == null) {
            return a();
        }
        eVar.getClass();
        char c5 = 0;
        c5 = 2;
        for (int i11 = 0; i11 < i10 && c5 == 2; i11++) {
            byte directionality = Character.getDirectionality(charSequence.charAt(i11));
            a aVar = f.f23782a;
            if (directionality == 0) {
                c5 = 1;
                continue;
            } else if (directionality != 1 && directionality != 2) {
                switch (directionality) {
                    case 14:
                    case 15:
                        c5 = 1;
                        continue;
                    case 16:
                    case 17:
                        break;
                    default:
                        c5 = 2;
                        continue;
                }
            }
        }
        if (c5 == 0) {
            return true;
        }
        if (c5 != 1) {
            return a();
        }
        return false;
    }
}
