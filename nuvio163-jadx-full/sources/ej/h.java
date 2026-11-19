package ej;

import java.lang.reflect.Method;
import java.util.HashMap;

/* JADX INFO: loaded from: classes5.dex */
public final class h {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Method f15737a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final d[] f15738b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final String f15739c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final int f15740d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final Class f15741e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public final f f15742f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public final boolean f15743g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public final Class[] f15744h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final boolean f15745i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public final i f15746j;
    public final boolean k;

    public h(HashMap map) {
        Object[][] objArr = {new Object[]{"handler", Method.class}, new Object[]{"priority", Integer.class}, new Object[]{"invocation", Class.class}, new Object[]{"filter", d[].class}, new Object[]{"condition", String.class}, new Object[]{"envelope", Boolean.class}, new Object[]{"messages", Class[].class}, new Object[]{"synchronized", Boolean.class}, new Object[]{"listener", i.class}, new Object[]{"subtypes", Boolean.class}};
        for (int i10 = 0; i10 < 10; i10++) {
            Object[] objArr2 = objArr[i10];
            if (map.get(objArr2[0]) == null || !((Class) objArr2[1]).isAssignableFrom(map.get(objArr2[0]).getClass())) {
                throw new IllegalArgumentException("Property " + objArr2[0] + " was expected to be not null and of type " + objArr2[1] + " but was: " + map.get(objArr2[0]));
            }
        }
        this.f15737a = (Method) map.get("handler");
        this.f15738b = (d[]) map.get("filter");
        this.f15739c = (String) map.get("condition");
        this.f15740d = ((Integer) map.get("priority")).intValue();
        this.f15741e = (Class) map.get("invocation");
        this.f15742f = (f) map.get("invocationMode");
        this.f15743g = ((Boolean) map.get("envelope")).booleanValue();
        this.f15745i = ((Boolean) map.get("subtypes")).booleanValue();
        this.f15746j = (i) map.get("listener");
        this.k = ((Boolean) map.get("synchronized")).booleanValue();
        this.f15744h = (Class[]) map.get("messages");
    }
}
