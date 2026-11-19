package com.liskovsoft.sharedutils.querystringparser;

/* JADX INFO: loaded from: classes4.dex */
class NullQueryString implements UrlQueryString {
    private final String mUrl;

    private NullQueryString(String str) {
        this.mUrl = str;
    }

    public static UrlQueryString parse(String str) {
        return new NullQueryString(str);
    }

    @Override // com.liskovsoft.sharedutils.querystringparser.UrlQueryString
    public boolean contains(String str) {
        return false;
    }

    @Override // com.liskovsoft.sharedutils.querystringparser.UrlQueryString
    public String get(String str) {
        return null;
    }

    @Override // com.liskovsoft.sharedutils.querystringparser.UrlQueryString
    public float getFloat(String str) {
        return 0.0f;
    }

    @Override // com.liskovsoft.sharedutils.querystringparser.UrlQueryString
    public boolean isEmpty() {
        return true;
    }

    @Override // com.liskovsoft.sharedutils.querystringparser.UrlQueryString
    public boolean isValid() {
        return false;
    }

    @Override // com.liskovsoft.sharedutils.querystringparser.UrlQueryString
    public void remove(String str) {
    }

    @Override // com.liskovsoft.sharedutils.querystringparser.UrlQueryString
    public void set(String str, float f10) {
    }

    public String toString() {
        return this.mUrl;
    }

    @Override // com.liskovsoft.sharedutils.querystringparser.UrlQueryString
    public void set(String str, int i10) {
    }

    @Override // com.liskovsoft.sharedutils.querystringparser.UrlQueryString
    public void set(String str, String str2) {
    }
}
