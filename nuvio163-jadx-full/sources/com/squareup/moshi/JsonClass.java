package com.squareup.moshi;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes.dex */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonClass {
    boolean generateAdapter();

    String generator() default "";
}
