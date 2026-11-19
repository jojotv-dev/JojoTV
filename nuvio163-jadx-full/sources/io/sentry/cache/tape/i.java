package io.sentry.cache.tape;

import androidx.media3.common.C;
import androidx.media3.session.legacy.PlaybackStateCompat;
import dc.d1;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: loaded from: classes.dex */
public final class i implements Closeable, Iterable {

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    public static final byte[] f25433s = new byte[4096];

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public RandomAccessFile f25434b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final File f25435c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public long f25436e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public int f25437i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public g f25438m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public g f25439n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public final byte[] f25440o = new byte[32];

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public int f25441p = 0;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public final int f25442q;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    public boolean f25443r;

    public i(File file, RandomAccessFile randomAccessFile, int i10) throws IOException {
        this.f25435c = file;
        this.f25434b = randomAccessFile;
        this.f25442q = i10;
        W();
    }

    public static RandomAccessFile H(File file) throws IOException {
        if (!file.exists()) {
            File file2 = new File(file.getPath() + ".tmp");
            RandomAccessFile randomAccessFile = new RandomAccessFile(file2, "rwd");
            try {
                randomAccessFile.setLength(PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM);
                randomAccessFile.seek(0L);
                randomAccessFile.writeInt(C.RATE_UNSET_INT);
                randomAccessFile.writeLong(PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM);
                randomAccessFile.close();
                if (!file2.renameTo(file)) {
                    throw new IOException("Rename failed!");
                }
            } catch (Throwable th2) {
                randomAccessFile.close();
                throw th2;
            }
        }
        return new RandomAccessFile(file, "rwd");
    }

    public static int X(int i10, byte[] bArr) {
        return ((bArr[i10] & 255) << 24) + ((bArr[i10 + 1] & 255) << 16) + ((bArr[i10 + 2] & 255) << 8) + (bArr[i10 + 3] & 255);
    }

    public static long Y(int i10, byte[] bArr) {
        return ((((long) bArr[i10]) & 255) << 56) + ((((long) bArr[i10 + 1]) & 255) << 48) + ((((long) bArr[i10 + 2]) & 255) << 40) + ((((long) bArr[i10 + 3]) & 255) << 32) + ((((long) bArr[i10 + 4]) & 255) << 24) + ((((long) bArr[i10 + 5]) & 255) << 16) + ((((long) bArr[i10 + 6]) & 255) << 8) + (((long) bArr[i10 + 7]) & 255);
    }

    public static void f0(int i10, int i11, byte[] bArr) {
        bArr[i10] = (byte) (i11 >> 24);
        bArr[i10 + 1] = (byte) (i11 >> 16);
        bArr[i10 + 2] = (byte) (i11 >> 8);
        bArr[i10 + 3] = (byte) i11;
    }

    public static void g0(long j10, byte[] bArr, int i10) {
        bArr[i10] = (byte) (j10 >> 56);
        bArr[i10 + 1] = (byte) (j10 >> 48);
        bArr[i10 + 2] = (byte) (j10 >> 40);
        bArr[i10 + 3] = (byte) (j10 >> 32);
        bArr[i10 + 4] = (byte) (j10 >> 24);
        bArr[i10 + 5] = (byte) (j10 >> 16);
        bArr[i10 + 6] = (byte) (j10 >> 8);
        bArr[i10 + 7] = (byte) j10;
    }

    public final g U(long j10) {
        if (j10 != 0) {
            byte[] bArr = this.f25440o;
            if (b0(j10, bArr, 4)) {
                return new g(j10, X(0, bArr));
            }
        }
        return g.f25426c;
    }

    public final void W() throws IOException {
        this.f25434b.seek(0L);
        RandomAccessFile randomAccessFile = this.f25434b;
        byte[] bArr = this.f25440o;
        randomAccessFile.readFully(bArr);
        this.f25436e = Y(4, bArr);
        this.f25437i = X(12, bArr);
        long jY = Y(16, bArr);
        long jY2 = Y(24, bArr);
        if (this.f25436e > this.f25434b.length()) {
            throw new IOException("File is truncated. Expected length: " + this.f25436e + ", Actual length: " + this.f25434b.length());
        }
        if (this.f25436e <= 32) {
            throw new IOException(a0.i.h(this.f25436e, ") is invalid.", new StringBuilder("File is corrupt; length stored in header (")));
        }
        this.f25438m = U(jY);
        this.f25439n = U(jY2);
    }

