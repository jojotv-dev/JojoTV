package uo;

import java.io.Writer;

/* JADX INFO: loaded from: classes5.dex */
public final class h extends Writer {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final StringBuilder f60048b;

    public h(int i10) {
        StringBuilder sb2 = new StringBuilder(i10);
        this.f60048b = sb2;
        ((Writer) this).lock = sb2;
    }

    @Override // java.io.Writer, java.lang.Appendable
    public final Writer append(CharSequence charSequence) {
        write(String.valueOf(charSequence));
        return this;
    }

    public final String toString() {
        return this.f60048b.toString();
    }

    @Override // java.io.Writer
    public final void write(int i10) {
        this.f60048b.append((char) i10);
    }

    @Override // java.io.Writer, java.lang.Appendable
    public final Appendable append(CharSequence charSequence) {
        write(String.valueOf(charSequence));
        return this;
    }

    @Override // java.io.Writer
    public final void write(char[] cArr, int i10, int i11) {
        int i12;
        if (i10 < 0 || i10 > cArr.length || i11 < 0 || (i12 = i10 + i11) > cArr.length || i12 < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (i11 == 0) {
            return;
        }
        this.f60048b.append(cArr, i10, i11);
    }

    @Override // java.io.Writer, java.lang.Appendable
    public final Writer append(CharSequence charSequence, int i10, int i11) {
        if (charSequence == null) {
            charSequence = "null";
        }
        write(String.valueOf(charSequence.subSequence(i10, i11)));
        return this;
    }

    @Override // java.io.Writer
    public final void write(String str) {
        this.f60048b.append(str);
    }

    @Override // java.io.Writer, java.lang.Appendable
    public final Appendable append(CharSequence charSequence, int i10, int i11) {
        if (charSequence == null) {
            charSequence = "null";
        }
        write(String.valueOf(charSequence.subSequence(i10, i11)));
        return this;
    }

    @Override // java.io.Writer
    public final void write(String str, int i10, int i11) {
        this.f60048b.append((CharSequence) str, i10, i11 + i10);
    }

    @Override // java.io.Writer, java.lang.Appendable
    public final Writer append(char c5) {
        write(c5);
        return this;
    }

    @Override // java.io.Writer, java.lang.Appendable
    public final Appendable append(char c5) {
        write(c5);
        return this;
    }

    @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
    public final void close() {
    }

    @Override // java.io.Writer, java.io.Flushable
    public final void flush() {
    }
}
