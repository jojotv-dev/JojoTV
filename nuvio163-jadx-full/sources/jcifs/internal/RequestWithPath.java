package jcifs.internal;

/* JADX INFO: loaded from: classes5.dex */
public interface RequestWithPath extends CommonServerMessageBlock {
    String getDomain();

    String getFullUNCPath();

    String getPath();

    String getServer();

    boolean isResolveInDfs();

    void setFullUNCPath(String str, String str2, String str3);

    void setPath(String str);

    void setResolveInDfs(boolean z2);
}
