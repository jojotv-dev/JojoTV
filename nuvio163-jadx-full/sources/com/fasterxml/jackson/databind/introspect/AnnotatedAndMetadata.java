package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.introspect.Annotated;

/* JADX INFO: loaded from: classes3.dex */
public class AnnotatedAndMetadata<A extends Annotated, M> {
    public final A annotated;
    public final M metadata;

    public AnnotatedAndMetadata(A a10, M m7) {
        this.annotated = a10;
        this.metadata = m7;
    }

    public static <A extends Annotated, M> AnnotatedAndMetadata<A, M> of(A a10, M m7) {
        return new AnnotatedAndMetadata<>(a10, m7);
    }
}
