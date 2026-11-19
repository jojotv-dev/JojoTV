package j$.sun.nio.cs;

import java.nio.CharBuffer;
import java.nio.charset.CoderResult;

/* JADX INFO: loaded from: classes2.dex */
public final class e {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public CoderResult f26423a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public boolean f26424b;

    public final int a(char c5, CharBuffer charBuffer) {
        if (Character.isHighSurrogate(c5)) {
            if (!charBuffer.hasRemaining()) {
                this.f26423a = CoderResult.UNDERFLOW;
                return -1;
            }
            char c10 = charBuffer.get();
            if (Character.isLowSurrogate(c10)) {
                int codePoint = Character.toCodePoint(c5, c10);
                this.f26424b = true;
                this.f26423a = null;
                return codePoint;
            }
            this.f26423a = CoderResult.malformedForLength(1);
            return -1;
        }
        if (Character.isLowSurrogate(c5)) {
            this.f26423a = CoderResult.malformedForLength(1);
            return -1;
        }
        this.f26424b = false;
        this.f26423a = null;
        return c5;
    }

    public final int b(char c5, char[] cArr, int i10, int i11) {
        if (!Character.isHighSurrogate(c5)) {
            if (Character.isLowSurrogate(c5)) {
                this.f26423a = CoderResult.malformedForLength(1);
                return -1;
            }
            this.f26424b = false;
            this.f26423a = null;
            return c5;
        }
        if (i11 - i10 < 2) {
            this.f26423a = CoderResult.UNDERFLOW;
            return -1;
        }
        char c10 = cArr[i10 + 1];
        if (Character.isLowSurrogate(c10)) {
            int codePoint = Character.toCodePoint(c5, c10);
            this.f26424b = true;
            this.f26423a = null;
            return codePoint;
        }
        this.f26423a = CoderResult.malformedForLength(1);
        return -1;
    }
}
