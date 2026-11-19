package d8;

import io.ktor.sse.ServerSentEventKt;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import w7.i;

/* JADX INFO: loaded from: classes.dex */
public class b implements Closeable, Flushable {

    /* JADX INFO: renamed from: t, reason: collision with root package name */
    public static final Pattern f11871t = Pattern.compile("-?(?:0|[1-9][0-9]*)(?:\\.[0-9]+)?(?:[eE][-+]?[0-9]+)?");

    /* JADX INFO: renamed from: u, reason: collision with root package name */
    public static final String[] f11872u = new String[128];

    /* JADX INFO: renamed from: v, reason: collision with root package name */
    public static final String[] f11873v;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Writer f11874b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int[] f11875c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f11876e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public i f11877i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public String f11878m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public String f11879n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public boolean f11880o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public int f11881p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public boolean f11882q;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    public String f11883r;

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    public boolean f11884s;

    static {
        for (int i10 = 0; i10 <= 31; i10++) {
            f11872u[i10] = String.format("\\u%04x", Integer.valueOf(i10));
        }
        String[] strArr = f11872u;
        strArr[34] = "\\\"";
        strArr[92] = "\\\\";
        strArr[9] = "\\t";
        strArr[8] = "\\b";
        strArr[10] = "\\n";
        strArr[13] = "\\r";
        strArr[12] = "\\f";
        String[] strArr2 = (String[]) strArr.clone();
        f11873v = strArr2;
        strArr2[60] = "\\u003c";
        strArr2[62] = "\\u003e";
        strArr2[38] = "\\u0026";
        strArr2[61] = "\\u003d";
        strArr2[39] = "\\u0027";
    }

    public b(Writer writer) {
        int[] iArr = new int[32];
        this.f11875c = iArr;
        this.f11876e = 0;
        if (iArr.length == 0) {
            this.f11875c = Arrays.copyOf(iArr, 0);
        }
        int[] iArr2 = this.f11875c;
        int i10 = this.f11876e;
        this.f11876e = i10 + 1;
        iArr2[i10] = 6;
        this.f11881p = 2;
        this.f11884s = true;
        Objects.requireNonNull(writer, "out == null");
        this.f11874b = writer;
        J(i.f62109d);
    }

    public final void A() throws IOException {
        if (this.f11880o) {
            return;
        }
        String str = this.f11877i.f62110a;
        Writer writer = this.f11874b;
        writer.write(str);
        int i10 = this.f11876e;
        for (int i11 = 1; i11 < i10; i11++) {
            writer.write(this.f11877i.f62111b);
        }
    }

    public b E() throws IOException {
        if (this.f11883r != null) {
            if (!this.f11884s) {
                this.f11883r = null;
                return this;
            }
            c0();
        }
        g();
        this.f11874b.write("null");
        return this;
    }

    public final int H() {
        int i10 = this.f11876e;
        if (i10 != 0) {
            return this.f11875c[i10 - 1];
        }
        throw new IllegalStateException("JsonWriter is closed.");
    }

    public final void J(i iVar) {
        Objects.requireNonNull(iVar);
        this.f11877i = iVar;
        this.f11879n = ",";
        if (iVar.f62112c) {
            this.f11878m = ": ";
            if (iVar.f62110a.isEmpty()) {
                this.f11879n = ", ";
            }
        } else {
            this.f11878m = ServerSentEventKt.COLON;
        }
        this.f11880o = this.f11877i.f62110a.isEmpty() && this.f11877i.f62111b.isEmpty();
    }

    public final void Q(int i10) {
        u4.i.a(i10);
        this.f11881p = i10;
    }

    /* JADX WARN: Code duplicated, block: B:20:0x0034  */
    public final void U(String str) throws IOException {
        String str2;
        String[] strArr = this.f11882q ? f11873v : f11872u;
        Writer writer = this.f11874b;
        writer.write(34);
        int length = str.length();
        int i10 = 0;
        for (int i11 = 0; i11 < length; i11++) {
            char cCharAt = str.charAt(i11);
            if (cCharAt < 128) {
                str2 = strArr[cCharAt];
                if (str2 != null) {
                    if (i10 < i11) {
                        writer.write(str, i10, i11 - i10);
                    }
                    writer.write(str2);
                    i10 = i11 + 1;
                }
            } else {
                if (cCharAt == 8232) {
                    str2 = "\\u2028";
                } else if (cCharAt == 8233) {
                    str2 = "\\u2029";
                }
                if (i10 < i11) {
                    writer.write(str, i10, i11 - i10);
                }
                writer.write(str2);
                i10 = i11 + 1;
            }
        }
        if (i10 < length) {
            writer.write(str, i10, length - i10);
        }
        writer.write(34);
    }

    public void W(double d10) throws IOException {
        c0();
        if (this.f11881p == 1 || !(Double.isNaN(d10) || Double.isInfinite(d10))) {
            g();
            this.f11874b.append((CharSequence) Double.toString(d10));
        } else {
            throw new IllegalArgumentException("Numeric values must be finite, but was " + d10);
        }
    }

