package com.nuvio.vpn.android;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/* JADX INFO: loaded from: classes4.dex */
public final class g implements AutoCloseable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final byte[] f10868b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final char[] f10869c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final char[] f10870e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final boolean f10871i;

    public g(byte[] bArr, char[] cArr, char[] cArr2, boolean z2) {
        this.f10868b = bArr;
        this.f10869c = cArr;
        this.f10870e = cArr2;
        this.f10871i = z2 && cArr == null;
    }

    public static String A(String str) {
        if (str.isEmpty() || str.startsWith("#") || str.startsWith(";") || str.startsWith("<")) {
            return "";
        }
        String str2 = (String) ag.e.a(str).get(0);
        return str2.startsWith("--") ? str2.substring(2) : str2;
    }

    /* JADX WARN: Code duplicated, block: B:113:0x0202  */
    /* JADX WARN: Code duplicated, block: B:117:0x0209  */
    /* JADX WARN: Code duplicated, block: B:119:0x020c A[Catch: all -> 0x0207, TryCatch #3 {all -> 0x0207, blocks: (B:114:0x0203, B:119:0x020c, B:120:0x020f), top: B:124:0x0203 }] */
    public static g E(char[] cArr) throws Throwable {
        char[] cArr2;
        char[] cArr3;
        char c5;
        char[] cArrI = ag.e.i(cArr);
        StringBuilder sb2 = new StringBuilder();
        try {
            try {
                String[] strArrSplit = new String(cArrI).split("\\r?\\n", -1);
                int length = strArrSplit.length;
                char[] charArray = null;
                int i10 = 0;
                boolean z2 = false;
                boolean z10 = false;
                String str = null;
                boolean z11 = false;
                String strSubstring = null;
                boolean z12 = false;
                int i11 = 0;
                char[] charArray2 = null;
                boolean z13 = false;
                int i12 = 0;
                while (true) {
                    String str2 = "no";
                    String[] strArr = strArrSplit;
                    if (i10 >= length) {
                        boolean z14 = z11;
                        if (z2) {
                            throw new IOException("The saved OpenVPN service credentials are incomplete.");
                        }
                        if (z10) {
                            sb2.append("auth-user-pass\n");
                        }
                        if (str != null && !z14) {
                            sb2.append("data-ciphers ");
                            sb2.append(str);
                            sb2.append('\n');
                            if (!z12) {
                                sb2.append("data-ciphers-fallback ");
                                sb2.append(str);
                                sb2.append('\n');
                            }
                        }
                        sb2.append("remote-cert-tls server\nallow-compression ");
                        if (!z13) {
                            str2 = "asym";
                        }
                        sb2.append(str2);
                        sb2.append("\nverb 0\n");
                        g gVar = new g(sb2.toString().getBytes(StandardCharsets.UTF_8), charArray2, charArray, z10);
                        Arrays.fill(cArrI, (char) 0);
                        return gVar;
                    }
                    try {
                        String str3 = strArr[i10];
                        int i13 = length;
                        int i14 = i10;
                        String lowerCase = str3.trim().toLowerCase(Locale.ROOT);
                        if (z2) {
                            if (lowerCase.equals("</auth-user-pass>")) {
                                if (i11 != 2) {
                                    throw new IOException("The saved OpenVPN service credentials are invalid.");
                                }
                                z2 = false;
                            } else {
                                if (str3.isEmpty() || str3.indexOf(0) >= 0 || i11 >= 2) {
                                    throw new IOException("The saved OpenVPN service credentials are invalid.");
                                }
                                int i15 = i11 + 1;
                                if (i11 == 0) {
                                    charArray2 = str3.toCharArray();
                                } else {
                                    charArray = str3.toCharArray();
                                }
                                i11 = i15;
                            }
                        } else if (strSubstring != null) {
                            sb2.append(str3);
                            sb2.append('\n');
                            if (lowerCase.equals("</" + strSubstring + ">")) {
                                strSubstring = null;
                            }
                        } else if (!lowerCase.equals("<auth-user-pass>")) {
                            if (n(lowerCase)) {
                                strSubstring = lowerCase.substring(1, lowerCase.length() - 1);
                            }
                            String strA = A(lowerCase);
                            if (strA.equals("ns-cert-type")) {
                                List listA = ag.e.a(str3.trim());
                                int i16 = i12 + 1;
                                if (i16 == 1) {
                                    i12 = i16;
                                    if (listA.size() != 2 || !((String) listA.get(1)).equals("server")) {
                                    }
                                }
                                throw new IOException("OpenVPN compatibility requires a single server certificate purpose.");
                            }
                            if (strA.equals("auth-user-pass")) {
                                z2 = z2;
                                z11 = z11;
                                z10 = true;
                            } else {
                                if (strA.equals("cipher")) {
                                    str = (String) ag.e.a(str3.trim()).get(1);
                                }
                                z11 = (strA.equals("data-ciphers") || strA.equals("ncp-ciphers")) ? true : z11;
                                if (strA.equals("data-ciphers-fallback")) {
                                    z12 = true;
                                }
                                if (strA.equals("allow-compression")) {
                                    List listA2 = ag.e.a(lowerCase);
                                    if (listA2.size() == 2 && ((String) listA2.get(1)).equals("no")) {
                                        z13 = true;
                                    }
                                } else if (!strA.equals("persist-tun") && !strA.equals("verb")) {
                                    sb2.append(str3);
                                    sb2.append('\n');
                                }
                                z2 = z2;
                            }
                        } else {
                            if (charArray2 != null) {
                                throw new IOException("The OpenVPN service credentials are ambiguous.");
                            }
                            z2 = true;
                            z10 = true;
                        }
                        i10 = i14 + 1;
                        strArrSplit = strArr;
                        length = i13;
                    } catch (IOException e10) {
                        e = e10;
                        cArr3 = charArray;
                        cArr2 = charArray2;
                        if (cArr2 != null) {
                            c5 = 0;
                            try {
                                Arrays.fill(cArr2, (char) 0);
                            } catch (Throwable th2) {
                                th = th2;
                                Arrays.fill(cArrI, c5);
                                throw th;
                            }
                        } else {
                            c5 = 0;
                        }
                        if (cArr3 != null) {
                            Arrays.fill(cArr3, c5);
                        }
                        throw e;
                    } catch (RuntimeException e11) {
                        e = e11;
                        cArr3 = charArray;
                        cArr2 = charArray2;
                        if (cArr2 != null) {
                            c5 = 0;
                            Arrays.fill(cArr2, (char) 0);
                        } else {
                            c5 = 0;
                        }
                        if (cArr3 != null) {
                            Arrays.fill(cArr3, c5);
                        }
                        throw e;
                    }
                }
            } catch (Throwable th3) {
                th = th3;
                c5 = 0;
                Arrays.fill(cArrI, c5);
                throw th;
            }
        } catch (IOException | RuntimeException e12) {
            e = e12;
            cArr2 = null;
            cArr3 = null;
        }
    }

    public static char[] g(char[] cArr) throws IOException {
        if (ag.e.t(cArr) != 2) {
            return (char[]) cArr.clone();
        }
        char[] cArrI = ag.e.i(cArr);
        try {
            StringBuilder sb2 = new StringBuilder();
            String strSubstring = null;
            for (String str : new String(cArrI).split("\\r?\\n", -1)) {
                String lowerCase = str.trim().toLowerCase(Locale.ROOT);
                if (strSubstring != null) {
                    sb2.append(str);
                    sb2.append('\n');
                    if (lowerCase.equals("</" + strSubstring + ">")) {
                        strSubstring = null;
                    }
                } else {
                    if (n(lowerCase)) {
                        strSubstring = lowerCase.substring(1, lowerCase.length() - 1);
                    }
                    String strA = A(lowerCase);
                    if (strA.equals("ns-cert-type")) {
                        sb2.append("remote-cert-tls server\n");
                    } else if (!strA.equals("fragment")) {
                        sb2.append(str);
                        sb2.append('\n');
                    }
                }
            }
            return sb2.toString().toCharArray();
        } finally {
            Arrays.fill(cArrI, (char) 0);
        }
    }

    public static byte[] l(String str, char[] cArr) throws IOException {
        if (!str.equals("username") && !str.equals("password")) {
            throw new IOException("Invalid credential request.");
        }
        if (cArr == null || cArr.length == 0 || cArr.length > 1024) {
            throw new IOException("OpenVPN service credentials are required.");
        }
        StringBuilder sb2 = new StringBuilder(str);
        sb2.append(" \"Auth\" \"");
        for (char c5 : cArr) {
            if (c5 == '\r' || c5 == '\n' || c5 == 0) {
                throw new IOException("Invalid OpenVPN service credentials.");
            }
            if (c5 == '\\' || c5 == '\"') {
                sb2.append('\\');
            }
            sb2.append(c5);
        }
        sb2.append("\"\n");
        return sb2.toString().getBytes(StandardCharsets.UTF_8);
    }

    public static boolean n(String str) {
        return str.startsWith("<") && str.endsWith(">") && !str.startsWith("</") && !str.equals("<connection>");
    }

    @Override // java.lang.AutoCloseable
    public final void close() {
        Arrays.fill(this.f10868b, (byte) 0);
        char[] cArr = this.f10869c;
        if (cArr != null) {
            Arrays.fill(cArr, (char) 0);
        }
        char[] cArr2 = this.f10870e;
        if (cArr2 != null) {
            Arrays.fill(cArr2, (char) 0);
        }
    }
}
