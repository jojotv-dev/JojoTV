package jcifs;

/* JADX INFO: loaded from: classes5.dex */
public interface SID {
    public static final int SID_TYPE_ALIAS = 4;
    public static final int SID_TYPE_DELETED = 6;
    public static final int SID_TYPE_DOMAIN = 3;
    public static final int SID_TYPE_DOM_GRP = 2;
    public static final int SID_TYPE_INVALID = 7;
    public static final int SID_TYPE_UNKNOWN = 8;
    public static final int SID_TYPE_USER = 1;
    public static final int SID_TYPE_USE_NONE = 0;
    public static final int SID_TYPE_WKN_GRP = 5;

    String getAccountName();

    String getDomainName();

    SID getDomainSid();

    int getRid();

    int getType();

    String getTypeText();

    String toDisplayString();

    <T> T unwrap(Class<T> cls);
}
