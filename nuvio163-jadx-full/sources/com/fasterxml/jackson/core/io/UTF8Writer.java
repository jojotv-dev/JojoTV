package com.fasterxml.jackson.core.io;

import androidx.media3.extractor.ts.PsExtractor;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/* JADX INFO: loaded from: classes3.dex */
public final class UTF8Writer extends Writer {
    static final int SURR1_FIRST = 55296;
    static final int SURR1_LAST = 56319;
    static final int SURR2_FIRST = 56320;
    static final int SURR2_LAST = 57343;
    public static final int SURROGATE_BASE = -56613888;
    private final IOContext _context;
    private OutputStream _out;
    private byte[] _outBuffer;
    private final int _outBufferEnd;
    private int _outPtr;
    private int _surrogate;

    public UTF8Writer(IOContext iOContext, OutputStream outputStream) {
        this._context = iOContext;
        this._out = outputStream;
        byte[] bArrAllocWriteEncodingBuffer = iOContext.allocWriteEncodingBuffer();
        this._outBuffer = bArrAllocWriteEncodingBuffer;
        this._outBufferEnd = bArrAllocWriteEncodingBuffer.length - 4;
        this._outPtr = 0;
    }

    public static void illegalSurrogate(int i10) throws IOException {
        throw new IOException(illegalSurrogateDesc(i10));
    }

    public static String illegalSurrogateDesc(int i10) {
        if (i10 > 1114111) {
            return "Illegal character point (0x" + Integer.toHexString(i10) + ") to output; max is 0x10FFFF as per RFC 4627";
        }
        if (i10 < 55296) {
            return "Illegal character point (0x" + Integer.toHexString(i10) + ") to output";
        }
        if (i10 <= 56319) {
            return "Unmatched first part of surrogate pair (0x" + Integer.toHexString(i10) + ")";
        }
        return "Unmatched second part of surrogate pair (0x" + Integer.toHexString(i10) + ")";
    }

