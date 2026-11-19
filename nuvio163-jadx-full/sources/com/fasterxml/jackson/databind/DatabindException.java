package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;

/* JADX INFO: loaded from: classes3.dex */
public abstract class DatabindException extends JsonProcessingException {
    private static final long serialVersionUID = 3;

    public DatabindException(String str, JsonLocation jsonLocation, Throwable th2) {
        super(str, jsonLocation, th2);
    }

    public abstract void prependPath(Object obj, int i10);

    public abstract void prependPath(Object obj, String str);

    public DatabindException(String str) {
        super(str);
    }

    public DatabindException(String str, JsonLocation jsonLocation) {
        this(str, jsonLocation, null);
    }

    public DatabindException(String str, Throwable th2) {
        this(str, null, th2);
    }
}
