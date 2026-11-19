package q5;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class j {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f45674a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f45675b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final long f45676c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final long f45677d;

    public j(long j10, int i10, int i11, long j11) {
        this.f45674a = i10;
        this.f45675b = i11;
        this.f45676c = j10;
        this.f45677d = j11;
    }

    public static j a(File file) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
        try {
            j jVar = new j(dataInputStream.readLong(), dataInputStream.readInt(), dataInputStream.readInt(), dataInputStream.readLong());
            dataInputStream.close();
            return jVar;
        } catch (Throwable th2) {
            try {
                dataInputStream.close();
                throw th2;
            } catch (Throwable th3) {
                th2.addSuppressed(th3);
                throw th2;
            }
        }
    }

    public final void b(File file) throws IOException {
        file.delete();
        DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));
        try {
            dataOutputStream.writeInt(this.f45674a);
            dataOutputStream.writeInt(this.f45675b);
            dataOutputStream.writeLong(this.f45676c);
            dataOutputStream.writeLong(this.f45677d);
            dataOutputStream.close();
        } catch (Throwable th2) {
            try {
                dataOutputStream.close();
            } catch (Throwable th3) {
                th2.addSuppressed(th3);
            }
            throw th2;
        }
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && (obj instanceof j)) {
            j jVar = (j) obj;
            if (this.f45675b == jVar.f45675b && this.f45676c == jVar.f45676c && this.f45674a == jVar.f45674a && this.f45677d == jVar.f45677d) {
                return true;
            }
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(this.f45675b), Long.valueOf(this.f45676c), Integer.valueOf(this.f45674a), Long.valueOf(this.f45677d));
    }
}
