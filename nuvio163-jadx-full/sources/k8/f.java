package k8;

import a0.i;
import io.ktor.util.date.GMTDateParser;
import java.io.IOException;
import java.io.StringReader;

/* JADX INFO: loaded from: classes4.dex */
public final class f {

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final char[] f27994m = {'r', 'u', 'e'};

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public static final char[] f27995n = {'a', 'l', GMTDateParser.SECONDS, 'e'};

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public static final char[] f27996o = {'u', 'l', 'l'};

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public int f27998b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f27999c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public int f28000d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f28001e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public boolean f28002f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public int f28003g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public final StringReader f28004h;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public int f28006j;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    public boolean f28007l;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public int f27997a = 1;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final char[] f28005i = new char[32768];
    public final StringBuilder k = new StringBuilder();

    public f(StringReader stringReader) {
        int iF;
        this.f28004h = stringReader;
        this.f28002f = i();
        do {
            iF = f(256);
            for (int i10 = 0; i10 < iF; i10++) {
                char c5 = this.f28005i[this.f28003g];
                if (!h(c5)) {
                    g();
                    return;
                }
                if (c5 == '\n') {
                    this.f27997a++;
                    this.f27998b = this.f28003g + 1 + this.f27999c;
                    this.f28000d = 0;
                }
                this.f28003g++;
            }
        } while (iF > 0);
        this.f28002f = true;
    }

    public static boolean h(int i10) {
        return i10 == 32 || i10 == 10 || i10 == 13 || i10 == 9;
    }

    public final int a() {
        if (this.f28002f) {
            return -1;
        }
        int i10 = this.f28003g;
        char c5 = this.f28005i[i10];
        if (c5 == '\n') {
            this.f27997a++;
            this.f27998b = i10 + 1 + this.f27999c;
            this.f28000d = 0;
        }
        int i11 = i10 + 1;
        this.f28003g = i11;
        if (i11 >= this.f28006j) {
            this.f28002f = i();
        }
        return c5;
    }

    public final void b(char[] cArr, char c5) {
        int i10 = 0;
        if (f(cArr.length) < cArr.length) {
            throw d(c5, cArr, 0);
        }
        while (true) {
            int length = cArr.length;
            char[] cArr2 = this.f28005i;
            if (i10 >= length) {
                g();
                byte b5 = this.f28002f ? (byte) -1 : cArr2[this.f28003g];
                if (b5 != 9 && b5 != 10 && b5 != 13 && b5 != 32 && b5 != 44 && b5 != 58 && b5 != 91 && b5 != 93 && b5 != 123 && b5 != 125) {
                    throw d(c5, cArr, cArr.length);
                }
                return;
            }
            int i11 = this.f28003g;
            this.f28003g = i11 + 1;
            if (cArr2[i11] != cArr[i10]) {
                throw d(c5, cArr, i10);
            }
            i10++;
        }
    }

    public final void c() {
        int i10;
        char[] cArr;
        int i11;
        int i12;
        StringBuilder sb2 = this.k;
        sb2.setLength(0);
        loop0: while (true) {
            int i13 = 256;
            int iF = f(256);
            if (iF == 0) {
                throw e(null, "String was not terminated before end of input", true);
            }
            i10 = 0;
            while (true) {
                cArr = this.f28005i;
                if (i10 < iF) {
                    char cJ = j();
                    if (h(cJ)) {
                        break loop0;
                    }
                    char c5 = ':';
                    if (cJ == ':') {
                        break loop0;
                    }
                    char c10 = '\\';
                    if (cJ == '\\') {
                        sb2.append(cArr, (this.f28003g - i10) - 1, i10);
                        this.f28003g--;
                        while (true) {
                            int iF2 = f(i13);
                            if (iF2 == 0) {
                                throw e(null, "String was not terminated before end of input", true);
                            }
                            int i14 = this.f28003g + iF2;
                            while (true) {
                                i11 = this.f28003g;
                                if (i11 < i14) {
                                    char cJ2 = j();
                                    if (cJ2 != '\t' && cJ2 != '\n' && cJ2 != '\r' && cJ2 != ' ') {
                                        if (cJ2 != ',') {
                                            if (cJ2 != c5) {
                                                if (cJ2 != '{' && cJ2 != '}') {
                                                    switch (cJ2) {
                                                        case '[':
                                                        case ']':
                                                            break;
                                                        case '\\':
                                                            if (i14 - this.f28003g < 5) {
                                                                int iF3 = f(5);
                                                                int i15 = this.f28003g;
                                                                int i16 = i15 + iF3;
                                                                if (cArr[i15] == 'u' && iF3 < 5) {
                                                                    this.f28003g = this.f28006j;
                                                                    throw e(null, "EOF encountered in the middle of a string escape", false);
                                                                }
                                                                i14 = i16;
                                                            }
                                                            int i17 = this.f28003g;
                                                            this.f28003g = i17 + 1;
                                                            char c11 = cArr[i17];
                                                            if (c11 != '\"' && c11 != '/' && c11 != c10) {
                                                                if (c11 == 'b') {
                                                                    c11 = '\b';
                                                                } else if (c11 == 'f') {
                                                                    c11 = '\f';
                                                                } else if (c11 == 'n') {
                                                                    c11 = '\n';
                                                                } else if (c11 == 'r') {
                                                                    c11 = '\r';
                                                                } else if (c11 == 't') {
                                                                    c11 = '\t';
                                                                } else {
                                                                    if (c11 != 'u') {
                                                                        throw e(null, "Invalid escape: \\" + c11, false);
                                                                    }
                                                                    int i18 = 0;
                                                                    for (int i19 = 0; i19 < 4; i19++) {
                                                                        int i20 = i18 << 4;
                                                                        int i21 = this.f28003g;
                                                                        this.f28003g = i21 + 1;
                                                                        char c12 = cArr[i21];
                                                                        if (c12 >= '0' && c12 <= '9') {
                                                                            i12 = c12 - '0';
                                                                        } else if (c12 >= 'A' && c12 <= 'F') {
                                                                            i12 = c12 - '7';
                                                                        } else {
                                                                            if (c12 < 'a' || c12 > 'f') {
                                                                                StringBuilder sb3 = new StringBuilder("Expected unicode hex escape character: ");
                                                                                sb3.append(c12);
                                                                                sb3.append(" (");
                                                                                sb3.append((int) c12);
                                                                                sb3.append(")");
                                                                                throw e(null, sb3.toString(), false);
                                                                            }
                                                                            i12 = c12 - 'W';
                                                                        }
                                                                        i18 = i20 | i12;
                                                                    }
                                                                    c11 = (char) i18;
                                                                }
                                                            }
                                                            sb2.append(c11);
                                                            c10 = '\\';
                                                            c5 = ':';
                                                            break;
                                                        default:
                                                            sb2.append(cJ2);
                                                            c10 = '\\';
                                                            c5 = ':';
                                                            break;
                                                    }
                                                }
                                            }
                                        }
                                        throw e(null, "Invalid character in semi-string: " + cJ2, false);
                                    }
                                    g();
                                    return;
                                }
                            }
                            int i22 = this.f28006j;
                            if (i11 > i22) {
                                this.f28003g = i22;
                                throw e(null, "EOF encountered in the middle of a string escape", false);
                            }
                            i13 = 256;
                            c10 = '\\';
                            c5 = ':';
                        }
                    } else {
                        if (cJ == '[' || cJ == ']' || cJ == '{' || cJ == '}' || cJ == ',') {
                            throw e(null, "Invalid character in semi-string: " + cJ, false);
                        }
                        i10++;
                    }
                }
            }
            sb2.append(cArr, this.f28003g - iF, iF);
        }
        sb2.append(cArr, (this.f28003g - i10) - 1, i10);
        g();
    }

