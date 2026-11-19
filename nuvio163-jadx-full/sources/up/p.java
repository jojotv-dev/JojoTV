package up;

import java.io.IOException;
import java.io.PrintStream;
import java.net.SocketTimeoutException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelectableChannel;

/* JADX INFO: loaded from: classes5.dex */
public abstract class p {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final long f60231a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final SelectionKey f60232b;

    public p(AbstractSelectableChannel abstractSelectableChannel, long j10) throws Throwable {
        Selector selectorOpen;
        this.f60231a = j10;
        try {
            selectorOpen = Selector.open();
            try {
                abstractSelectableChannel.configureBlocking(false);
                this.f60232b = abstractSelectableChannel.register(selectorOpen, 1);
            } catch (Throwable th2) {
                th = th2;
                if (selectorOpen != null) {
                    selectorOpen.close();
                }
                abstractSelectableChannel.close();
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            selectorOpen = null;
        }
    }

    public static void a(SelectionKey selectionKey, long j10) throws IOException {
        int iSelectNow;
        long jCurrentTimeMillis = j10 - System.currentTimeMillis();
        if (jCurrentTimeMillis > 0) {
            iSelectNow = selectionKey.selector().select(jCurrentTimeMillis);
        } else {
            iSelectNow = jCurrentTimeMillis == 0 ? selectionKey.selector().selectNow() : 0;
        }
        if (iSelectNow == 0) {
            throw new SocketTimeoutException();
        }
    }

    public static void c(String str, byte[] bArr) {
        if (r1.a("verbosemsg")) {
            PrintStream printStream = System.err;
            char[] cArr = wp.c.f62674a;
            int length = bArr.length;
            StringBuffer stringBuffer = new StringBuffer();
            StringBuffer stringBuffer2 = new StringBuffer();
            stringBuffer2.append(length);
            stringBuffer2.append("b");
            stringBuffer.append(stringBuffer2.toString());
            StringBuffer stringBuffer3 = new StringBuffer(" (");
            stringBuffer3.append(str);
            stringBuffer3.append(")");
            stringBuffer.append(stringBuffer3.toString());
            stringBuffer.append(':');
            int length2 = (stringBuffer.toString().length() + 8) & (-8);
            stringBuffer.append('\t');
            int i10 = (80 - length2) / 3;
            for (int i11 = 0; i11 < length; i11++) {
                if (i11 != 0 && i11 % i10 == 0) {
                    stringBuffer.append('\n');
                    for (int i12 = 0; i12 < length2 / 8; i12++) {
                        stringBuffer.append('\t');
                    }
                }
                byte b5 = bArr[i11];
                char[] cArr2 = wp.c.f62674a;
                stringBuffer.append(cArr2[(b5 & 255) >> 4]);
                stringBuffer.append(cArr2[b5 & 15]);
                stringBuffer.append(' ');
            }
            stringBuffer.append('\n');
            printStream.println(stringBuffer.toString());
        }
    }

    public final void b() throws IOException {
        SelectionKey selectionKey = this.f60232b;
        selectionKey.selector().close();
        selectionKey.channel().close();
    }
}
