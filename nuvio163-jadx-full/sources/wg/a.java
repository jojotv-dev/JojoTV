package wg;

import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

/* JADX INFO: loaded from: classes4.dex */
public final class a extends SSLSocket {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final SSLSocket f62527b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final boolean f62528c;

    public a(SSLSocket sSLSocket, boolean z2) {
        this.f62527b = sSLSocket;
        this.f62528c = z2;
        int i10 = 0;
        if (z2) {
            ArrayList arrayList = new ArrayList(Arrays.asList(sSLSocket.getEnabledProtocols()));
            arrayList.remove("SSLv2");
            arrayList.remove("SSLv3");
            l((String[]) arrayList.toArray(new String[arrayList.size()]));
            ArrayList arrayList2 = new ArrayList(10);
            Pattern patternCompile = Pattern.compile(".*(EXPORT|NULL).*");
            String[] enabledCipherSuites = sSLSocket.getEnabledCipherSuites();
            int length = enabledCipherSuites.length;
            while (i10 < length) {
                String str = enabledCipherSuites[i10];
                if (!patternCompile.matcher(str).matches()) {
                    arrayList2.add(str);
                }
                i10++;
            }
            setEnabledCipherSuites((String[]) arrayList2.toArray(new String[arrayList2.size()]));
            return;
        }
        ArrayList arrayList3 = new ArrayList(Arrays.asList(sSLSocket.getSupportedProtocols()));
        arrayList3.remove("SSLv2");
        arrayList3.remove("SSLv3");
        l((String[]) arrayList3.toArray(new String[arrayList3.size()]));
        ArrayList arrayList4 = new ArrayList(10);
        Pattern patternCompile2 = Pattern.compile(".*(_DES|DH_|DSS|EXPORT|MD5|NULL|RC4|TLS_FALLBACK_SCSV).*");
        String[] supportedCipherSuites = sSLSocket.getSupportedCipherSuites();
        int length2 = supportedCipherSuites.length;
        while (i10 < length2) {
            String str2 = supportedCipherSuites[i10];
            if (!patternCompile2.matcher(str2).matches()) {
                arrayList4.add(str2);
            }
            i10++;
        }
        setEnabledCipherSuites((String[]) arrayList4.toArray(new String[arrayList4.size()]));
    }

    @Override // javax.net.ssl.SSLSocket
    public final void addHandshakeCompletedListener(HandshakeCompletedListener handshakeCompletedListener) {
        this.f62527b.addHandshakeCompletedListener(handshakeCompletedListener);
    }

    @Override // java.net.Socket
    public final void bind(SocketAddress socketAddress) throws IOException {
        this.f62527b.bind(socketAddress);
    }

    @Override // java.net.Socket, java.io.Closeable, java.lang.AutoCloseable
    public final synchronized void close() {
        this.f62527b.close();
    }

    @Override // java.net.Socket
    public final void connect(SocketAddress socketAddress) throws IOException {
        this.f62527b.connect(socketAddress);
    }

    public final boolean equals(Object obj) {
        return this.f62527b.equals(obj);
    }

    @Override // javax.net.ssl.SSLSocket
    /* JADX INFO: renamed from: g, reason: merged with bridge method [inline-methods] */
    public final void setEnabledCipherSuites(String[] strArr) {
        this.f62527b.setEnabledCipherSuites(strArr);
    }

    @Override // java.net.Socket
    public final SocketChannel getChannel() {
        return this.f62527b.getChannel();
    }

    @Override // javax.net.ssl.SSLSocket
    public final boolean getEnableSessionCreation() {
        return this.f62527b.getEnableSessionCreation();
    }

    @Override // javax.net.ssl.SSLSocket
    public final String[] getEnabledCipherSuites() {
        return this.f62527b.getEnabledCipherSuites();
    }

    @Override // javax.net.ssl.SSLSocket
    public final String[] getEnabledProtocols() {
        return this.f62527b.getEnabledProtocols();
    }

