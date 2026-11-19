package xp;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.Executor;

/* JADX INFO: loaded from: classes3.dex */
public final class q extends g {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Executor f64222a;

    public q(Executor executor) {
        this.f64222a = executor;
    }

    @Override // xp.g
    public final h a(Type type, Annotation[] annotationArr) {
        if (e1.i(type) != f.class) {
            return null;
        }
        if (type instanceof ParameterizedType) {
            return new q2.y(e1.h(0, (ParameterizedType) type), e1.l(annotationArr, z0.class) ? null : this.f64222a);
        }
        throw new IllegalArgumentException("Call return type must be parameterized as Call<Foo> or Call<? extends Foo>");
    }
}
