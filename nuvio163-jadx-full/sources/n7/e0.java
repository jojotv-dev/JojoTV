package n7;

/* JADX INFO: loaded from: classes.dex */
public final class e0 implements Cloneable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final float f39583b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f39584c;

    public e0(float f10, int i10) {
        this.f39583b = f10;
        this.f39584c = i10;
    }

    public final float a(z1 z1Var) {
        float fSqrt;
        if (this.f39584c != 9) {
            return d(z1Var);
        }
        x1 x1Var = (x1) z1Var.f39800e;
        c2.a aVar = x1Var.f39776g;
        if (aVar == null) {
            aVar = x1Var.f39775f;
        }
        float f10 = this.f39583b;
        if (aVar == null) {
            return f10;
        }
        float f11 = aVar.f6408d;
        float f12 = aVar.f6409e;
        if (f11 == f12) {
            fSqrt = f10 * f11;
        } else {
            fSqrt = f10 * ((float) (Math.sqrt((f12 * f12) + (f11 * f11)) / 1.414213562373095d));
        }
        return fSqrt / 100.0f;
    }

    public final float b(z1 z1Var, float f10) {
        return this.f39584c == 9 ? (this.f39583b * f10) / 100.0f : d(z1Var);
    }

    public final float c() {
        float f10;
        float f11;
        int iB = u4.i.b(this.f39584c);
        float f12 = this.f39583b;
        if (iB == 0) {
            return f12;
        }
        if (iB == 3) {
            return f12 * 96.0f;
        }
        if (iB == 4) {
            f10 = f12 * 96.0f;
            f11 = 2.54f;
        } else if (iB == 5) {
            f10 = f12 * 96.0f;
            f11 = 25.4f;
        } else if (iB == 6) {
            f10 = f12 * 96.0f;
            f11 = 72.0f;
        } else {
            if (iB != 7) {
                return f12;
            }
            f10 = f12 * 96.0f;
            f11 = 6.0f;
        }
        return f10 / f11;
    }

    public final float d(z1 z1Var) {
        float textSize;
        int iB = u4.i.b(this.f39584c);
        float f10 = this.f39583b;
        switch (iB) {
            case 1:
                textSize = ((x1) z1Var.f39800e).f39773d.getTextSize();
                break;
            case 2:
                textSize = ((x1) z1Var.f39800e).f39773d.getTextSize() / 2.0f;
                break;
            case 3:
                z1Var.getClass();
                return f10 * 96.0f;
            case 4:
                z1Var.getClass();
                return (f10 * 96.0f) / 2.54f;
            case 5:
                z1Var.getClass();
                return (f10 * 96.0f) / 25.4f;
            case 6:
                z1Var.getClass();
                return (f10 * 96.0f) / 72.0f;
            case 7:
                z1Var.getClass();
                return (f10 * 96.0f) / 6.0f;
            case 8:
                x1 x1Var = (x1) z1Var.f39800e;
                c2.a aVar = x1Var.f39776g;
                if (aVar == null) {
                    aVar = x1Var.f39775f;
                }
                if (aVar != null) {
                    return (f10 * aVar.f6408d) / 100.0f;
                }
            default:
                return f10;
        }
        return textSize * f10;
    }

    public final float e(z1 z1Var) {
        if (this.f39584c != 9) {
            return d(z1Var);
        }
        x1 x1Var = (x1) z1Var.f39800e;
        c2.a aVar = x1Var.f39776g;
        if (aVar == null) {
            aVar = x1Var.f39775f;
        }
        float f10 = this.f39583b;
        return aVar == null ? f10 : (f10 * aVar.f6409e) / 100.0f;
    }

    public final boolean f() {
        return this.f39583b < 0.0f;
    }

    public final boolean g() {
        return this.f39583b == 0.0f;
    }

    public final String toString() {
        String str;
        StringBuilder sb2 = new StringBuilder();
        sb2.append(String.valueOf(this.f39583b));
        switch (this.f39584c) {
            case 1:
                str = "px";
                break;
            case 2:
                str = "em";
                break;
            case 3:
                str = "ex";
                break;
            case 4:
                str = "in";
                break;
            case 5:
                str = "cm";
                break;
            case 6:
                str = "mm";
                break;
            case 7:
                str = "pt";
                break;
            case 8:
                str = "pc";
                break;
            case 9:
                str = "percent";
                break;
            default:
                str = "null";
                break;
        }
        sb2.append(str);
        return sb2.toString();
    }

    public e0(float f10) {
        this.f39583b = f10;
        this.f39584c = 1;
    }
}