    public final void Z(int i10) {
        if (i10 < 0) {
            throw new IllegalArgumentException(d1.f("Cannot remove negative (", i10, ") number of elements."));
        }
        if (i10 == 0) {
            return;
        }
        int i11 = this.f25437i;
        if (i10 == i11) {
            clear();
            return;
        }
        if (i11 == 0) {
            throw new NoSuchElementException();
        }
        if (i10 > i11) {
            throw new IllegalArgumentException(h1.f.j(this.f25437i, ").", o2.a.v(i10, "Cannot remove more elements (", ") than present in queue (")));
        }
        g gVar = this.f25438m;
        long j10 = gVar.f25427a;
        int iX = gVar.f25428b;
        long jD0 = j10;
        long j11 = 0;
        for (int i12 = 0; i12 < i10; i12++) {
            j11 += (long) (iX + 4);
            jD0 = d0(jD0 + 4 + ((long) iX));
            byte[] bArr = this.f25440o;
            if (!b0(jD0, bArr, 4)) {
                return;
            }
            iX = X(0, bArr);
        }
        e0(this.f25437i - i10, this.f25436e, jD0, this.f25439n.f25427a);
        this.f25437i -= i10;
        this.f25441p++;
        this.f25438m = new g(jD0, iX);
        while (j11 > 0) {
            int iMin = (int) Math.min(j11, 4096);
            c0(j10, f25433s, iMin);
            long j12 = iMin;
            j11 -= j12;
            j10 += j12;
        }
    }

    public final void a0() {
        this.f25434b.close();
        File file = this.f25435c;
        file.delete();
        this.f25434b = H(file);
        W();
    }

    public final boolean b0(long j10, byte[] bArr, int i10) throws IOException {
        try {
            long jD0 = d0(j10);
            long j11 = ((long) i10) + jD0;
            long j12 = this.f25436e;
            if (j11 <= j12) {
                this.f25434b.seek(jD0);
                this.f25434b.readFully(bArr, 0, i10);
                return true;
            }
            int i11 = (int) (j12 - jD0);
            this.f25434b.seek(jD0);
            this.f25434b.readFully(bArr, 0, i11);
            this.f25434b.seek(32L);
            this.f25434b.readFully(bArr, i11, i10 - i11);
            return true;
        } catch (EOFException unused) {
            a0();
            return false;
        } catch (IOException e10) {
            throw e10;
        } catch (Throwable unused2) {
            a0();
            return false;
        }
    }

    public final void c0(long j10, byte[] bArr, int i10) throws IOException {
        long jD0 = d0(j10);
        long j11 = ((long) i10) + jD0;
        long j12 = this.f25436e;
        if (j11 <= j12) {
            this.f25434b.seek(jD0);
            this.f25434b.write(bArr, 0, i10);
            return;
        }
        int i11 = (int) (j12 - jD0);
        this.f25434b.seek(jD0);
        this.f25434b.write(bArr, 0, i11);
        this.f25434b.seek(32L);
        this.f25434b.write(bArr, i11, i10 - i11);
    }

    public final void clear() {
        if (this.f25443r) {
            throw new IllegalStateException("closed");
        }
        e0(0, PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM, 0L, 0L);
        this.f25434b.seek(32L);
        this.f25434b.write(f25433s, 0, 4064);
        this.f25437i = 0;
        g gVar = g.f25426c;
        this.f25438m = gVar;
        this.f25439n = gVar;
        if (this.f25436e > PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM) {
            this.f25434b.setLength(PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM);
            this.f25434b.getChannel().force(true);
        }
        this.f25436e = PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM;
        this.f25441p++;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public final void close() {
        this.f25443r = true;
        this.f25434b.close();
    }

    public final long d0(long j10) {
        long j11 = this.f25436e;
        return j10 < j11 ? j10 : (j10 + 32) - j11;
    }

    public final void e0(int i10, long j10, long j11, long j12) throws IOException {
        this.f25434b.seek(0L);
        byte[] bArr = this.f25440o;
        f0(0, C.RATE_UNSET_INT, bArr);
        g0(j10, bArr, 4);
        f0(12, i10, bArr);
        g0(j11, bArr, 16);
        g0(j12, bArr, 24);
        this.f25434b.write(bArr, 0, 32);
    }

    @Override // java.lang.Iterable
    public final Iterator iterator() {
        return new h(this);
    }

    public final String toString() {
        return "QueueFile{file=" + this.f25435c + ", zero=true, length=" + this.f25436e + ", size=" + this.f25437i + ", first=" + this.f25438m + ", last=" + this.f25439n + '}';
    }
}
