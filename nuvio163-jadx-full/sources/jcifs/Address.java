package jcifs;

import java.net.InetAddress;

/* JADX INFO: loaded from: classes5.dex */
public interface Address {
    String firstCalledName();

    String getHostAddress();

    String getHostName();

    String nextCalledName(CIFSContext cIFSContext);

    InetAddress toInetAddress();

    <T extends Address> T unwrap(Class<T> cls);
}
