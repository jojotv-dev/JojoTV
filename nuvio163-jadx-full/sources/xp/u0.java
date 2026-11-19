package xp;

import okhttp3.Response;
import okhttp3.ResponseBody;

/* JADX INFO: loaded from: classes3.dex */
public final class u0<T> {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Response f64280a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Object f64281b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final ResponseBody f64282c;

    public u0(Response response, Object obj, ResponseBody responseBody) {
        this.f64280a = response;
        this.f64281b = obj;
        this.f64282c = responseBody;
    }

    public final Object a() {
        return this.f64281b;
    }

    public final String toString() {
        return this.f64280a.toString();
    }
}
