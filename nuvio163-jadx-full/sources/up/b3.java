package up;

import java.io.EOFException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.security.SecureRandom;

/* JADX INFO: loaded from: classes5.dex */
public final class b3 extends p {

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final SecureRandom f60073d = new SecureRandom();

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static volatile boolean f60074e = true;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public boolean f60075c;

    static {
        new Thread(new ao.e(4)).start();
    }

    public final void d() {
        if (f60074e) {
            try {
                Thread.sleep(2L);
            } catch (InterruptedException unused) {
            }
            if (f60074e) {
                return;
            }
        }
        DatagramChannel datagramChannel = (DatagramChannel) this.f60232b.channel();
        for (int i10 = 0; i10 < 1024; i10++) {
            try {
                datagramChannel.socket().bind(new InetSocketAddress(f60073d.nextInt(64511) + 1024));
                this.f60075c = true;
                return;
            } catch (SocketException unused2) {
            }
        }
    }

    public final byte[] e(int i10) throws EOFException {
        SelectionKey selectionKey = this.f60232b;
        DatagramChannel datagramChannel = (DatagramChannel) selectionKey.channel();
        byte[] bArr = new byte[i10];
        selectionKey.interestOps(1);
        while (!selectionKey.isReadable()) {
            try {
                p.a(selectionKey, this.f60231a);
            } catch (Throwable th2) {
                if (selectionKey.isValid()) {
                    selectionKey.interestOps(0);
                }
                throw th2;
            }
        }
        if (selectionKey.isValid()) {
            selectionKey.interestOps(0);
        }
        long j10 = datagramChannel.read(ByteBuffer.wrap(bArr));
        if (j10 <= 0) {
            throw new EOFException();
        }
        int i11 = (int) j10;
        byte[] bArr2 = new byte[i11];
        System.arraycopy(bArr, 0, bArr2, 0, i11);
        datagramChannel.socket().getLocalSocketAddress();
        datagramChannel.socket().getRemoteSocketAddress();
        p.c("UDP read", bArr2);
        return bArr2;
    }
}