    @Override // java.net.Socket
    public final InetAddress getInetAddress() {
        return this.f62527b.getInetAddress();
    }

    @Override // java.net.Socket
    public final InputStream getInputStream() {
        return this.f62527b.getInputStream();
    }

    @Override // java.net.Socket
    public final boolean getKeepAlive() {
        return this.f62527b.getKeepAlive();
    }

    @Override // java.net.Socket
    public final InetAddress getLocalAddress() {
        return this.f62527b.getLocalAddress();
    }

    @Override // java.net.Socket
    public final int getLocalPort() {
        return this.f62527b.getLocalPort();
    }

    @Override // java.net.Socket
    public final SocketAddress getLocalSocketAddress() {
        return this.f62527b.getLocalSocketAddress();
    }

    @Override // javax.net.ssl.SSLSocket
    public final boolean getNeedClientAuth() {
        return this.f62527b.getNeedClientAuth();
    }

    @Override // java.net.Socket
    public final boolean getOOBInline() {
        return this.f62527b.getOOBInline();
    }

    @Override // java.net.Socket
    public final OutputStream getOutputStream() {
        return this.f62527b.getOutputStream();
    }

    @Override // java.net.Socket
    public final int getPort() {
        return this.f62527b.getPort();
    }

    @Override // java.net.Socket
    public final synchronized int getReceiveBufferSize() {
        return this.f62527b.getReceiveBufferSize();
    }

    @Override // java.net.Socket
    public final SocketAddress getRemoteSocketAddress() {
        return this.f62527b.getRemoteSocketAddress();
    }

    @Override // java.net.Socket
    public final boolean getReuseAddress() {
        return this.f62527b.getReuseAddress();
    }

    @Override // java.net.Socket
    public final synchronized int getSendBufferSize() {
        return this.f62527b.getSendBufferSize();
    }

    @Override // javax.net.ssl.SSLSocket
    public final SSLSession getSession() {
        return this.f62527b.getSession();
    }

    @Override // java.net.Socket
    public final int getSoLinger() {
        return this.f62527b.getSoLinger();
    }

    @Override // java.net.Socket
    public final synchronized int getSoTimeout() {
        return this.f62527b.getSoTimeout();
    }

    @Override // javax.net.ssl.SSLSocket
    public final String[] getSupportedCipherSuites() {
        return this.f62527b.getSupportedCipherSuites();
    }

    @Override // javax.net.ssl.SSLSocket
    public final String[] getSupportedProtocols() {
        return this.f62527b.getSupportedProtocols();
    }

    @Override // java.net.Socket
    public final boolean getTcpNoDelay() {
        return this.f62527b.getTcpNoDelay();
    }

    @Override // java.net.Socket
    public final int getTrafficClass() {
        return this.f62527b.getTrafficClass();
    }

    @Override // javax.net.ssl.SSLSocket
    public final boolean getUseClientMode() {
        return this.f62527b.getUseClientMode();
    }

    @Override // javax.net.ssl.SSLSocket
    public final boolean getWantClientAuth() {
        return this.f62527b.getWantClientAuth();
    }

    @Override // java.net.Socket
    public final boolean isBound() {
        return this.f62527b.isBound();
    }

    @Override // java.net.Socket
    public final boolean isClosed() {
        return this.f62527b.isClosed();
    }

    @Override // java.net.Socket
    public final boolean isConnected() {
        return this.f62527b.isConnected();
    }

    @Override // java.net.Socket
    public final boolean isInputShutdown() {
        return this.f62527b.isInputShutdown();
    }

    @Override // java.net.Socket
    public final boolean isOutputShutdown() {
        return this.f62527b.isOutputShutdown();
    }

    public final void l(String[] strArr) {
        this.f62527b.setEnabledProtocols(strArr);
    }

    @Override // javax.net.ssl.SSLSocket
    public final void removeHandshakeCompletedListener(HandshakeCompletedListener handshakeCompletedListener) {
        this.f62527b.removeHandshakeCompletedListener(handshakeCompletedListener);
    }

