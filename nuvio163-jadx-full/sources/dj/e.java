package dj;

import javax.el.BeanELResolver;
import javax.el.ELContext;

/* JADX INFO: loaded from: classes5.dex */
public final class e extends ELContext {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Object f13334a;

    public e(Object obj) {
        this.f13334a = obj;
        new d();
        new c(this);
        new BeanELResolver(true);
    }
}
