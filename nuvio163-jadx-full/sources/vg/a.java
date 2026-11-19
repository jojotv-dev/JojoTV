package vg;

import java.net.InetSocketAddress;
import java.net.Proxy;

/* JADX INFO: loaded from: classes4.dex */
public abstract class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final Proxy f61062a;

    static {
        new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8118));
        f61062a = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 9050));
    }
}
