package com.liskovsoft.sharedutils.querystringparser;

/* JADX INFO: loaded from: classes4.dex */
public interface UrlQueryString {
    boolean contains(String str);

    String get(String str);

    float getFloat(String str);

    boolean isEmpty();

    boolean isValid();

    void remove(String str);

    void set(String str, float f10);

    void set(String str, int i10);

    void set(String str, String str2);
}
