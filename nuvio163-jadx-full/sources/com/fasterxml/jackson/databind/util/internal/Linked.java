package com.fasterxml.jackson.databind.util.internal;

import com.fasterxml.jackson.databind.util.internal.Linked;

/* JADX INFO: loaded from: classes3.dex */
interface Linked<T extends Linked<T>> {
    T getNext();

    T getPrevious();

    void setNext(T t10);

    void setPrevious(T t10);
}
