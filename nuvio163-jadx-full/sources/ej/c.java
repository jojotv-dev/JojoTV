package ej;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes5.dex */
@Retention(RetentionPolicy.RUNTIME)
public @interface c {
    String condition() default "";

    f delivery() default f.f15734b;

    boolean enabled() default true;

    b[] filters() default {};

    Class invocation() default cj.i.class;

    int priority() default 0;

    boolean rejectSubtypes() default false;
}