    public final d d(char c5, char[] cArr, int i10) {
        StringBuilder sb2 = new StringBuilder();
        sb2.append(c5);
        String str = "";
        sb2.append(cArr == null ? "" : new String(cArr, 0, i10));
        StringBuilder sb3 = new StringBuilder(sb2.toString());
        while (true) {
            byte b5 = this.f28002f ? (byte) -1 : this.f28005i[this.f28003g];
            if (((b5 < 65 || b5 > 90) && (b5 < 97 || b5 > 122)) || sb3.length() >= 15) {
                break;
            }
            sb3.append((char) a());
        }
        StringBuilder sb4 = new StringBuilder("Unexpected token '");
        sb4.append((Object) sb3);
        sb4.append("'");
        if (cArr != null) {
            str = ". Did you mean '" + c5 + new String(cArr) + "'?";
        }
        sb4.append(str);
        return e(null, sb4.toString(), true);
    }

    public final d e(Exception exc, String str, boolean z2) {
        if (z2) {
            StringBuilder sbS = i.s(str, " on line ");
            sbS.append(this.f27997a);
            sbS.append(", char ");
            sbS.append(this.f28001e);
            return new d(sbS.toString(), exc);
        }
        int iMax = Math.max(1, ((this.f28003g + this.f27999c) - this.f27998b) - this.f28000d);
        StringBuilder sbS2 = i.s(str, " on line ");
        sbS2.append(this.f27997a);
        sbS2.append(", char ");
        sbS2.append(iMax);
        return new d(sbS2.toString(), exc);
    }

    public final int f(int i10) {
        int i11;
        int i12 = this.f28006j;
        int i13 = i12 - i10;
        int i14 = this.f28003g;
        if (i13 >= i14) {
            return i10;
        }
        char[] cArr = this.f28005i;
        if (i14 > 0) {
            this.f27999c += i14;
            int i15 = i12 - i14;
            this.f28006j = i15;
            System.arraycopy(cArr, i14, cArr, 0, i15);
            this.f28003g = 0;
        }
        do {
            try {
                int length = cArr.length;
                int i16 = this.f28006j;
                if (length <= i16) {
                    throw new IOException("Unexpected internal error");
                }
                int i17 = this.f28004h.read(cArr, i16, cArr.length - i16);
                if (i17 <= 0) {
                    return this.f28006j - this.f28003g;
                }
                i11 = this.f28006j + i17;
                this.f28006j = i11;
            } catch (IOException e10) {
                throw e(e10, "IOException", true);
            }
        } while (i11 <= i10);
        return i11 - this.f28003g;
    }

    public final void g() {
        if (this.f28003g >= this.f28006j) {
            this.f28002f = i();
        }
    }

    public final boolean i() throws d {
        try {
            StringReader stringReader = this.f28004h;
            char[] cArr = this.f28005i;
            int i10 = stringReader.read(cArr, 0, cArr.length);
            if (i10 <= 0) {
                return true;
            }
            this.f27999c += this.f28006j;
            this.f28003g = 0;
            this.f28006j = i10;
            return false;
        } catch (IOException e10) {
            throw e(e10, "IOException", true);
        }
    }

    public final char j() {
        int i10 = this.f28003g;
        this.f28003g = i10 + 1;
        char c5 = this.f28005i[i10];
        if (c5 >= ' ') {
            return c5;
        }
        if (c5 == '\n') {
            this.f27997a++;
            this.f27998b = i10 + 2 + this.f27999c;
            this.f28000d = 0;
        }
        throw e(null, "Strings may not contain control characters: 0x" + Integer.toString(c5, 16), false);
    }
}