    public void X(long j10) throws IOException {
        c0();
        g();
        this.f11874b.write(Long.toString(j10));
    }

    public void Y(Boolean bool) throws IOException {
        if (bool == null) {
            E();
            return;
        }
        c0();
        g();
        this.f11874b.write(bool.booleanValue() ? "true" : "false");
    }

    public void Z(Number number) throws IOException {
        if (number == null) {
            E();
            return;
        }
        c0();
        String string = number.toString();
        if (!string.equals("-Infinity") && !string.equals("Infinity") && !string.equals("NaN")) {
            Class<?> cls = number.getClass();
            if (cls != Integer.class && cls != Long.class && cls != Double.class && cls != Float.class && cls != Byte.class && cls != Short.class && cls != BigDecimal.class && cls != BigInteger.class && cls != AtomicInteger.class && cls != AtomicLong.class && !f11871t.matcher(string).matches()) {
                throw new IllegalArgumentException("String created by " + cls + " is not a valid JSON number: " + string);
            }
        } else if (this.f11881p != 1) {
            throw new IllegalArgumentException("Numeric values must be finite, but was ".concat(string));
        }
        g();
        this.f11874b.append((CharSequence) string);
    }

    public void a0(String str) throws IOException {
        if (str == null) {
            E();
            return;
        }
        c0();
        g();
        U(str);
    }

    public void b0(boolean z2) throws IOException {
        c0();
        g();
        this.f11874b.write(z2 ? "true" : "false");
    }

    public void beginArray() throws IOException {
        c0();
        g();
        int i10 = this.f11876e;
        int[] iArr = this.f11875c;
        if (i10 == iArr.length) {
            this.f11875c = Arrays.copyOf(iArr, i10 * 2);
        }
        int[] iArr2 = this.f11875c;
        int i11 = this.f11876e;
        this.f11876e = i11 + 1;
        iArr2[i11] = 1;
        this.f11874b.write(91);
    }

    public void beginObject() throws IOException {
        c0();
        g();
        int i10 = this.f11876e;
        int[] iArr = this.f11875c;
        if (i10 == iArr.length) {
            this.f11875c = Arrays.copyOf(iArr, i10 * 2);
        }
        int[] iArr2 = this.f11875c;
        int i11 = this.f11876e;
        this.f11876e = i11 + 1;
        iArr2[i11] = 3;
        this.f11874b.write(123);
    }

    public final void c0() throws IOException {
        if (this.f11883r != null) {
            int iH = H();
            if (iH == 5) {
                this.f11874b.write(this.f11879n);
            } else if (iH != 3) {
                throw new IllegalStateException("Nesting problem.");
            }
            A();
            this.f11875c[this.f11876e - 1] = 4;
            U(this.f11883r);
            this.f11883r = null;
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.f11874b.close();
        int i10 = this.f11876e;
        if (i10 > 1 || (i10 == 1 && this.f11875c[i10 - 1] != 7)) {
            throw new IOException("Incomplete document");
        }
        this.f11876e = 0;
    }

    public void endArray() throws IOException {
        l(']', 1, 2);
    }

    public void endObject() throws IOException {
        l('}', 3, 5);
    }

    @Override // java.io.Flushable
    public void flush() throws IOException {
        if (this.f11876e == 0) {
            throw new IllegalStateException("JsonWriter is closed.");
        }
        this.f11874b.flush();
    }

    public final void g() throws IOException {
        int iH = H();
        if (iH == 1) {
            this.f11875c[this.f11876e - 1] = 2;
            A();
            return;
        }
        Writer writer = this.f11874b;
        if (iH == 2) {
            writer.append((CharSequence) this.f11879n);
            A();
        } else {
            if (iH == 4) {
                writer.append((CharSequence) this.f11878m);
                this.f11875c[this.f11876e - 1] = 5;
                return;
            }
            if (iH != 6) {
                if (iH != 7) {
                    throw new IllegalStateException("Nesting problem.");
                }
                if (this.f11881p != 1) {
                    throw new IllegalStateException("JSON must have only one top-level value.");
                }
            }
            this.f11875c[this.f11876e - 1] = 7;
        }
    }

    public final void l(char c5, int i10, int i11) throws IOException {
        int iH = H();
        if (iH != i11 && iH != i10) {
            throw new IllegalStateException("Nesting problem.");
        }
        if (this.f11883r != null) {
            throw new IllegalStateException("Dangling name: " + this.f11883r);
        }
        this.f11876e--;
        if (iH == i11) {
            A();
        }
        this.f11874b.write(c5);
    }

    public void n(String str) {
        Objects.requireNonNull(str, "name == null");
        if (this.f11883r != null) {
            throw new IllegalStateException("Already wrote a name, expecting a value.");
        }
        int iH = H();
        if (iH != 3 && iH != 5) {
            throw new IllegalStateException("Please begin an object before writing a name.");
        }
        this.f11883r = str;
    }
}
