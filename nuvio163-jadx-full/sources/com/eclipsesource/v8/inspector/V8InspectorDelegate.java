package com.eclipsesource.v8.inspector;

/* JADX INFO: loaded from: classes3.dex */
public interface V8InspectorDelegate {
    void onResponse(String str);

    void waitFrontendMessageOnPause();
}
