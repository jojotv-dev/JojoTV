package jcifs;

import java.net.URL;

/* JADX INFO: loaded from: classes5.dex */
public interface SmbResourceLocator {
    Address getAddress();

    String getCanonicalURL();

    String getDfsPath();

    DfsReferralData getDfsReferral();

    String getName();

    String getParent();

    String getPath();

    int getPort();

    String getServer();

    String getServerWithDfs();

    String getShare();

    int getType();

    String getUNCPath();

    URL getURL();

    String getURLPath();

    boolean isIPC();

    boolean isRoot();

    boolean isWorkgroup();
}
