package j$.time.temporal;

import com.fasterxml.jackson.core.JsonPointer;
import j$.time.DateTimeException;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/* JADX INFO: loaded from: classes2.dex */
public final class q implements Serializable {
    private static final long serialVersionUID = -7317881728594519368L;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final long f26695a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f26696b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final long f26697c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final long f26698d;

    public static q f(long j10, long j11) {
        if (j10 > j11) {
            throw new IllegalArgumentException("Minimum value must be less than maximum value");
        }
        return new q(j10, j10, j11, j11);
    }

    public static q g(long j10, long j11, long j12) {
        if (j10 > 1) {
            throw new IllegalArgumentException("Smallest minimum value must be less than largest minimum value");
        }
        if (j11 > j12) {
            throw new IllegalArgumentException("Smallest maximum value must be less than largest maximum value");
        }
        if (1 > j12) {
            throw new IllegalArgumentException("Minimum value must be less than maximum value");
        }
        return new q(j10, 1L, j11, j12);
    }

    public q(long j10, long j11, long j12, long j13) {
        this.f26695a = j10;
        this.f26696b = j11;
        this.f26697c = j12;
        this.f26698d = j13;
    }

    public final boolean d() {
        return this.f26695a >= -2147483648L && this.f26698d <= 2147483647L;
    }

    public final boolean e(long j10) {
        return j10 >= this.f26695a && j10 <= this.f26698d;
    }

    public final int a(long j10, TemporalField temporalField) {
        if (d() && e(j10)) {
            return (int) j10;
        }
        throw new DateTimeException(c(j10, temporalField));
    }

    public final void b(long j10, TemporalField temporalField) {
        if (!e(j10)) {
            throw new DateTimeException(c(j10, temporalField));
        }
    }

    public final String c(long j10, TemporalField temporalField) {
        if (temporalField != null) {
            return "Invalid value for " + temporalField + " (valid values " + this + "): " + j10;
        }
        return "Invalid value (valid values " + this + "): " + j10;
    }

    private void readObject(ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        long j10 = this.f26695a;
        long j11 = this.f26696b;
        if (j10 > j11) {
            throw new InvalidObjectException("Smallest minimum value must be less than largest minimum value");
        }
        long j12 = this.f26697c;
        long j13 = this.f26698d;
        if (j12 > j13) {
            throw new InvalidObjectException("Smallest maximum value must be less than largest maximum value");
        }
        if (j11 > j13) {
            throw new InvalidObjectException("Minimum value must be less than maximum value");
        }
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof q) {
            q qVar = (q) obj;
            if (this.f26695a == qVar.f26695a && this.f26696b == qVar.f26696b && this.f26697c == qVar.f26697c && this.f26698d == qVar.f26698d) {
                return true;
            }
        }
        return false;
    }

    public final int hashCode() {
        long j10 = this.f26695a;
        long j11 = this.f26696b;
        long j12 = j10 + (j11 << 16) + (j11 >> 48);
        long j13 = this.f26697c;
        long j14 = j12 + (j13 << 32) + (j13 >> 32);
        long j15 = this.f26698d;
        long j16 = j14 + (j15 << 48) + (j15 >> 16);
        return (int) (j16 ^ (j16 >>> 32));
    }

    public final String toString() {
        StringBuilder sb2 = new StringBuilder();
        sb2.append(this.f26695a);
        if (this.f26695a != this.f26696b) {
            sb2.append(JsonPointer.SEPARATOR);
            sb2.append(this.f26696b);
        }
        sb2.append(" - ");
        sb2.append(this.f26697c);
        if (this.f26697c != this.f26698d) {
            sb2.append(JsonPointer.SEPARATOR);
            sb2.append(this.f26698d);
        }
        return sb2.toString();
    }
}
