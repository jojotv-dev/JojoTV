package io.sentry.vendor.gson.stream;

import io.ktor.sse.ServerSentEventKt;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public final class c implements Closeable, Flushable {

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public static final String[] f26319q = new String[128];

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Writer f26320b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int[] f26321c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f26322e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public String f26323i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public String f26324m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public boolean f26325n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public String f26326o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public final boolean f26327p;

    static {
        for (int i10 = 0; i10 <= 31; i10++) {
            f26319q[i10] = String.format("\\u%04x", Integer.valueOf(i10));
        }
        String[] strArr = f26319q;
        strArr[34] = "\\\"";
        strArr[92] = "\\\\";
        strArr[9] = "\\t";
        strArr[8] = "\\b";
        strArr[10] = "\\n";
        strArr[13] = "\\r";
        strArr[12] = "\\f";
        String[] strArr2 = (String[]) strArr.clone();
        strArr2[60] = "\\u003c";
        strArr2[62] = "\\u003e";
        strArr2[38] = "\\u0026";
        strArr2[61] = "\\u003d";
        strArr2[39] = "\\u0027";
    }

    public c(Writer writer) {
        int[] iArr = new int[8];
        this.f26321c = iArr;
        this.f26322e = 0;
        if (iArr.length == 0) {
            this.f26321c = Arrays.copyOf(iArr, 0);
        }
        int[] iArr2 = this.f26321c;
        int i10 = this.f26322e;
        this.f26322e = i10 + 1;
        iArr2[i10] = 6;
        this.f26324m = ServerSentEventKt.COLON;
        this.f26327p = true;
        this.f26320b = writer;
    }

    public final void A() throws IOException {
        if (this.f26326o != null) {
            if (!this.f26327p) {
                this.f26326o = null;
                return;
            }
            J();
        }
        g();
        this.f26320b.write("null");
    }

    public final int E() {
        int i10 = this.f26322e;
        if (i10 != 0) {
            return this.f26321c[i10 - 1];
        }
        throw new IllegalStateException("JsonWriter is closed.");
    }

    /* JADX WARN: Code duplicated, block: B:16:0x002d  */
    public final void H(String str) throws IOException {
        String str2;
        Writer writer = this.f26320b;
        writer.write(34);
        int length = str.length();
        int i10 = 0;
        for (int i11 = 0; i11 < length; i11++) {
            char cCharAt = str.charAt(i11);
            if (cCharAt < 128) {
                str2 = f26319q[cCharAt];
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

    public final void J() throws IOException {
        if (this.f26326o != null) {
            int iE = E();
            if (iE == 5) {
                this.f26320b.write(44);
            } else if (iE != 3) {
                throw new IllegalStateException("Nesting problem.");
            }
            n();
            this.f26321c[this.f26322e - 1] = 4;
            H(this.f26326o);
            this.f26326o = null;
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public final void close() throws IOException {
        this.f26320b.close();
        int i10 = this.f26322e;
        if (i10 > 1 || (i10 == 1 && this.f26321c[i10 - 1] != 7)) {
            throw new IOException("Incomplete document");
        }
        this.f26322e = 0;
    }

    @Override // java.io.Flushable
    public final void flush() throws IOException {
        if (this.f26322e == 0) {
            throw new IllegalStateException("JsonWriter is closed.");
        }
        this.f26320b.flush();
    }

    public final void g() throws IOException {
        int iE = E();
        if (iE == 1) {
            this.f26321c[this.f26322e - 1] = 2;
            n();
            return;
        }
        Writer writer = this.f26320b;
        if (iE == 2) {
            writer.append(',');
            n();
        } else {
            if (iE == 4) {
                writer.append((CharSequence) this.f26324m);
                this.f26321c[this.f26322e - 1] = 5;
                return;
            }
            if (iE != 6) {
                if (iE != 7) {
                    throw new IllegalStateException("Nesting problem.");
                }
                if (!this.f26325n) {
                    throw new IllegalStateException("JSON must have only one top-level value.");
                }
            }
            this.f26321c[this.f26322e - 1] = 7;
        }
    }

    public final void l(char c5, int i10, int i11) throws IOException {
        int iE = E();
        if (iE != i11 && iE != i10) {
            throw new IllegalStateException("Nesting problem.");
        }
        if (this.f26326o != null) {
            throw new IllegalStateException("Dangling name: " + this.f26326o);
        }
        this.f26322e--;
        if (iE == i11) {
            n();
        }
        this.f26320b.write(c5);
    }

    public final void n() throws IOException {
        if (this.f26323i == null) {
            return;
        }
        Writer writer = this.f26320b;
        writer.write(10);
        int i10 = this.f26322e;
        for (int i11 = 1; i11 < i10; i11++) {
            writer.write(this.f26323i);
        }
    }
}
