package ah;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Collections;
import java.util.Iterator;

/* JADX INFO: loaded from: classes5.dex */
public final class b implements NsdManager.ResolveListener {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final c f1718a;

    public b(c cVar) {
        this.f1718a = cVar;
    }

    @Override // android.net.nsd.NsdManager.ResolveListener
    public final void onServiceResolved(NsdServiceInfo nsdServiceInfo) {
        c cVar = this.f1718a;
        if (cVar.f1725g) {
            try {
                Iterator it = Collections.list(NetworkInterface.getNetworkInterfaces()).iterator();
                while (it.hasNext()) {
                    Iterator it2 = Collections.list(((NetworkInterface) it.next()).getInetAddresses()).iterator();
                    while (it2.hasNext()) {
                        String hostAddress = ((InetAddress) it2.next()).getHostAddress();
                        if (hostAddress != null && hostAddress.equals(nsdServiceInfo.getHost().getHostAddress())) {
                            int port = nsdServiceInfo.getPort();
                            try {
                                ServerSocket serverSocket = new ServerSocket();
                                try {
                                    serverSocket.bind(new InetSocketAddress(ra.a.D(cVar.f1719a), port), 1);
                                    serverSocket.close();
                                } catch (Throwable th2) {
                                    try {
                                        serverSocket.close();
                                    } catch (Throwable th3) {
                                        th2.addSuppressed(th3);
                                    }
                                    throw th2;
                                }
                            } catch (IOException unused) {
                                cVar.f1726h = nsdServiceInfo.getServiceName();
                                cVar.f1721c.a(nsdServiceInfo.getHost(), nsdServiceInfo.getPort());
                            }
                        }
                    }
                }
            } catch (SocketException e10) {
                e10.printStackTrace();
            }
        }
    }

    @Override // android.net.nsd.NsdManager.ResolveListener
    public final void onResolveFailed(NsdServiceInfo nsdServiceInfo, int i10) {
    }
}