    @Override // java.net.Socket
    public final void sendUrgentData(int i10) throws IOException {
        this.f62527b.sendUrgentData(i10);
    }

    @Override // javax.net.ssl.SSLSocket
    public final void setEnableSessionCreation(boolean z2) {
        this.f62527b.setEnableSessionCreation(z2);
    }

    @Override // javax.net.ssl.SSLSocket
    public final void setEnabledProtocols(String[] strArr) {
        if (strArr != null && strArr.length == 1 && "SSLv3".equals(strArr[0])) {
            boolean z2 = this.f62528c;
            SSLSocket sSLSocket = this.f62527b;
            ArrayList arrayList = new ArrayList(z2 ? Arrays.asList(sSLSocket.getEnabledProtocols()) : Arrays.asList(sSLSocket.getSupportedProtocols()));
            if (arrayList.size() > 1) {
                arrayList.remove("SSLv2");
                arrayList.remove("SSLv3");
            } else {
                Log.w("TlsOnlySocketFactory", "SSL stuck with protocol available for ".concat(String.valueOf(arrayList)));
            }
            strArr = (String[]) arrayList.toArray(new String[arrayList.size()]);
        }
        l(strArr);
    }

    @Override // java.net.Socket
    public final void setKeepAlive(boolean z2) throws SocketException {
        this.f62527b.setKeepAlive(z2);
    }

    @Override // javax.net.ssl.SSLSocket
    public final void setNeedClientAuth(boolean z2) {
        this.f62527b.setNeedClientAuth(z2);
    }

    @Override // java.net.Socket
    public final void setOOBInline(boolean z2) throws SocketException {
        this.f62527b.setOOBInline(z2);
    }

    @Override // java.net.Socket
    public final void setPerformancePreferences(int i10, int i11, int i12) {
        this.f62527b.setPerformancePreferences(i10, i11, i12);
    }

    @Override // java.net.Socket
    public final synchronized void setReceiveBufferSize(int i10) {
        this.f62527b.setReceiveBufferSize(i10);
    }

    @Override // java.net.Socket
    public final void setReuseAddress(boolean z2) throws SocketException {
        this.f62527b.setReuseAddress(z2);
    }

    @Override // java.net.Socket
    public final synchronized void setSendBufferSize(int i10) {
        this.f62527b.setSendBufferSize(i10);
    }

    @Override // java.net.Socket
    public final void setSoLinger(boolean z2, int i10) throws SocketException {
        this.f62527b.setSoLinger(z2, i10);
    }

    @Override // java.net.Socket
    public final synchronized void setSoTimeout(int i10) {
        this.f62527b.setSoTimeout(i10);
    }

    @Override // java.net.Socket
    public final void setTcpNoDelay(boolean z2) throws SocketException {
        this.f62527b.setTcpNoDelay(z2);
    }

    @Override // java.net.Socket
    public final void setTrafficClass(int i10) throws SocketException {
        this.f62527b.setTrafficClass(i10);
    }

    @Override // javax.net.ssl.SSLSocket
    public final void setUseClientMode(boolean z2) {
        this.f62527b.setUseClientMode(z2);
    }

    @Override // javax.net.ssl.SSLSocket
    public final void setWantClientAuth(boolean z2) {
        this.f62527b.setWantClientAuth(z2);
    }

    @Override // java.net.Socket
    public final void shutdownInput() throws IOException {
        this.f62527b.shutdownInput();
    }

    @Override // java.net.Socket
    public final void shutdownOutput() throws IOException {
        this.f62527b.shutdownOutput();
    }

    @Override // javax.net.ssl.SSLSocket
    public final void startHandshake() throws IOException {
        this.f62527b.startHandshake();
    }

    @Override // javax.net.ssl.SSLSocket, java.net.Socket
    public final String toString() {
        return this.f62527b.toString();
    }

    @Override // java.net.Socket
    public final void connect(SocketAddress socketAddress, int i10) throws IOException {
        this.f62527b.connect(socketAddress, i10);
    }
}