    @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        OutputStream outputStream = this._out;
        if (outputStream != null) {
            int i10 = this._outPtr;
            if (i10 > 0) {
                outputStream.write(this._outBuffer, 0, i10);
                this._outPtr = 0;
            }
            OutputStream outputStream2 = this._out;
            this._out = null;
            byte[] bArr = this._outBuffer;
            if (bArr != null) {
                this._outBuffer = null;
                this._context.releaseWriteEncodingBuffer(bArr);
            }
            outputStream2.close();
            int i11 = this._surrogate;
            this._surrogate = 0;
            if (i11 > 0) {
                illegalSurrogate(i11);
            }
        }
        this._context.close();
    }

    public int convertSurrogate(int i10) throws IOException {
        int i11 = this._surrogate;
        this._surrogate = 0;
        if (i10 >= 56320 && i10 <= 57343) {
            return (i11 << 10) + i10 + SURROGATE_BASE;
        }
        throw new IOException("Broken surrogate pair: first char 0x" + Integer.toHexString(i11) + ", second 0x" + Integer.toHexString(i10) + "; illegal combination");
    }

    @Override // java.io.Writer, java.io.Flushable
    public void flush() throws IOException {
        OutputStream outputStream = this._out;
        if (outputStream != null) {
            int i10 = this._outPtr;
            if (i10 > 0) {
                outputStream.write(this._outBuffer, 0, i10);
                this._outPtr = 0;
            }
            this._out.flush();
        }
    }

    @Override // java.io.Writer
    public void write(char[] cArr) throws IOException {
        write(cArr, 0, cArr.length);
    }

    @Override // java.io.Writer, java.lang.Appendable
    public Writer append(char c5) throws IOException {
        write(c5);
        return this;
    }

    @Override // java.io.Writer
    public void write(char[] cArr, int i10, int i11) throws IOException {
        if (i11 < 2) {
            if (i11 == 1) {
                write(cArr[i10]);
                return;
            }
            return;
        }
        if (this._surrogate > 0) {
            i11--;
            write(convertSurrogate(cArr[i10]));
            i10++;
        }
        int i12 = this._outPtr;
        byte[] bArr = this._outBuffer;
        int i13 = this._outBufferEnd;
        int i14 = i11 + i10;
        while (i10 < i14) {
            if (i12 >= i13) {
                this._out.write(bArr, 0, i12);
                i12 = 0;
            }
            int i15 = i10 + 1;
            char c5 = cArr[i10];
            if (c5 < 128) {
                int i16 = i12 + 1;
                bArr[i12] = (byte) c5;
                int i17 = i14 - i15;
                int i18 = i13 - i16;
                if (i17 > i18) {
                    i17 = i18;
                }
                int i19 = i17 + i15;
                while (true) {
                    i10 = i15;
                    i12 = i16;
                    if (i10 >= i19) {
                        continue;
                    } else {
                        i15 = i10 + 1;
                        c5 = cArr[i10];
                        if (c5 < 128) {
                            i16 = i12 + 1;
                            bArr[i12] = (byte) c5;
                        }
                    }
                }
            }
            if (c5 < 2048) {
                int i20 = i12 + 1;
                bArr[i12] = (byte) ((c5 >> 6) | PsExtractor.AUDIO_STREAM);
                i12 += 2;
                bArr[i20] = (byte) ((c5 & '?') | 128);
            } else if (c5 < 55296 || c5 > 57343) {
                bArr[i12] = (byte) ((c5 >> '\f') | 224);
                int i21 = i12 + 2;
                bArr[i12 + 1] = (byte) (((c5 >> 6) & 63) | 128);
                i12 += 3;
                bArr[i21] = (byte) ((c5 & '?') | 128);
            } else {
                if (c5 > 56319) {
                    this._outPtr = i12;
                    illegalSurrogate(c5);
                }
                this._surrogate = c5;
                if (i15 >= i14) {
                    break;
                }
                i10 = i15 + 1;
                int iConvertSurrogate = convertSurrogate(cArr[i15]);
                if (iConvertSurrogate > 1114111) {
                    this._outPtr = i12;
                    illegalSurrogate(iConvertSurrogate);
                }
                bArr[i12] = (byte) ((iConvertSurrogate >> 18) | PsExtractor.VIDEO_STREAM_MASK);
                bArr[i12 + 1] = (byte) (((iConvertSurrogate >> 12) & 63) | 128);
                int i22 = i12 + 3;
                bArr[i12 + 2] = (byte) (((iConvertSurrogate >> 6) & 63) | 128);
                i12 += 4;
                bArr[i22] = (byte) ((iConvertSurrogate & 63) | 128);
            }
            i10 = i15;
        }
        this._outPtr = i12;
    }

    @Override // java.io.Writer
    public void write(int i10) throws IOException {
        int i11;
        if (this._surrogate > 0) {
            i10 = convertSurrogate(i10);
        } else if (i10 >= 55296 && i10 <= 57343) {
            if (i10 > 56319) {
                illegalSurrogate(i10);
            }
            this._surrogate = i10;
            return;
        }
        int i12 = this._outPtr;
        if (i12 >= this._outBufferEnd) {
            this._out.write(this._outBuffer, 0, i12);
            this._outPtr = 0;
        }
        if (i10 < 128) {
            byte[] bArr = this._outBuffer;
            int i13 = this._outPtr;
            this._outPtr = i13 + 1;
            bArr[i13] = (byte) i10;
            return;
        }
        int i14 = this._outPtr;
        if (i10 < 2048) {
            byte[] bArr2 = this._outBuffer;
            int i15 = i14 + 1;
            bArr2[i14] = (byte) ((i10 >> 6) | PsExtractor.AUDIO_STREAM);
            i11 = i14 + 2;
            bArr2[i15] = (byte) ((i10 & 63) | 128);
        } else if (i10 <= 65535) {
            byte[] bArr3 = this._outBuffer;
            bArr3[i14] = (byte) ((i10 >> 12) | 224);
            int i16 = i14 + 2;
            bArr3[i14 + 1] = (byte) (((i10 >> 6) & 63) | 128);
            i11 = i14 + 3;
            bArr3[i16] = (byte) ((i10 & 63) | 128);
        } else {
            if (i10 > 1114111) {
                illegalSurrogate(i10);
            }
            byte[] bArr4 = this._outBuffer;
            bArr4[i14] = (byte) ((i10 >> 18) | PsExtractor.VIDEO_STREAM_MASK);
            bArr4[i14 + 1] = (byte) (((i10 >> 12) & 63) | 128);
            int i17 = i14 + 3;
            bArr4[i14 + 2] = (byte) (((i10 >> 6) & 63) | 128);
            i11 = i14 + 4;
            bArr4[i17] = (byte) ((i10 & 63) | 128);
        }
        this._outPtr = i11;
    }

    @Override // java.io.Writer
    public void write(String str) throws IOException {
        write(str, 0, str.length());
    }

    @Override // java.io.Writer
    public void write(String str, int i10, int i11) throws IOException {
        if (i11 < 2) {
            if (i11 == 1) {
                write(str.charAt(i10));
                return;
            }
            return;
        }
        if (this._surrogate > 0) {
            i11--;
            write(convertSurrogate(str.charAt(i10)));
            i10++;
        }
        int i12 = this._outPtr;
        byte[] bArr = this._outBuffer;
        int i13 = this._outBufferEnd;
        int i14 = i11 + i10;
        while (i10 < i14) {
            if (i12 >= i13) {
                this._out.write(bArr, 0, i12);
                i12 = 0;
            }
            int i15 = i10 + 1;
            char cCharAt = str.charAt(i10);
            if (cCharAt < 128) {
                int i16 = i12 + 1;
                bArr[i12] = (byte) cCharAt;
                int i17 = i14 - i15;
                int i18 = i13 - i16;
                if (i17 > i18) {
                    i17 = i18;
                }
                int i19 = i17 + i15;
                while (true) {
                    i10 = i15;
                    i12 = i16;
                    if (i10 >= i19) {
                        continue;
                    } else {
                        i15 = i10 + 1;
                        cCharAt = str.charAt(i10);
                        if (cCharAt < 128) {
                            i16 = i12 + 1;
                            bArr[i12] = (byte) cCharAt;
                        }
                    }
                }
            }
            if (cCharAt < 2048) {
                int i20 = i12 + 1;
                bArr[i12] = (byte) ((cCharAt >> 6) | PsExtractor.AUDIO_STREAM);
                i12 += 2;
                bArr[i20] = (byte) ((cCharAt & '?') | 128);
            } else if (cCharAt >= 55296 && cCharAt <= 57343) {
                if (cCharAt > 56319) {
                    this._outPtr = i12;
                    illegalSurrogate(cCharAt);
                }
                this._surrogate = cCharAt;
                if (i15 >= i14) {
                    break;
                }
                i10 = i15 + 1;
                int iConvertSurrogate = convertSurrogate(str.charAt(i15));
                if (iConvertSurrogate > 1114111) {
                    this._outPtr = i12;
                    illegalSurrogate(iConvertSurrogate);
                }
                bArr[i12] = (byte) ((iConvertSurrogate >> 18) | PsExtractor.VIDEO_STREAM_MASK);
                bArr[i12 + 1] = (byte) (((iConvertSurrogate >> 12) & 63) | 128);
                int i21 = i12 + 3;
                bArr[i12 + 2] = (byte) (((iConvertSurrogate >> 6) & 63) | 128);
                i12 += 4;
                bArr[i21] = (byte) ((iConvertSurrogate & 63) | 128);
            } else {
                bArr[i12] = (byte) ((cCharAt >> '\f') | 224);
                int i22 = i12 + 2;
                bArr[i12 + 1] = (byte) (((cCharAt >> 6) & 63) | 128);
                i12 += 3;
                bArr[i22] = (byte) ((cCharAt & '?') | 128);
            }
            i10 = i15;
        }
        this._outPtr = i12;
    }
}
