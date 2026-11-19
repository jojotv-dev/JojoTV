package com.eclipsesource.v8;

/* JADX INFO: loaded from: classes3.dex */
public class V8ScriptExecutionException extends V8ScriptException {
    public V8ScriptExecutionException(String str, int i10, String str2, String str3, int i11, int i12, String str4) {
        this(str, i10, str2, str3, i11, i12, str4, null);
    }

    public V8ScriptExecutionException(String str, int i10, String str2, String str3, int i11, int i12, String str4, Throwable th2) {
        super(str, i10, str2, str3, i11, i12, str4, th2);
    }
}
