package d2;

import io.github.jan.supabase.postgrest.PropertyConversionMethod;
import io.sentry.v1;
import kotlin.reflect.KProperty1;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class a implements e0.g, e2.i, PropertyConversionMethod, v1 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f11674b;

    public /* synthetic */ a(int i10) {
        this.f11674b = i10;
    }

    @Override // io.sentry.v1
    public Object a() {
        return null;
    }

    @Override // e0.g
    public int b(int i10, t3.n nVar) {
        switch (this.f11674b) {
            case 17:
                return Math.round((1 + (nVar != t3.n.f57171b ? (-1.0f) * (-1) : -1.0f)) * (i10 / 2.0f));
            default:
                return Math.round((1 + 0.0f) * ((i10 + 0) / 2.0f));
        }
    }

    @Override // e2.i
    public double c(double d10) {
        switch (this.f11674b) {
            case 19:
                double d11 = d10 < 0.0d ? -d10 : d10;
                return Math.copySign(d11 >= 0.0031308049535603718d ? (Math.pow(d11, 0.4166666666666667d) - 0.05213270142180095d) / 0.9478672985781991d : d11 / 0.07739938080495357d, d10);
            case 20:
                double d12 = d10 < 0.0d ? -d10 : d10;
                return Math.copySign(d12 >= 0.04045d ? Math.pow((0.9478672985781991d * d12) + 0.05213270142180095d, 2.4d) : d12 * 0.07739938080495357d, d10);
            case 21:
                float[] fArr = e2.d.f13736a;
                return e2.d.b(e2.d.f13738c, d10);
            case 22:
                float[] fArr2 = e2.d.f13736a;
                return e2.d.a(e2.d.f13738c, d10);
            case 23:
                float[] fArr3 = e2.d.f13736a;
                return e2.d.d(e2.d.f13739d, d10);
            case 24:
                float[] fArr4 = e2.d.f13736a;
                return e2.d.c(e2.d.f13739d, d10);
            default:
                return d10;
        }
    }

    @Override // io.github.jan.supabase.postgrest.PropertyConversionMethod
    public String invoke(KProperty1 kProperty1) {
        switch (this.f11674b) {
            case 26:
                return PropertyConversionMethod.Companion.SERIAL_NAME$lambda$0(kProperty1);
            case 27:
                return PropertyConversionMethod.Companion.CAMEL_CASE_TO_SNAKE_CASE$lambda$1(kProperty1);
            default:
                return PropertyConversionMethod.Companion.NONE$lambda$2(kProperty1);
        }
    }
}
