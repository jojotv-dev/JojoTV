package com.fasterxml.jackson.core.io;

import java.io.DataInput;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;

/* JADX INFO: loaded from: classes3.dex */
public abstract class InputDecorator implements Serializable {
    private static final long serialVersionUID = 1;

    public DataInput decorate(IOContext iOContext, DataInput dataInput) {
        throw new UnsupportedOperationException();
    }

    public abstract InputStream decorate(IOContext iOContext, InputStream inputStream);

    public abstract InputStream decorate(IOContext iOContext, byte[] bArr, int i10, int i11);

    public abstract Reader decorate(IOContext iOContext, Reader reader);
}
