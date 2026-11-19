package com.squareup.moshi;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes4.dex */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Json {
    public static final String UNSET_NAME = "\u0000";

    boolean ignore() default false;

    String name() default "\u0000";
}
