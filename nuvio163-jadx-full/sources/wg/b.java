package wg;

import android.net.SSLCertificateSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/* JADX INFO: loaded from: classes4.dex */
public final class b extends SSLSocketFactory {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final SSLSocketFactory f62529a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final boolean f62530b = true;

    public b(SSLSocketFactory sSLSocketFactory) {
        this.f62529a = sSLSocketFactory;
    }

    public final Socket a(String str, Socket socket) {
        if (!(socket instanceof SSLSocket)) {
            return socket;
        }
        a aVar = new a((SSLSocket) socket, this.f62530b);
        SSLSocketFactory sSLSocketFactory = this.f62529a;
        if (sSLSocketFactory instanceof SSLCertificateSocketFactory) {
            SSLCertificateSocketFactory sSLCertificateSocketFactory = (SSLCertificateSocketFactory) sSLSocketFactory;
            sSLCertificateSocketFactory.setHostname(socket, str);
            sSLCertificateSocketFactory.setUseSessionTickets(socket, false);
            return aVar;
        }
        SSLSocket sSLSocket = aVar.f62527b;
        try {
            sSLSocket.getClass().getMethod("setHostname", String.class).invoke(sSLSocket, str);
            return aVar;
        } catch (Exception e10) {
            throw new IllegalStateException("Could not enable SNI", e10);
        }
    }

    @Override // javax.net.ssl.SSLSocketFactory
    public final Socket createSocket(Socket socket, String str, int i10, boolean z2) {
        return a(str, this.f62529a.createSocket(socket, str, i10, z2));
    }

    @Override // javax.net.ssl.SSLSocketFactory
    public final String[] getDefaultCipherSuites() {
        return this.f62529a.getDefaultCipherSuites();
    }

    @Override // javax.net.ssl.SSLSocketFactory
    public final String[] getSupportedCipherSuites() {
        return this.f62529a.getSupportedCipherSuites();
    }

    @Override // javax.net.SocketFactory
    public final Socket createSocket(String str, int i10) {
        return a(str, this.f62529a.createSocket(str, i10));
    }

    @Override // javax.net.SocketFactory
    public final Socket createSocket(String str, int i10, InetAddress inetAddress, int i11) {
        return a(str, this.f62529a.createSocket(str, i10, inetAddress, i11));
    }

    @Override // javax.net.SocketFactory
    public final Socket createSocket(InetAddress inetAddress, int i10) throws IOException {
        return a(inetAddress.getHostName(), this.f62529a.createSocket(inetAddress, i10));
    }

    @Override // javax.net.SocketFactory
    public final Socket createSocket(InetAddress inetAddress, int i10, InetAddress inetAddress2, int i11) throws IOException {
        return a(inetAddress.getHostName(), this.f62529a.createSocket(inetAddress, i10, inetAddress2, i11));
    }
}
