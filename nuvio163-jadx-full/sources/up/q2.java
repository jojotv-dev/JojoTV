package up;

import java.io.EOFException;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/* JADX INFO: loaded from: classes5.dex */
public final class q2 extends p {
    public q2(long j10) {
        super(SocketChannel.open(), j10);
    }

    public final byte[] d(int i10) {
        SelectionKey selectionKey = this.f60232b;
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        byte[] bArr = new byte[i10];
        ByteBuffer byteBufferWrap = ByteBuffer.wrap(bArr);
        selectionKey.interestOps(1);
        int i11 = 0;
        while (i11 < i10) {
            try {
                boolean zIsReadable = selectionKey.isReadable();
                long j10 = this.f60231a;
                if (zIsReadable) {
                    long j11 = socketChannel.read(byteBufferWrap);
                    if (j11 < 0) {
                        throw new EOFException();
                    }
                    i11 += (int) j11;
                    if (i11 < i10 && System.currentTimeMillis() > j10) {
                        throw new SocketTimeoutException();
                    }
                } else {
                    p.a(selectionKey, j10);
                }
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
        return bArr;
    }

    public final void e(SocketAddress socketAddress) {
        SelectionKey selectionKey = this.f60232b;
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        if (socketChannel.connect(socketAddress)) {
            return;
        }
        selectionKey.interestOps(8);
        while (true) {
            try {
                if (socketChannel.finishConnect()) {
                    break;
                } else if (!selectionKey.isConnectable()) {
                    p.a(selectionKey, this.f60231a);
                }
            } finally {
                if (selectionKey.isValid()) {
                    selectionKey.interestOps(0);
                }
            }
        }
    }

    public final byte[] f() {
        byte[] bArrD = d(2);
        byte[] bArrD2 = d(((bArrD[0] & 255) << 8) + (bArrD[1] & 255));
        SocketChannel socketChannel = (SocketChannel) this.f60232b.channel();
        socketChannel.socket().getLocalSocketAddress();
        socketChannel.socket().getRemoteSocketAddress();
        p.c("TCP read", bArrD2);
        return bArrD2;
    }

    public final void g(byte[] bArr) {
        SelectionKey selectionKey = this.f60232b;
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        socketChannel.socket().getLocalSocketAddress();
        socketChannel.socket().getRemoteSocketAddress();
        p.c("TCP write", bArr);
        ByteBuffer[] byteBufferArr = {ByteBuffer.wrap(new byte[]{(byte) (bArr.length >>> 8), (byte) (bArr.length & 255)}), ByteBuffer.wrap(bArr)};
        selectionKey.interestOps(4);
        int i10 = 0;
        while (i10 < bArr.length + 2) {
            try {
                boolean zIsWritable = selectionKey.isWritable();
                long j10 = this.f60231a;
                if (zIsWritable) {
                    long jWrite = socketChannel.write(byteBufferArr);
                    if (jWrite < 0) {
                        throw new EOFException();
                    }
                    i10 += (int) jWrite;
                    if (i10 < bArr.length + 2 && System.currentTimeMillis() > j10) {
                        throw new SocketTimeoutException();
                    }
                } else {
                    p.a(selectionKey, j10);
                }
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
    }
}
