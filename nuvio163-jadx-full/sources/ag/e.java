package ag;

import androidx.media3.container.MdtaMetadataEntry;
import androidx.media3.container.NalUnitUtil;
import androidx.media3.exoplayer.rtsp.SessionDescription;
import androidx.media3.extractor.AacUtil;
import androidx.media3.extractor.flac.FlacConstants;
import androidx.media3.extractor.ts.TsExtractor;
import com.fasterxml.jackson.core.io.doubleparser.FastDoubleMath;
import com.liskovsoft.mediaserviceinterfaces.data.SponsorSegment;
import io.ktor.sse.ServerSentEventKt;
import is.xyz.mpv.MPV;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import jcifs.internal.fscc.FileInformation;
import jcifs.internal.smb1.ServerMessageBlock;
import jcifs.internal.smb1.trans.SmbComTransaction;
import jcifs.internal.smb2.info.Smb2QueryDirectoryRequest;
import kotlin.io.encoding.Base64;
import kotlin.text.Typography;
import kotlinx.coroutines.internal.LockFreeTaskQueueCore;
import okhttp3.internal.ws.WebSocketProtocol;
import okio.Utf8;
import w.h0;

/* JADX INFO: loaded from: classes4.dex */
public abstract class e {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final byte[] f1712a = {78, 86, 80, 1};

    public static List a(String str) throws IOException {
        if (str == null) {
            throw y();
        }
        ArrayList arrayList = new ArrayList();
        StringBuilder sb2 = new StringBuilder();
        int i10 = 0;
        char c5 = 0;
        while (i10 < str.length()) {
            char cCharAt = str.charAt(i10);
            if (cCharAt < ' ' && cCharAt != '\t') {
                throw y();
            }
            if (cCharAt == '\\' && c5 != 3) {
                i10++;
                if (i10 == str.length()) {
                    throw y();
                }
                char cCharAt2 = str.charAt(i10);
                if (cCharAt2 != '\\' && cCharAt2 != '\"' && cCharAt2 != ' ' && cCharAt2 != '\t') {
                    throw y();
                }
                sb2.append(cCharAt2);
                if (c5 == 0) {
                    c5 = 1;
                }
            } else if (c5 != 0) {
                if ((c5 == 2 && cCharAt == '\"') || (c5 == 3 && cCharAt == '\'')) {
                    arrayList.add(sb2.toString());
                    sb2.setLength(0);
                } else if (c5 == 1 && (cCharAt == ' ' || cCharAt == '\t')) {
                    arrayList.add(sb2.toString());
                    sb2.setLength(0);
                } else {
                    sb2.append(cCharAt);
                }
                c5 = 0;
            } else {
                if (cCharAt == '#' || cCharAt == ';') {
                    break;
                }
                if (cCharAt != ' ' && cCharAt != '\t') {
                    if (cCharAt == '\"') {
                        c5 = 2;
                    } else if (cCharAt == '\'') {
                        c5 = 3;
                    } else {
                        sb2.append(cCharAt);
                        c5 = 1;
                    }
                }
            }
            i10++;
        }
        if (c5 == 2 || c5 == 3) {
            throw y();
        }
        if (c5 == 1) {
            arrayList.add(sb2.toString());
        }
        return Collections.unmodifiableList(arrayList);
    }

    public static boolean b(int i10, int i11, List list) {
        return list.size() - 1 >= i10 && list.size() - 1 <= i11;
    }

    public static f c(byte[] bArr) throws IOException {
        int i10;
        int length = bArr.length - 22;
        while (true) {
            if (length < Math.max(0, bArr.length - 65557)) {
                length = -1;
                break;
            }
            if (x(length, bArr) == 101010256) {
                if (w(length + 20, bArr) + length + 22 == bArr.length) {
                    break;
                }
            }
            length--;
        }
        if (length >= 0 && w(length + 4, bArr) == 0 && w(length + 6, bArr) == 0) {
            int i11 = length + 10;
            if (w(length + 8, bArr) == w(i11, bArr)) {
                int iW = w(i11, bArr);
                long jX = x(length + 12, bArr);
                long jX2 = x(length + 16, bArr);
                if (iW < 1 || iW > 32 || jX + jX2 != length) {
                    throw new IOException("ZIP bundle must contain 1 to 32 entries and one .ovpn file.");
                }
                HashMap map = new HashMap();
                HashSet hashSet = new HashSet();
                HashMap map2 = new HashMap();
                ArrayList<b> arrayList = new ArrayList();
                int i12 = (int) jX2;
                int i13 = 0;
                int i14 = 0;
                while (i13 < iW) {
                    if (i12 < 0 || i12 > length - 46 || x(i12, bArr) != 33639248) {
                        throw new IOException("ZIP bundle is damaged or incomplete.");
                    }
                    int iW2 = w(i12 + 28, bArr);
                    int i15 = iW;
                    int i16 = i12 + 46;
                    long j10 = jX2;
                    int i17 = i16 + iW2;
                    int iW3 = w(i12 + 30, bArr) + i17 + w(i12 + 32, bArr);
                    if (iW3 <= length && w(i12 + 34, bArr) == 0) {
                        int i18 = i12 + 8;
                        if ((w(i18, bArr) & 65) == 0) {
                            int iW4 = w(i12 + 10, bArr);
                            int i19 = i13;
                            if (iW4 != 0 && iW4 != 8) {
                                throw new IOException("ZIP bundle uses an unsupported compression method.");
                            }
                            String strV = v(Arrays.copyOfRange(bArr, i16, i17));
                            boolean zEndsWith = strV.endsWith("/");
                            r(strV, zEndsWith);
                            int i20 = i14;
                            String strF = zEndsWith ? a4.b.f(1, 0, strV) : strV;
                            HashSet hashSet2 = hashSet;
                            if (!hashSet.add(strF.toLowerCase(Locale.ROOT))) {
                                throw new IOException("ZIP bundle contains duplicate or ambiguous paths.");
                            }
                            String[] strArrSplit = strF.split("/");
                            int length2 = strArrSplit.length;
                            int i21 = length;
                            String strConcat = "";
                            int i22 = 0;
                            while (i22 < length2) {
                                int i23 = i22;
                                String strX = h0.x(strConcat, strArrSplit[i23]);
                                String str = (String) map2.put(strX.toLowerCase(Locale.ROOT), strX);
                                if (str != null && !str.equals(strX)) {
                                    throw new IOException("ZIP bundle contains duplicate or ambiguous paths.");
                                }
                                strConcat = strX.concat("/");
                                i22 = i23 + 1;
                            }
                            int iX = ((int) (x(i12 + 38, bArr) >>> 16)) & 61440;
                            if (iX != 0) {
                                if (iX != (zEndsWith ? 16384 : 32768)) {
                                    throw new IOException("ZIP bundle may contain only regular certificate files.");
                                }
                            }
                            if (zEndsWith) {
                                i10 = i20;
                            } else {
                                Locale locale = Locale.ROOT;
                                if (strV.toLowerCase(locale).endsWith(".ovpn")) {
                                    i10 = i20 + 1;
                                } else {
                                    if (!strV.toLowerCase(locale).matches(".*\\.(crt|cer|pem|key|crl)")) {
                                        throw new IOException("ZIP bundle may contain only one .ovpn and certificate or key files.");
                                    }
                                    i10 = i20;
                                }
                            }
                            long jX3 = x(i12 + 24, bArr);
                            long jX4 = x(i12 + 20, bArr);
                            long jX5 = x(i12 + 42, bArr);
                            if (jX3 <= 1048576) {
                                int i24 = i10;
                                if (jX4 <= bArr.length && jX5 < j10) {
                                    int i25 = (int) jX5;
                                    if (i25 > j10 - 30 || x(i25, bArr) != 67324752 || w(i25 + 6, bArr) != w(i18, bArr) || w(i25 + 8, bArr) != iW4) {
                                        throw new IOException("ZIP bundle has inconsistent file headers.");
                                    }
                                    int iW5 = w(i25 + 26, bArr);
                                    long jW = jX5 + 30 + ((long) iW5) + ((long) w(i25 + 28, bArr));
                                    if (iW5 == iW2) {
                                        long j11 = jW + jX4;
                                        if (j11 <= j10) {
                                            int i26 = i25 + 30;
                                            int i27 = 0;
                                            while (i27 < iW2) {
                                                int i28 = i25;
                                                int i29 = iW4;
                                                if (bArr[i26 + i27] == bArr[i16 + i27]) {
                                                    i27++;
                                                    iW4 = i29;
                                                    i25 = i28;
                                                }
                                            }
                                            b bVar = new b(iW4, x(i12 + 16, bArr), jX3, jX4, i25, (int) j11, w(i18, bArr));
                                            map.put(strV, bVar);
                                            arrayList.add(bVar);
                                            i13 = i19 + 1;
                                            i14 = i24;
                                            iW = i15;
                                            i12 = iW3;
                                            jX2 = j10;
                                            hashSet = hashSet2;
                                            length = i21;
                                        }
                                    }
                                    throw new IOException("ZIP bundle has inconsistent file headers.");
                                }
                            }
                            throw new IOException("ZIP bundle exceeds its size limit or is damaged.");
                        }
                    }
                    throw new IOException("Encrypted or split ZIP bundles are not supported.");
                }
                long j12 = jX2;
                int i30 = i14;
                if (i12 != length || i30 != 1) {
                    throw new IOException("ZIP bundle must contain exactly one .ovpn configuration.");
                }
                Collections.sort(arrayList, Comparator.comparingInt(new a()));
                int i31 = 0;
                for (b bVar2 : arrayList) {
                    int i32 = bVar2.f1705e;
                    long j13 = bVar2.f1703c;
                    long j14 = bVar2.f1704d;
                    long j15 = bVar2.f1702b;
                    if (i32 != i31) {
                        throw new IOException("ZIP bundle has overlapping or inconsistent file records.");
                    }
                    i31 = bVar2.f1706f;
                    if ((bVar2.f1707g & 8) != 0) {
                        long j16 = j12 - 12;
                        if (i31 > j16) {
                            throw new IOException("ZIP bundle has an incomplete data descriptor.");
                        }
                        if (x(i31, bArr) == 134695760) {
                            i31 += 4;
                        }
                        if (i31 > j16 || x(i31, bArr) != j15 || x(i31 + 4, bArr) != j14 || x(i31 + 8, bArr) != j13) {
                            throw new IOException("ZIP bundle has an invalid data descriptor.");
                        }
                        i31 += 12;
                    } else if (x(i32 + 14, bArr) != j15 || x(i32 + 18, bArr) != j14 || x(i32 + 22, bArr) != j13) {
                        throw new IOException("ZIP bundle has inconsistent file headers.");
                    }
                }
                if (i31 != j12) {
                    throw new IOException("ZIP bundle has inconsistent file records.");
                }
                HashMap map3 = new HashMap();
                try {
                    ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(bArr), StandardCharsets.UTF_8);
                    try {
                        HashSet hashSet3 = new HashSet();
                        String name = null;
                        int length3 = 0;
                        while (true) {
                            ZipEntry nextEntry = zipInputStream.getNextEntry();
                            if (nextEntry == null) {
                                if (hashSet3.size() != map.size()) {
                                    throw new IOException("ZIP bundle is damaged or incomplete.");
                                }
                                zipInputStream.close();
                                if (name == null) {
                                    throw new IOException("ZIP bundle must contain exactly one .ovpn configuration.");
                                }
                                byte[] bytes = g(v((byte[]) map3.get(name)), name, map3).getBytes(StandardCharsets.UTF_8);
                                try {
                                    if (bytes.length > 1048576) {
                                        throw new IOException("Combined OpenVPN configuration exceeds 1 MiB.");
                                    }
                                    f fVarO = o(new ByteArrayInputStream(bytes), 1);
                                    Arrays.fill(bytes, (byte) 0);
                                    Iterator it = map3.values().iterator();
                                    while (it.hasNext()) {
                                        Arrays.fill((byte[]) it.next(), (byte) 0);
                                    }
                                    return fVarO;
                                } catch (Throwable th2) {
                                    Arrays.fill(bytes, (byte) 0);
                                    throw th2;
                                }
                            }
                            b bVar3 = (b) map.get(nextEntry.getName());
                            if (bVar3 == null || !hashSet3.add(nextEntry.getName())) {
                                throw new IOException("ZIP bundle contains ambiguous entries.");
                            }
                            byte[] bArrP = p(zipInputStream, 1048576 - length3);
                            length3 += bArrP.length;
                            if (nextEntry.getCrc() != bVar3.f1702b || nextEntry.getSize() != bArrP.length || nextEntry.getSize() != bVar3.f1703c || nextEntry.getCompressedSize() != bVar3.f1704d || nextEntry.getMethod() != bVar3.f1701a) {
                                Arrays.fill(bArrP, (byte) 0);
                                throw new IOException("ZIP bundle is damaged or incomplete.");
                            }
                            if (nextEntry.isDirectory()) {
                                Arrays.fill(bArrP, (byte) 0);
                                if (nextEntry.getSize() != 0) {
                                    throw new IOException("ZIP bundle contains an invalid directory.");
                                }
                            } else {
                                map3.put(nextEntry.getName(), bArrP);
                                v(bArrP);
                                if (nextEntry.getName().toLowerCase(Locale.ROOT).endsWith(".ovpn")) {
                                    name = nextEntry.getName();
                                }
                            }
                        }
                    } catch (Throwable th3) {
                        try {
                            zipInputStream.close();
                            throw th3;
                        } catch (Throwable th4) {
                            th3.addSuppressed(th4);
                            throw th3;
                        }
                    }
                } catch (Throwable th5) {
                    Iterator it2 = map3.values().iterator();
                    while (it2.hasNext()) {
                        Arrays.fill((byte[]) it2.next(), (byte) 0);
                    }
                    throw th5;
                }
            }
        }
        throw new IOException("ZIP bundle is damaged, incomplete or split across files.");
    }

    /* JADX WARN: Code duplicated, block: B:45:0x0099 A[DONT_INVERT, EDGE_INSN: B:45:0x0099->B:51:0x00a2 BREAK  A[LOOP:0: B:37:0x0080->B:44:0x0096]] */
    /* JADX WARN: Code duplicated, block: B:46:0x009b  */
    public static int d(URI uri, int i10) {
        if (i10 == 0 || uri == null || uri.isOpaque() || uri.getHost() == null || uri.getRawFragment() != null || uri.getRawUserInfo() != null || uri.getPort() > 65535 || uri.getPort() == 0) {
            return 3;
        }
        String scheme = uri.getScheme();
        boolean zEqualsIgnoreCase = "https".equalsIgnoreCase(scheme);
        if (!zEqualsIgnoreCase && !"http".equalsIgnoreCase(scheme)) {
            return 3;
        }
        String host = uri.getHost();
        if (!"localhost".equalsIgnoreCase(host) && !"::1".equals(host) && !"[::1]".equals(host) && !"[0:0:0:0:0:0:0:1]".equals(host)) {
            String[] strArrSplit = host.split("\\.", -1);
            if (strArrSplit.length != 4) {
                if (i10 == 1) {
                    break;
                }
            } else {
                if ("127".equals(strArrSplit[0])) {
                    for (String str : strArrSplit) {
                        if (!str.matches("0|[1-9][0-9]{0,2}") || Integer.parseInt(str) > 255) {
                            if (i10 == 1) {
                                break;
                            }
                        }
                    }
                } else if (i10 == 1 && (!zEqualsIgnoreCase || i10 != 2)) {
                    return 2;
                }
            }
        }
        return 1;
    }

    public static char[] e(byte[] bArr) throws CharacterCodingException {
        CharsetDecoder charsetDecoderNewDecoder = StandardCharsets.UTF_8.newDecoder();
        CodingErrorAction codingErrorAction = CodingErrorAction.REPORT;
        CharBuffer charBufferDecode = charsetDecoderNewDecoder.onMalformedInput(codingErrorAction).onUnmappableCharacter(codingErrorAction).decode(ByteBuffer.wrap(bArr));
        char[] cArr = new char[charBufferDecode.remaining()];
        charBufferDecode.get(cArr);
        if (charBufferDecode.hasArray()) {
            Arrays.fill(charBufferDecode.array(), (char) 0);
        }
        return cArr;
    }

    public static ArrayList f(String str) throws IOException {
        ArrayList arrayList = new ArrayList();
        StringBuilder sb2 = new StringBuilder();
        int i10 = 0;
        char c5 = 0;
        boolean z2 = false;
        while (i10 < str.length()) {
            char cCharAt = str.charAt(i10);
            if (cCharAt == '\\') {
                i10++;
                if (i10 == str.length()) {
                    throw new IOException("OpenVPN bundle has an invalid quoted filename.");
                }
                sb2.append(str.charAt(i10));
            } else {
                if (c5 != 0) {
                    if (cCharAt == c5) {
                        c5 = 0;
                    } else {
                        sb2.append(cCharAt);
                    }
                } else if (cCharAt == '\'' || cCharAt == '\"') {
                    c5 = cCharAt;
                } else if (Character.isWhitespace(cCharAt)) {
                    if (z2) {
                        arrayList.add(sb2.toString());
                        sb2.setLength(0);
                        z2 = false;
                    }
                } else {
                    if ((cCharAt == '#' || cCharAt == ';') && !z2) {
                        break;
                    }
                    sb2.append(cCharAt);
                }
                i10++;
            }
            z2 = true;
            i10++;
        }
        if (c5 != 0) {
            throw new IOException("OpenVPN bundle has an invalid quoted filename.");
        }
        if (z2) {
            arrayList.add(sb2.toString());
        }
        return arrayList;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static String g(String str, String str2, HashMap map) throws IOException {
        int i10;
        String[] strArr;
        int i11;
        String str3;
        int i12;
        int i13 = 0;
        String strSubstring = str2.substring(0, str2.lastIndexOf(47) + 1);
        StringBuilder sb2 = new StringBuilder();
        HashSet hashSet = new HashSet();
        HashSet hashSet2 = new HashSet();
        String[] strArrSplit = str.split("\\r?\\n");
        int length = strArrSplit.length;
        int i14 = 0;
        int i15 = 0;
        String strF = null;
        while (true) {
            i10 = i13;
            if (i14 >= length) {
                break;
            }
            String strTrim = strArrSplit[i14].trim();
            if (strF != null) {
                if (strTrim.equalsIgnoreCase("</" + strF + ">")) {
                    strF = null;
                }
            } else if (strTrim.matches("(?i)<(ca|cert|key|tls-auth|tls-crypt|tls-crypt-v2|crl-verify|extra-certs|auth-user-pass|pkcs12|dh)>")) {
                strF = a4.b.f(1, 1, strTrim);
            } else if (strTrim.equalsIgnoreCase("<connection>")) {
                i15 = 1;
            } else if (strTrim.equalsIgnoreCase("</connection>")) {
                i15 = i10;
            } else if (i15 == 0) {
                String lowerCase = strTrim.split("\\s+", 2)[i10].toLowerCase(Locale.ROOT);
                if (lowerCase.equals("key-direction") || lowerCase.equals("--key-direction")) {
                    ArrayList arrayListF = f(strTrim);
                    if (arrayListF.size() == 2) {
                        hashSet2.add((String) arrayListF.get(1));
                    }
                }
            }
            i14++;
            i13 = i10;
        }
        String[] strArrSplit2 = str.split("\\r?\\n", -1);
        int length2 = strArrSplit2.length;
        int i16 = i10;
        String strF2 = null;
        while (i16 < length2) {
            String str4 = strArrSplit2[i16];
            String strTrim2 = str4.trim();
            if (strF2 != null) {
                sb2.append(str4);
                sb2.append('\n');
                strArr = strArrSplit2;
                str3 = strSubstring;
                i11 = length2;
                i12 = i10;
                if (strTrim2.equalsIgnoreCase("</" + strF2 + ">")) {
                    strF2 = null;
                }
            } else {
                if (strTrim2.matches("(?i)<(ca|cert|key|tls-auth|tls-crypt|tls-crypt-v2|crl-verify|extra-certs|auth-user-pass|pkcs12|dh)>")) {
                    strF2 = a4.b.f(1, 1, strTrim2);
                }
                String str5 = strTrim2.split("\\s+", 2)[i10];
                Locale locale = Locale.ROOT;
                String lowerCase2 = str5.toLowerCase(locale);
                strArr = strArrSplit2;
                if (lowerCase2.startsWith("--")) {
                    lowerCase2 = lowerCase2.substring(2);
                }
                if (lowerCase2.matches("ca|cert|key|tls-auth|tls-crypt|tls-crypt-v2|crl-verify|extra-certs")) {
                    ArrayList arrayListF2 = f(strTrim2);
                    i11 = length2;
                    if (arrayListF2.size() >= 2 && !((String) arrayListF2.get(1)).equals("[inline]")) {
                        if (hashSet.add(lowerCase2)) {
                            if (arrayListF2.size() <= (lowerCase2.equals("tls-auth") ? 3 : 2)) {
                                String str6 = (String) arrayListF2.get(1);
                                r(str6, i10);
                                byte[] bArr = (byte[]) map.get(strSubstring + str6);
                                if (bArr == null || str6.toLowerCase(locale).endsWith(".ovpn")) {
                                    throw new IOException("An OpenVPN certificate or key is missing from the ZIP bundle.");
                                }
                                if (arrayListF2.size() == 3) {
                                    String str7 = (String) arrayListF2.get(2);
                                    if (!str7.equals(SessionDescription.SUPPORTED_SDP_VERSION) && !str7.equals("1") && !str7.equals("bidirectional")) {
                                        throw new IOException("ZIP bundle has an invalid TLS key direction.");
                                    }
                                    Iterator it = hashSet2.iterator();
                                    while (it.hasNext()) {
                                        if (!((String) it.next()).equals(str7)) {
                                            throw new IOException("ZIP bundle has conflicting TLS key directions.");
                                        }
                                    }
                                    sb2.append("key-direction ");
                                    sb2.append(str7);
                                    sb2.append('\n');
                                }
                                String strV = v(bArr);
                                if (strV.matches("(?s).*<\\s*/.*")) {
                                    throw new IOException("ZIP certificate or key contains an invalid inline closing tag.");
                                }
                                int length3 = strV.length();
                                while (true) {
                                    if (length3 <= 0) {
                                        str3 = strSubstring;
                                        break;
                                    }
                                    int i17 = length3 - 1;
                                    str3 = strSubstring;
                                    if (strV.charAt(i17) != '\r' && strV.charAt(i17) != '\n') {
                                        break;
                                    }
                                    length3--;
                                    strSubstring = str3;
                                }
                                sb2.append(Typography.less);
                                sb2.append(lowerCase2);
                                sb2.append(">\n");
                                i12 = 0;
                                sb2.append((CharSequence) strV, 0, length3);
                                sb2.append('\n');
                                sb2.append("</");
                                sb2.append(lowerCase2);
                                sb2.append(">\n");
                                if (sb2.length() > 1048576) {
                                    throw new IOException("Combined OpenVPN configuration exceeds 1 MiB.");
                                }
                            }
                        }
                        throw new IOException("ZIP bundle contains an ambiguous certificate or key reference.");
                    }
                    str3 = strSubstring;
                    i12 = i10;
                    sb2.append(str4);
                    sb2.append('\n');
                } else {
                    sb2.append(str4);
                    sb2.append('\n');
                    str3 = strSubstring;
                    i11 = length2;
                    i12 = i10;
                }
            }
            i16++;
            i10 = i12;
            strArrSplit2 = strArr;
            strSubstring = str3;
            length2 = i11;
        }
        return sb2.toString();
    }

    public static int h(String str) throws IOException {
        int i10;
        int i11;
        String[] strArrSplit;
        int i12;
        int i13 = -1;
        String[] strArrSplit2 = str.split("\\r?\\n", -1);
        ArrayList arrayList = new ArrayList();
        String str2 = null;
        int i14 = 0;
        String lowerCase = null;
        boolean z2 = false;
        boolean z10 = false;
        for (int i15 = 0; i15 < strArrSplit2.length; i15++) {
            String strTrim = strArrSplit2[i15].trim();
            if (lowerCase != null) {
                if (strTrim.equalsIgnoreCase("</" + lowerCase + ">")) {
                    lowerCase = null;
                }
            } else if (!strTrim.isEmpty() && !strTrim.startsWith("#") && !strTrim.startsWith(";")) {
                if (!strTrim.startsWith("<")) {
                    arrayList.add(Integer.valueOf(i15));
                    String str3 = strTrim.split("\\s+", 2)[0];
                    if (str3.equals("fragment") || str3.equals("--fragment")) {
                        z2 = true;
                    }
                } else if (strTrim.equals("<connection>") || strTrim.equals("</connection>")) {
                    z10 = true;
                } else if (strTrim.endsWith(">") && !strTrim.startsWith("</")) {
                    lowerCase = a4.b.f(1, 1, strTrim).toLowerCase(Locale.ROOT);
                }
            }
        }
        if (!z2) {
            return -1;
        }
        ArrayList<List> arrayList2 = new ArrayList();
        Iterator it = arrayList.iterator();
        int i16 = -1;
        boolean z11 = true;
        while (true) {
            i13 = i13;
            if (!it.hasNext()) {
                if (i16 < 0) {
                    return i13;
                }
                if (z10 || !z11) {
                    throw y();
                }
                HashSet hashSet = new HashSet();
                boolean z12 = false;
                for (List list : arrayList2) {
                    String strSubstring = (String) list.get(0);
                    if (strSubstring.startsWith("--")) {
                        strSubstring = strSubstring.substring(2);
                    }
                    if (strSubstring.equals("ncp-ciphers")) {
                        strSubstring = "data-ciphers";
                    }
                    if (!hashSet.add(strSubstring) || !b(1, 1, list)) {
                        throw y();
                    }
                    boolean zEquals = strSubstring.equals("data-ciphers");
                    if (!strSubstring.equals("data-ciphers-fallback")) {
                        z12 = true;
                    }
                    if (zEquals) {
                        i10 = i13;
                        strArrSplit = ((String) list.get(1)).split(ServerSentEventKt.COLON, i10);
                        i11 = 0;
                    } else {
                        i10 = i13;
                        i11 = 0;
                        strArrSplit = new String[]{(String) list.get(1)};
                    }
                    int length = strArrSplit.length;
                    for (int i17 = i11; i17 < length; i17++) {
                        if (!strArrSplit[i17].matches("AES-" + str2 + "-(CBC|GCM)")) {
                            throw y();
                        }
                    }
                    i13 = i10;
                }
                if (z12) {
                    return i16;
                }
                throw y();
            }
            int iIntValue = ((Integer) it.next()).intValue();
            String strTrim2 = strArrSplit2[iIntValue].trim();
            List listA = a(strTrim2);
            if (!listA.isEmpty()) {
                int i18 = i14;
                String strSubstring2 = (String) listA.get(i14);
                if (strSubstring2.startsWith("--")) {
                    i12 = 2;
                    strSubstring2 = strSubstring2.substring(2);
                } else {
                    i12 = 2;
                }
                String[] strArr = strArrSplit2;
                boolean zEquals2 = strTrim2.split("\\s+", i12)[i18].equals(listA.get(i18));
                if (strSubstring2.equals("keysize")) {
                    if (!zEquals2 || i16 >= 0 || !k(listA, "128", "192", "256")) {
                        throw y();
                    }
                    str2 = (String) listA.get(1);
                    i16 = iIntValue;
                } else if (strSubstring2.equals("cipher") || strSubstring2.equals("data-ciphers") || strSubstring2.equals("ncp-ciphers") || strSubstring2.equals("data-ciphers-fallback")) {
                    arrayList2.add(listA);
                    z11 = zEquals2 & z11;
                }
                strArrSplit2 = strArr;
                i14 = 0;
            }
        }
    }

    public static char[] i(char[] cArr) throws IOException {
        if (cArr.length == 0 || cArr.length > 1048576) {
            throw y();
        }
        String str = new String(cArr);
        int iH = h(str);
        if (iH < 0) {
            return (char[]) cArr.clone();
        }
        String[] strArrSplit = str.split("\\r?\\n", -1);
        StringBuilder sb2 = new StringBuilder(str.length());
        for (int i10 = 0; i10 < strArrSplit.length; i10++) {
            if (i10 != iH) {
                if (sb2.length() > 0) {
                    sb2.append('\n');
                }
                sb2.append(strArrSplit[i10]);
            }
        }
        return sb2.toString().toCharArray();
    }

    public static boolean j(int i10, int i11, String str) {
        if (str.matches("[0-9]{1,10}")) {
            try {
                long j10 = Long.parseLong(str);
                if (j10 >= i10 && j10 <= i11) {
                    return true;
                }
            } catch (NumberFormatException unused) {
            }
        }
        return false;
    }

    public static boolean k(List list, String... strArr) {
        if (b(1, 1, list)) {
            for (String str : strArr) {
                if (((String) list.get(1)).equals(str)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static d l(String str, byte[] bArr, SecretKey secretKey) throws Throwable {
        byte[] bArr2;
        z(str);
        if (bArr == null || bArr.length < 32 || bArr.length > 1052672) {
            throw new IOException("Invalid encrypted profile size.");
        }
        int i10 = 0;
        while (true) {
            byte[] bArr3 = f1712a;
            if (i10 >= 4) {
                Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                cipher.init(2, secretKey, new GCMParameterSpec(128, bArr, 4, 12));
                cipher.updateAAD(bArr3);
                cipher.updateAAD(str.getBytes(StandardCharsets.US_ASCII));
                byte[] bArrDoFinal = cipher.doFinal(bArr, 16, bArr.length - 16);
                try {
                    DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(bArrDoFinal));
                    int unsignedByte = dataInputStream.readUnsignedByte();
                    if (unsignedByte != 1 && unsignedByte != 2) {
                        throw new IOException("Unsupported profile protocol.");
                    }
                    int i11 = dataInputStream.readInt();
                    if (i11 < 1 || i11 > 256) {
                        throw new IOException("Invalid profile name size.");
                    }
                    bArr2 = new byte[i11];
                    try {
                        dataInputStream.readFully(bArr2);
                        int i12 = dataInputStream.readInt();
                        if (i12 < 1 || i12 > 1048576 || dataInputStream.available() != i12) {
                            throw new IOException("Invalid profile content size.");
                        }
                        byte[] bArr4 = new byte[i12];
                        dataInputStream.readFully(bArr4);
                        char[] cArrE = e(bArr4);
                        try {
                            d dVar = new d(new String(e(bArr2)), cArrE, unsignedByte == 1 ? 1 : 2);
                            Arrays.fill(bArrDoFinal, (byte) 0);
                            Arrays.fill(bArr4, (byte) 0);
                            Arrays.fill(bArr2, (byte) 0);
                            return dVar;
                        } catch (IOException | RuntimeException e10) {
                            Arrays.fill(cArrE, (char) 0);
                            throw e10;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        Arrays.fill(bArrDoFinal, (byte) 0);
                        if (0 != 0) {
                            Arrays.fill((byte[]) null, (byte) 0);
                        }
                        if (bArr2 != null) {
                            Arrays.fill(bArr2, (byte) 0);
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    bArr2 = null;
                }
            } else {
                if (bArr[i10] != bArr3[i10]) {
                    throw new IOException("Unsupported encrypted profile format.");
                }
                i10++;
            }
        }
    }

    public static void m(int i10, String str) throws IOException {
        boolean z2;
        boolean z10;
        boolean z11;
        if (str.trim().isEmpty() || str.indexOf(0) >= 0) {
            throw new IOException("VPN configuration is empty or contains binary data.");
        }
        String[] strArrSplit = str.split("\\r?\\n");
        int length = strArrSplit.length;
        int i11 = 0;
        boolean z12 = false;
        boolean z13 = false;
        boolean z14 = false;
        boolean z15 = false;
        boolean z16 = false;
        int length2 = 0;
        boolean z17 = false;
        boolean z18 = false;
        boolean z19 = false;
        String str2 = null;
        while (i11 < length) {
            String str3 = strArrSplit[i11];
            String strTrim = str3.trim();
            String[] strArr = strArrSplit;
            int i12 = length;
            int i13 = i11;
            if (i10 == 1 && z13 && !strTrim.equals("</connection>") && (length2 = length2 + str3.getBytes(StandardCharsets.UTF_8).length + 1) >= 4096) {
                throw new IOException("OpenVPN connection block exceeds the engine's 4096-byte option budget. Use fewer servers per block or separate connection blocks.");
            }
            if (strTrim.isEmpty() || strTrim.startsWith("#") || strTrim.startsWith(";")) {
                z2 = z12;
                z10 = z13;
                z11 = z14;
            } else {
                z2 = z12;
                if (i10 == 2) {
                    z12 = strTrim.equalsIgnoreCase("[Interface]") ? true : z2;
                    if (strTrim.equalsIgnoreCase("[Peer]")) {
                        z14 = true;
                    }
                    String lowerCase = strTrim.split("=", 2)[0].trim().toLowerCase(Locale.ROOT);
                    if (lowerCase.equals("preup") || lowerCase.equals("postup") || lowerCase.equals("predown") || lowerCase.equals("postdown") || lowerCase.equals("saveconfig") || lowerCase.equals("table")) {
                        throw new IOException("Desktop wg-quick hooks/options are not supported on Android.");
                    }
                } else {
                    z10 = z13;
                    if (!z10) {
                        z11 = z14;
                        if (str2 != null) {
                            if (strTrim.equalsIgnoreCase("</" + str2 + ">")) {
                                z12 = z2;
                                z13 = z10;
                                z14 = z11;
                                str2 = null;
                            } else if (!str2.equals("auth-user-pass") && strTrim.startsWith("<") && strTrim.endsWith(">")) {
                                throw new IOException("Nested or mismatched OpenVPN blocks are not supported.");
                            }
                        } else if (!strTrim.startsWith("<")) {
                            String[] strArrSplit2 = strTrim.split("\\s+", 2);
                            String lowerCase2 = strArrSplit2[0].toLowerCase(Locale.ROOT);
                            if (lowerCase2.startsWith("--")) {
                                lowerCase2 = lowerCase2.substring(2);
                            }
                            String strTrim2 = strArrSplit2.length == 2 ? strArrSplit2[1].trim() : "";
                            if (lowerCase2.equals("client")) {
                                z15 = true;
                            }
                            if (lowerCase2.equals("tls-client")) {
                                z16 = true;
                            }
                            if (lowerCase2.equals("pull")) {
                                z17 = true;
                            }
                            if (lowerCase2.equals("remote") && !strTrim2.isEmpty()) {
                                z18 = true;
                            }
                            if (lowerCase2.matches("up|down|route-up|route-pre-down|ipchange|plugin|script-security|config|cd|chroot|daemon|log|log-append|writepid|status|auth-user-pass-verify|client-connect|client-disconnect|learn-address|tls-verify|iproute|tls-crypt-v2-verify|engine|providers") || lowerCase2.startsWith("management") || lowerCase2.startsWith("pkcs11")) {
                                throw new IOException("OpenVPN option \"" + q(lowerCase2) + "\" is not supported (scripts, files and management options can't be used).");
                            }
                            if (lowerCase2.matches("ca|cert|key|tls-auth|tls-crypt|tls-crypt-v2|pkcs12|extra-certs|dh|crl-verify|askpass|auth-user-pass") && !strTrim2.isEmpty() && !strTrim2.equals("[inline]")) {
                                if (lowerCase2.equals("crl-verify")) {
                                    throw new IOException("This OpenVPN profile references an external CRL (crl-verify). Import its certificate bundle or ask your provider for an inline CRL. Keep certificate revocation checks enabled.");
                                }
                                throw new IOException("Option \"" + q(lowerCase2) + "\" points to a separate file. Download a self-contained Android/OpenVPN Connect profile from your provider.");
                            }
                        } else {
                            if (!strTrim.endsWith(">")) {
                                throw new IOException("OpenVPN block tags must be on separate lines without trailing text.");
                            }
                            String lowerCase3 = a4.b.f(1, 1, strTrim).toLowerCase(Locale.ROOT);
                            if (lowerCase3.equals("connection")) {
                                length2 = 0;
                                z19 = false;
                                z12 = z2;
                                z14 = z11;
                                z13 = true;
                            } else {
                                if (!lowerCase3.matches("ca|cert|key|tls-auth|tls-crypt|tls-crypt-v2|auth-user-pass|pkcs12|extra-certs|dh|crl-verify")) {
                                    throw new IOException("OpenVPN block <" + q(lowerCase3) + "> is not supported. Use a compatible self-contained provider profile or the provider's app.");
                                }
                                str2 = lowerCase3;
                            }
                        }
                    } else {
                        if (!strTrim.equals("</connection>")) {
                            String[] strArrSplit3 = strTrim.split("\\s+");
                            String str4 = strArrSplit3[0];
                            if (str4.matches("remote|proto|port")) {
                                boolean z20 = z14;
                                if (strArrSplit3.length >= 2 && !strArrSplit3[1].startsWith("#") && !strArrSplit3[1].startsWith(";") && !strArrSplit3[1].equals("\"\"") && !strArrSplit3[1].equals("''")) {
                                    int length3 = (strArrSplit3.length * 16) + 64 + length2;
                                    if (length3 >= 4096) {
                                        throw new IOException("OpenVPN connection block exceeds the engine's 4096-byte option budget. Use fewer servers per block or separate connection blocks.");
                                    }
                                    length2 = length3;
                                    z12 = z2;
                                    z13 = z10;
                                    z14 = z20;
                                    if (str4.equals("remote")) {
                                        z19 = true;
                                    }
                                }
                            }
                            throw new IOException("OpenVPN connection blocks support only remote, proto and port options. Move other settings outside the block or use your provider's app.");
                        }
                        if (!z19) {
                            throw new IOException("Each OpenVPN connection block must contain a remote server.");
                        }
                        z13 = false;
                        z12 = z2;
                        z18 = true;
                    }
                }
                i11 = i13 + 1;
                strArrSplit = strArr;
                length = i12;
            }
            z12 = z2;
            z13 = z10;
            z14 = z11;
            i11 = i13 + 1;
            strArrSplit = strArr;
            length = i12;
        }
        boolean z21 = z12;
        boolean z22 = z13;
        boolean z23 = z14;
        if (i10 == 2 && (!z21 || !z23)) {
            throw new IOException("Expected a WireGuard configuration with Interface and Peer sections.");
        }
        if (i10 == 1) {
            if ((!z15 && (!z16 || !z17)) || !z18 || str2 != null || z22) {
                throw new IOException("Expected a self-contained OpenVPN client configuration with a remote server.");
            }
        }
    }

    public static f n(InputStream inputStream, int i10) {
        if (i10 == 0) {
            throw new IllegalArgumentException("Input and protocol are required.");
        }
        byte[] bArrP = p(inputStream, 1048576);
        try {
            if (bArrP.length < 2 || bArrP[0] != 80 || bArrP[1] != 75) {
                f fVarO = o(new ByteArrayInputStream(bArrP), i10);
                Arrays.fill(bArrP, (byte) 0);
                return fVarO;
            }
            if (i10 != 1) {
                throw new IOException("ZIP bundles are supported only for OpenVPN.");
            }
            f fVarC = c(bArrP);
            Arrays.fill(bArrP, (byte) 0);
            return fVarC;
        } catch (Throwable th2) {
            Arrays.fill(bArrP, (byte) 0);
            throw th2;
        }
    }

    public static f o(InputStream inputStream, int i10) {
        if (i10 == 0) {
            throw new IllegalArgumentException("Input and protocol are required.");
        }
        byte[] bArr = new byte[4096];
        c cVar = new c(2);
        while (true) {
            try {
                int i11 = inputStream.read(bArr);
                if (i11 == -1) {
                    byte[] byteArray = cVar.toByteArray();
                    try {
                        CharsetDecoder charsetDecoderNewDecoder = StandardCharsets.UTF_8.newDecoder();
                        CodingErrorAction codingErrorAction = CodingErrorAction.REPORT;
                        String string = charsetDecoderNewDecoder.onMalformedInput(codingErrorAction).onUnmappableCharacter(codingErrorAction).decode(ByteBuffer.wrap(byteArray)).toString();
                        if (string.startsWith("\ufeff")) {
                            string = string.substring(1);
                        }
                        if (string.trim().isEmpty() || string.indexOf(0) >= 0) {
                            throw new IOException("VPN configuration is empty or contains binary data.");
                        }
                        m(i10, string);
                        if (i10 == 1) {
                            u(string);
                        }
                        f fVar = new f(i10, string.toCharArray());
                        Arrays.fill(bArr, (byte) 0);
                        cVar.E();
                        if (byteArray != null) {
                            Arrays.fill(byteArray, (byte) 0);
                        }
                        return fVar;
                    } catch (CharacterCodingException unused) {
                        throw new IOException("VPN configuration must be UTF-8 text.");
                    }
                }
                if (i11 == 0) {
                    throw new IOException("Could not read VPN configuration.");
                }
                if (cVar.size() > 1048576 - i11) {
                    throw new IOException("VPN configuration exceeds 1 MiB.");
                }
                cVar.write(bArr, 0, i11);
            } catch (Throwable th2) {
                Arrays.fill(bArr, (byte) 0);
                cVar.E();
                if (0 != 0) {
                    Arrays.fill((byte[]) null, (byte) 0);
                }
                throw th2;
            }
        }
    }

    public static byte[] p(InputStream inputStream, int i10) {
        c cVar = new c(0);
        byte[] bArr = new byte[4096];
        while (true) {
            try {
                int i11 = inputStream.read(bArr);
                if (i11 == -1) {
                    byte[] byteArray = cVar.toByteArray();
                    cVar.E();
                    Arrays.fill(bArr, (byte) 0);
                    return byteArray;
                }
                if (i11 == 0) {
                    throw new IOException("Could not read VPN document.");
                }
                if (i11 > i10 - cVar.size()) {
                    throw new IOException("VPN document or decompressed ZIP bundle exceeds 1 MiB.");
                }
                cVar.write(bArr, 0, i11);
            } catch (Throwable th2) {
                cVar.E();
                Arrays.fill(bArr, (byte) 0);
                throw th2;
            }
        }
    }

    public static String q(String str) {
        String strReplaceAll = str.replaceAll("[^a-z0-9_-]", "?");
        return strReplaceAll.length() > 30 ? strReplaceAll.substring(0, 30) : strReplaceAll;
    }

    public static void r(String str, boolean z2) throws IOException {
        if (str.isEmpty() || str.length() > 1024 || str.startsWith("/") || str.indexOf(92) >= 0 || str.indexOf(58) >= 0) {
            throw new IOException("ZIP bundle contains an unsafe path.");
        }
        for (String str2 : (z2 ? a4.b.f(1, 0, str) : str).split("/", -1)) {
            if (str2.isEmpty() || str2.equals(".") || str2.equals("..")) {
                throw new IOException("ZIP bundle contains an unsafe path.");
            }
        }
        for (int i10 = 0; i10 < str.length(); i10++) {
            if (Character.isISOControl(str.charAt(i10))) {
                throw new IOException("ZIP bundle contains an unsafe path.");
            }
        }
    }

    public static byte[] s(String str, String str2, f fVar, SecretKey secretKey) throws Throwable {
        z(str);
        if (str2.trim().isEmpty()) {
            throw new IOException("A profile name is required.");
        }
        String strTrim = str2.trim();
        Charset charset = StandardCharsets.UTF_8;
        byte[] bytes = strTrim.getBytes(charset);
        if (bytes.length > 256) {
            throw new IOException("Profile name is too long.");
        }
        char[] cArrG = fVar.g();
        c cVar = new c(1);
        byte[] bArr = null;
        try {
            ByteBuffer byteBufferEncode = charset.newEncoder().encode(CharBuffer.wrap(cArrG));
            int iRemaining = byteBufferEncode.remaining();
            byte[] bArr2 = new byte[iRemaining];
            try {
                byteBufferEncode.get(bArr2);
                if (byteBufferEncode.hasArray()) {
                    Arrays.fill(byteBufferEncode.array(), (byte) 0);
                }
                if (iRemaining == 0 || iRemaining > 1048576) {
                    throw new IOException("Invalid profile size.");
                }
                DataOutputStream dataOutputStream = new DataOutputStream(cVar);
                dataOutputStream.writeByte(fVar.f1713b == 1 ? 1 : 2);
                dataOutputStream.writeInt(bytes.length);
                dataOutputStream.write(bytes);
                dataOutputStream.writeInt(iRemaining);
                dataOutputStream.write(bArr2);
                dataOutputStream.flush();
                byte[] byteArray = cVar.toByteArray();
                Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                cipher.init(1, secretKey);
                byte[] iv = cipher.getIV();
                if (iv == null || iv.length != 12) {
                    throw new GeneralSecurityException("Unsupported encryption provider.");
                }
                byte[] bArr3 = f1712a;
                cipher.updateAAD(bArr3);
                cipher.updateAAD(str.getBytes(StandardCharsets.US_ASCII));
                byte[] bArrDoFinal = cipher.doFinal(byteArray);
                byte[] bArr4 = new byte[bArrDoFinal.length + 16];
                System.arraycopy(bArr3, 0, bArr4, 0, 4);
                System.arraycopy(iv, 0, bArr4, 4, 12);
                System.arraycopy(bArrDoFinal, 0, bArr4, 16, bArrDoFinal.length);
                Arrays.fill(cArrG, (char) 0);
                Arrays.fill(bytes, (byte) 0);
                Arrays.fill(bArr2, (byte) 0);
                if (byteArray != null) {
                    Arrays.fill(byteArray, (byte) 0);
                }
                cVar.E();
                return bArr4;
            } catch (Throwable th2) {
                th = th2;
                bArr = bArr2;
                Arrays.fill(cArrG, (char) 0);
                Arrays.fill(bytes, (byte) 0);
                if (bArr != null) {
                    Arrays.fill(bArr, (byte) 0);
                }
                if (bArr != 0) {
                    Arrays.fill((byte[]) 0, (byte) 0);
                }
                cVar.E();
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
        }
    }

    public static int t(char[] cArr) throws IOException {
        if (cArr.length == 0 || cArr.length > 1048576) {
            throw new IOException("OpenVPN configuration is empty or exceeds its size limit.");
        }
        String str = new String(cArr);
        if (str.startsWith("\ufeff")) {
            str = str.substring(1);
        }
        m(1, str);
        return u(str);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:376:0x055b  */
    /* JADX WARN: Code duplicated, block: B:382:0x0573  */
    /* JADX WARN: Code duplicated, block: B:598:0x0969 A[PHI: r5
  0x0969: PHI (r5v8 int) = (r5v6 int), (r5v6 int), (r5v10 int) binds: [B:606:0x0986, B:608:0x0994, B:597:0x0967] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:77:0x0158  */
    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v176 */
    /* JADX WARN: Type inference failed for: r3v177 */
    /* JADX WARN: Type inference failed for: r3v195 */
    /* JADX WARN: Type inference failed for: r3v347 */
    /* JADX WARN: Type inference failed for: r3v361 */
    public static int u(String str) throws IOException {
        byte b5;
        ?? B;
        int i10;
        boolean zK;
        ArrayList<String> arrayList = new ArrayList();
        int i11 = 0;
        String lowerCase = null;
        boolean z2 = false;
        for (String str2 : str.split("\\r?\\n")) {
            String strTrim = str2.trim();
            if (lowerCase != null) {
                if (strTrim.equalsIgnoreCase("</" + lowerCase + ">")) {
                    lowerCase = null;
                }
            } else if (!strTrim.isEmpty() && !strTrim.startsWith("#") && !strTrim.startsWith(";")) {
                if (!strTrim.startsWith("<")) {
                    arrayList.add(strTrim);
                    String str3 = strTrim.split("\\s+", 2)[0];
                    if (str3.equals("fragment") || str3.equals("--fragment")) {
                        z2 = true;
                    }
                } else if (!strTrim.equals("<connection>") && !strTrim.equals("</connection>")) {
                    lowerCase = a4.b.f(1, 1, strTrim).toLowerCase(Locale.ROOT);
                    arrayList.add(strTrim);
                }
            }
        }
        if (!z2) {
            return 1;
        }
        h(str);
        int i12 = 0;
        int i13 = 0;
        for (String str4 : arrayList) {
            if (!str4.startsWith("<")) {
                List listA = a(str4);
                if (listA.isEmpty() || !str4.split("\\s+", 2)[i11].equals(listA.get(i11))) {
                    throw y();
                }
                String strSubstring = (String) listA.get(i11);
                if (strSubstring.startsWith("--")) {
                    strSubstring = strSubstring.substring(2);
                }
                if (!strSubstring.matches("[a-z][a-z0-9-]*")) {
                    throw y();
                }
                if (strSubstring.equals("ns-cert-type") && (i12 = i12 + 1) != 1) {
                    throw y();
                }
                if (strSubstring.equals("fragment")) {
                    i13++;
                    if (i13 != 1 || !b(1, 2, listA) || !j(68, 65535, (String) listA.get(1)) || (listA.size() == 3 && !((String) listA.get(2)).equals("mtu"))) {
                        throw new IOException("OpenVPN fragmentation requires one fragment size from 68 to 65535, optionally followed by mtu.");
                    }
                } else if (!strSubstring.equals("keysize")) {
                    switch (strSubstring.hashCode()) {
                        case -2014892514:
                            if (strSubstring.equals("verify-x509-name")) {
                                b5 = 0;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -1835241292:
                            if (strSubstring.equals("ping-restart")) {
                                b5 = 1;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -1833562386:
                            if (strSubstring.equals("auth-user-pass")) {
                                b5 = 2;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -1745954712:
                            if (strSubstring.equals("keepalive")) {
                                b5 = 3;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -1535726788:
                            if (strSubstring.equals("auth-nocache")) {
                                b5 = 4;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -1469698294:
                            if (strSubstring.equals("data-ciphers-fallback")) {
                                b5 = 5;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -1458291427:
                            if (strSubstring.equals("tls-version-max")) {
                                b5 = 6;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -1458291189:
                            if (strSubstring.equals("tls-version-min")) {
                                b5 = 7;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -1451980851:
                            if (strSubstring.equals("tls-crypt-v2")) {
                                b5 = 8;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -1360271861:
                            if (strSubstring.equals("cipher")) {
                                b5 = 9;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -1357712437:
                            if (strSubstring.equals("client")) {
                                b5 = 10;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -1295203610:
                            if (strSubstring.equals("persist-key")) {
                                b5 = ServerMessageBlock.SMB_COM_WRITE;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -1295194476:
                            if (strSubstring.equals("persist-tun")) {
                                b5 = Smb2QueryDirectoryRequest.FILE_NAMES_INFO;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -1168207500:
                            if (strSubstring.equals("resolv-retry")) {
                                b5 = 13;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -1134074715:
                            if (strSubstring.equals("data-ciphers")) {
                                b5 = 14;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -1077170121:
                            if (strSubstring.equals("fast-io")) {
                                b5 = 15;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -1064657560:
                            if (strSubstring.equals("mssfix")) {
                                b5 = 16;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -1064087795:
                            if (strSubstring.equals("remote-cert-eku")) {
                                b5 = 17;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -1064073351:
                            if (strSubstring.equals("remote-cert-tls")) {
                                b5 = ServerMessageBlock.SMB_COM_SEEK;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -1044259498:
                            if (strSubstring.equals("ncp-ciphers")) {
                                b5 = 19;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -1040225922:
                            if (strSubstring.equals("nobind")) {
                                b5 = FileInformation.FILE_ENDOFFILE_INFO;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -971108370:
                            if (strSubstring.equals("tun-mtu")) {
                                b5 = 21;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -936202258:
                            if (strSubstring.equals("rcvbuf")) {
                                b5 = 22;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -934610874:
                            if (strSubstring.equals("remote")) {
                                b5 = 23;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -905782965:
                            if (strSubstring.equals("setenv")) {
                                b5 = 24;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -897950614:
                            if (strSubstring.equals("sndbuf")) {
                                b5 = 25;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -881779909:
                            if (strSubstring.equals("ns-cert-type")) {
                                b5 = 26;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -804545494:
                            if (strSubstring.equals("remote-random")) {
                                b5 = 27;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -780444271:
                            if (strSubstring.equals("key-direction")) {
                                b5 = 28;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -745660546:
                            if (strSubstring.equals("connect-timeout")) {
                                b5 = 29;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -655551259:
                            if (strSubstring.equals("route-method")) {
                                b5 = 30;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -601315101:
                            if (strSubstring.equals("comp-lzo")) {
                                b5 = 31;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -599266462:
                            if (strSubstring.equals("compress")) {
                                b5 = 32;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -474902755:
                            if (strSubstring.equals("tls-cipher")) {
                                b5 = 33;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -472343331:
                            if (strSubstring.equals("tls-client")) {
                                b5 = 34;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -425299815:
                            if (strSubstring.equals("ping-exit")) {
                                b5 = SmbComTransaction.TRANS_PEEK_NAMED_PIPE;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -379754596:
                            if (strSubstring.equals("connect-retry-max")) {
                                b5 = 36;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -233147447:
                            if (strSubstring.equals("mute-replay-warnings")) {
                                b5 = ServerMessageBlock.SMB_COM_TRANSACTION;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -175628593:
                            if (strSubstring.equals("setenv-safe")) {
                                b5 = 38;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -82227941:
                            if (strSubstring.equals("reneg-bytes")) {
                                b5 = 39;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -17529517:
                            if (strSubstring.equals("redirect-gateway")) {
                                b5 = 40;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case -16871976:
                            if (strSubstring.equals("tls-ciphersuites")) {
                                b5 = 41;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case 3166:
                            if (strSubstring.equals("ca")) {
                                b5 = ServerMessageBlock.SMB_COM_MOVE;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case 99349:
                            if (strSubstring.equals("dev")) {
                                b5 = ServerMessageBlock.SMB_COM_ECHO;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case 106079:
                            if (strSubstring.equals("key")) {
                                b5 = 44;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case 3005864:
                            if (strSubstring.equals("auth")) {
                                b5 = ServerMessageBlock.SMB_COM_OPEN_ANDX;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case 3050020:
                            if (strSubstring.equals("cert")) {
                                b5 = ServerMessageBlock.SMB_COM_READ_ANDX;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case 3363353:
                            if (strSubstring.equals(SponsorSegment.ACTION_MUTE)) {
                                b5 = ServerMessageBlock.SMB_COM_WRITE_ANDX;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case 3441010:
                            if (strSubstring.equals("ping")) {
                                b5 = 48;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case 3446913:
                            if (strSubstring.equals("port")) {
                                b5 = 49;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case 3452485:
                            if (strSubstring.equals("pull")) {
                                b5 = ServerMessageBlock.SMB_COM_TRANSACTION2;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case 3616031:
                            if (strSubstring.equals("verb")) {
                                b5 = 51;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case 97526364:
                            if (strSubstring.equals("float")) {
                                b5 = ServerMessageBlock.SMB_COM_FIND_CLOSE2;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case 106940904:
                            if (strSubstring.equals("proto")) {
                                b5 = 53;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case 108728307:
                            if (strSubstring.equals("rport")) {
                                b5 = 54;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case 261691090:
                            if (strSubstring.equals("extra-certs")) {
                                b5 = 55;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case 362727055:
                            if (strSubstring.equals("tls-timeout")) {
                                b5 = 56;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case 400599580:
                            if (strSubstring.equals("tls-crypt")) {
                                b5 = 57;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case 519864108:
                            if (strSubstring.equals("remote-cert-ku")) {
                                b5 = 58;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case 538863842:
                            if (strSubstring.equals("allow-compression")) {
                                b5 = 59;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case 705602362:
                            if (strSubstring.equals("tls-auth")) {
                                b5 = 60;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case 713313603:
                            if (strSubstring.equals("comp-noadapt")) {
                                b5 = Base64.padSymbol;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case 780729554:
                            if (strSubstring.equals("tran-window")) {
                                b5 = 62;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case 1054306802:
                            if (strSubstring.equals("dev-type")) {
                                b5 = Utf8.REPLACEMENT_BYTE;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case 1078912255:
                            if (strSubstring.equals("route-delay")) {
                                b5 = 64;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case 1080642001:
                            if (strSubstring.equals("dhcp-option")) {
                                b5 = 65;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case 1213641629:
                            if (strSubstring.equals("server-poll-timeout")) {
                                b5 = 66;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case 1504982207:
                            if (strSubstring.equals("explicit-exit-notify")) {
                                b5 = 67;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case 1836584553:
                            if (strSubstring.equals("crl-verify")) {
                                b5 = 68;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case 1868084481:
                            if (strSubstring.equals("reneg-sec")) {
                                b5 = 69;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case 1939733262:
                            if (strSubstring.equals("hand-window")) {
                                b5 = 70;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case 2066536453:
                            if (strSubstring.equals("connect-retry")) {
                                b5 = 71;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case 2075961098:
                            if (strSubstring.equals("reneg-pkts")) {
                                b5 = 72;
                            } else {
                                b5 = -1;
                            }
                            break;
                        case 2121427193:
                            if (strSubstring.equals("peer-fingerprint")) {
                                b5 = 73;
                            } else {
                                b5 = -1;
                            }
                            break;
                        default:
                            b5 = -1;
                            break;
                    }
                    switch (b5) {
                        case 0:
                            i11 = 0;
                            if (!b(1, 2, listA) || ((String) listA.get(1)).isEmpty() || (listA.size() >= 3 && !((String) listA.get(2)).matches("subject|name|name-prefix"))) {
                                B = i11;
                            } else {
                                B = 1;
                            }
                            break;
                        case 1:
                        case 35:
                        case NalUnitUtil.H265_NAL_UNIT_TYPE_PREFIX_SEI /* 39 */:
                        case NalUnitUtil.H265_NAL_UNIT_TYPE_UNSPECIFIED /* 48 */:
                        case 69:
                        case 72:
                            if (b(1, 1, listA)) {
                                i11 = 0;
                                if (j(0, Integer.MAX_VALUE, (String) listA.get(1))) {
                                    B = 1;
                                }
                            } else {
                                i11 = 0;
                            }
                            B = i11;
                            break;
                        case 2:
                            if (b(0, 1, listA) && (listA.size() == 1 || ((String) listA.get(1)).equals("[inline]"))) {
                                zK = true;
                            } else {
                                zK = false;
                            }
                            i11 = 0;
                            B = zK;
                            break;
                        case 3:
                            if (b(2, 2, listA) && j(1, Integer.MAX_VALUE, (String) listA.get(1)) && j(1, Integer.MAX_VALUE, (String) listA.get(2))) {
                                zK = true;
                            } else {
                                zK = false;
                            }
                            i11 = 0;
                            B = zK;
                            break;
                        case 4:
                        case 10:
                        case 11:
                        case 12:
                        case 15:
                        case 20:
                        case 27:
                        case 34:
                        case 37:
                        case 50:
                        case 52:
                        case LockFreeTaskQueueCore.CLOSED_SHIFT /* 61 */:
                            i11 = 0;
                            B = b(0, 0, listA);
                            break;
                        case 5:
                        case 9:
                            if (b(1, 1, listA) && ((String) listA.get(1)).matches("AES-(128|192|256)-(CBC|GCM)|CHACHA20-POLY1305")) {
                                zK = true;
                            } else {
                                zK = false;
                            }
                            i11 = 0;
                            B = zK;
                            break;
                        case 6:
                        case 7:
                            zK = k(listA, "1.2", "1.3");
                            i11 = 0;
                            B = zK;
                            break;
                        case 8:
                        case AacUtil.AUDIO_OBJECT_TYPE_AAC_XHE /* 42 */:
                        case 44:
                        case 46:
                        case 55:
                        case 57:
                        case 60:
                        case 68:
                            zK = k(listA, "[inline]");
                            i11 = 0;
                            B = zK;
                            break;
                        case 13:
                            if (b(1, 1, listA) && (((String) listA.get(1)).equals("infinite") || j(0, Integer.MAX_VALUE, (String) listA.get(1)))) {
                                zK = true;
                            } else {
                                zK = false;
                            }
                            i11 = 0;
                            B = zK;
                            break;
                        case 14:
                        case 19:
                            if (b(1, 1, listA)) {
                                String[] strArrSplit = ((String) listA.get(1)).split(ServerSentEventKt.COLON, -1);
                                int length = strArrSplit.length;
                                int i14 = 0;
                                while (true) {
                                    if (i14 >= length) {
                                        zK = true;
                                        i11 = 0;
                                        B = zK;
                                        break;
                                    } else if (strArrSplit[i14].matches("AES-(128|192|256)-(CBC|GCM)|CHACHA20-POLY1305")) {
                                        i14++;
                                    }
                                }
                            }
                            zK = false;
                            i11 = 0;
                            B = zK;
                            break;
                        case 16:
                            if (b(0, 2, listA) && ((listA.size() == 1 || j(0, 65535, (String) listA.get(1))) && (listA.size() < 3 || ((String) listA.get(2)).equals("mtu") || ((String) listA.get(2)).equals("fixed")))) {
                                zK = true;
                            } else {
                                zK = false;
                            }
                            i11 = 0;
                            B = zK;
                            break;
                        case 17:
                            if (!b(1, 1, listA) || ((String) listA.get(1)).isEmpty()) {
                                zK = false;
                            } else {
                                zK = true;
                            }
                            i11 = 0;
                            B = zK;
                            break;
                        case 18:
                            zK = k(listA, "server");
                            i11 = 0;
                            B = zK;
                            break;
                        case 21:
                            if (b(1, 1, listA) && j(576, 65535, (String) listA.get(1))) {
                                zK = true;
                            } else {
                                zK = false;
                            }
                            i11 = 0;
                            B = zK;
                            break;
                        case 22:
                        case 25:
                            if (b(1, 1, listA) && j(0, Integer.MAX_VALUE, (String) listA.get(1))) {
                                zK = true;
                            } else {
                                zK = false;
                            }
                            i11 = 0;
                            B = zK;
                            break;
                        case 23:
                            if (!b(1, 3, listA) || ((String) listA.get(1)).isEmpty() || ((listA.size() >= 3 && !j(1, 65535, (String) listA.get(2))) || (listA.size() >= 4 && !((String) listA.get(3)).matches("udp|udp4|udp6")))) {
                                zK = false;
                            } else {
                                zK = true;
                            }
                            i11 = 0;
                            B = zK;
                            break;
                        case 24:
                            if (b(2, 2, listA) && ((String) listA.get(1)).equals("CLIENT_CERT") && ((String) listA.get(2)).matches("0|1")) {
                                zK = true;
                            } else {
                                zK = false;
                            }
                            i11 = 0;
                            B = zK;
                            break;
                        case 26:
                            zK = k(listA, "server");
                            i11 = 0;
                            B = zK;
                            break;
                        case 28:
                            zK = k(listA, SessionDescription.SUPPORTED_SDP_VERSION, "1", "bidirectional");
                            i11 = 0;
                            B = zK;
                            break;
                        case 29:
                        case 56:
                        case 62:
                        case 66:
                        case MPV.mpvLogLevel.MPV_LOG_LEVEL_TRACE /* 70 */:
                            if (b(1, 1, listA) && j(1, 3600, (String) listA.get(1))) {
                                zK = true;
                            } else {
                                zK = false;
                            }
                            i11 = 0;
                            B = zK;
                            break;
                        case 30:
                            zK = k(listA, "exe", "ipapi", "adaptive");
                            i11 = 0;
                            B = zK;
                            break;
                        case 31:
                            if (b(0, 1, listA) && (listA.size() == 1 || ((String) listA.get(1)).matches("no|yes|adaptive"))) {
                                zK = true;
                            } else {
                                zK = false;
                            }
                            i11 = 0;
                            B = zK;
                            break;
                        case 32:
                            if (b(0, 1, listA) && (listA.size() == 1 || ((String) listA.get(1)).matches("stub|stub-v2|lzo|lz4|lz4-v2"))) {
                                zK = true;
                            } else {
                                zK = false;
                            }
                            i11 = 0;
                            B = zK;
                            break;
                        case 33:
                        case 41:
                            if (b(1, 1, listA)) {
                                String[] strArrSplit2 = ((String) listA.get(1)).split(ServerSentEventKt.COLON, -1);
                                int length2 = strArrSplit2.length;
                                int i15 = 0;
                                while (true) {
                                    if (i15 >= length2) {
                                        zK = true;
                                        i11 = 0;
                                        B = zK;
                                        break;
                                    } else {
                                        String str5 = strArrSplit2[i15];
                                        if (str5.matches("(?:TLS[-_]|ECDHE-|DHE-)[A-Z0-9_-]{1,120}") && str5.matches(".*(?:AES|CHACHA20).*") && !str5.matches(".*(NULL|RC4|DES|MD5|EXPORT|PSK|ANON).*")) {
                                            i15++;
                                        }
                                    }
                                }
                            }
                            zK = false;
                            i11 = 0;
                            B = zK;
                            break;
                        case TsExtractor.TS_STREAM_TYPE_H265 /* 36 */:
                            if (b(1, 1, listA) && j(0, Integer.MAX_VALUE, (String) listA.get(1))) {
                                zK = true;
                            } else {
                                zK = false;
                            }
                            i11 = 0;
                            B = zK;
                            break;
                        case FlacConstants.STREAM_INFO_BLOCK_SIZE /* 38 */:
                            if (b(1, 2, listA) && ((String) listA.get(1)).matches("UV_[A-Za-z0-9_]{1,64}")) {
                                zK = true;
                            } else {
                                zK = false;
                            }
                            i11 = 0;
                            B = zK;
                            break;
                        case 40:
                            if (b(0, 2, listA)) {
                                int i16 = 1;
                                while (true) {
                                    if (i16 >= listA.size()) {
                                        zK = true;
                                        i11 = 0;
                                        B = zK;
                                        break;
                                    } else if (((String) listA.get(i16)).matches("def1|ipv6")) {
                                        i16++;
                                    }
                                }
                            }
                            zK = false;
                            i11 = 0;
                            B = zK;
                            break;
                        case 43:
                        case 63:
                            zK = k(listA, "tun");
                            i11 = 0;
                            B = zK;
                            break;
                        case TsExtractor.TS_STREAM_TYPE_MHAS /* 45 */:
                            zK = k(listA, "SHA1", "SHA256", "SHA384", "SHA512");
                            i11 = 0;
                            B = zK;
                            break;
                        case 47:
                            if (b(1, 1, listA) && j(0, Integer.MAX_VALUE, (String) listA.get(1))) {
                                zK = true;
                            } else {
                                zK = false;
                            }
                            i11 = 0;
                            B = zK;
                            break;
                        case 49:
                        case 54:
                            if (b(1, 1, listA) && j(1, 65535, (String) listA.get(1))) {
                                zK = true;
                            } else {
                                zK = false;
                            }
                            i11 = 0;
                            B = zK;
                            break;
                        case 51:
                            if (b(1, 1, listA) && j(0, 4, (String) listA.get(1))) {
                                zK = true;
                            } else {
                                zK = false;
                            }
                            i11 = 0;
                            B = zK;
                            break;
                        case FastDoubleMath.DOUBLE_SIGNIFICAND_WIDTH /* 53 */:
                            if (b(1, 1, listA) && ((String) listA.get(1)).matches("udp|udp4|udp6")) {
                                zK = true;
                            } else {
                                zK = false;
                            }
                            i11 = 0;
                            B = zK;
                            break;
                        case 58:
                            if (b(1, 16, listA)) {
                                int i17 = 1;
                                while (true) {
                                    if (i17 >= listA.size()) {
                                        zK = true;
                                        i11 = 0;
                                        B = zK;
                                        break;
                                    } else if (((String) listA.get(i17)).matches("[0-9A-Fa-f]{1,4}")) {
                                        i17++;
                                    }
                                }
                            }
                            zK = false;
                            i11 = 0;
                            B = zK;
                            break;
                        case 59:
                            zK = k(listA, "no", "asym");
                            i11 = 0;
                            B = zK;
                            break;
                        case 64:
                            if (b(0, 2, listA)) {
                                if (listA.size() != 1) {
                                    i10 = 3600;
                                    if (j(0, 3600, (String) listA.get(1))) {
                                    }
                                } else {
                                    i10 = 3600;
                                }
                                if (listA.size() < 3 || j(0, i10, (String) listA.get(2))) {
                                    zK = true;
                                }
                                i11 = 0;
                                B = zK;
                            }
                            zK = false;
                            i11 = 0;
                            B = zK;
                            break;
                        case 65:
                            if (b(2, 2, listA) && ((String) listA.get(1)).matches("DNS|DNS6") && ((String) listA.get(2)).matches("[0-9A-Fa-f:.]{2,45}")) {
                                zK = true;
                            } else {
                                zK = false;
                            }
                            i11 = 0;
                            B = zK;
                            break;
                        case MdtaMetadataEntry.TYPE_INDICATOR_INT32 /* 67 */:
                            if (b(0, 1, listA) && (listA.size() == 1 || j(0, 255, (String) listA.get(1)))) {
                                zK = true;
                            } else {
                                zK = false;
                            }
                            i11 = 0;
                            B = zK;
                            break;
                        case 71:
                            if (b(1, 2, listA) && j(1, 3600, (String) listA.get(1)) && (listA.size() < 3 || j(1, 3600, (String) listA.get(2)))) {
                                zK = true;
                            } else {
                                zK = false;
                            }
                            i11 = 0;
                            B = zK;
                            break;
                        case 73:
                            if (b(1, 1, listA) && ((String) listA.get(1)).matches("[0-9A-Fa-f]{2}(?::[0-9A-Fa-f]{2}){31}")) {
                                zK = true;
                            } else {
                                zK = false;
                            }
                            i11 = 0;
                            B = zK;
                            break;
                        default:
                            zK = false;
                            i11 = 0;
                            B = zK;
                            break;
                    }
                    if (B == 0) {
                        throw y();
                    }
                }
                i11 = 0;
            } else if (!str4.matches("<(ca|cert|key|tls-auth|tls-crypt|tls-crypt-v2|auth-user-pass|extra-certs|crl-verify)>")) {
                throw y();
            }
        }
        return 2;
    }

    public static String v(byte[] bArr) throws IOException {
        try {
            CharsetDecoder charsetDecoderNewDecoder = StandardCharsets.UTF_8.newDecoder();
            CodingErrorAction codingErrorAction = CodingErrorAction.REPORT;
            String string = charsetDecoderNewDecoder.onMalformedInput(codingErrorAction).onUnmappableCharacter(codingErrorAction).decode(ByteBuffer.wrap(bArr)).toString();
            if (string.startsWith("\ufeff")) {
                string = string.substring(1);
            }
            if (string.trim().isEmpty() || string.indexOf(0) >= 0) {
                throw new IOException("OpenVPN bundle files must contain UTF-8 text.");
            }
            return string;
        } catch (CharacterCodingException unused) {
            throw new IOException("OpenVPN bundle files must contain UTF-8 text.");
        }
    }

    public static int w(int i10, byte[] bArr) {
        return ((bArr[i10 + 1] & 255) << 8) | (bArr[i10] & 255);
    }

    public static long x(int i10, byte[] bArr) {
        return (((long) w(i10 + 2, bArr)) << 16) | (((long) w(i10, bArr)) & WebSocketProtocol.PAYLOAD_SHORT_MAX);
    }

    public static IOException y() {
        return new IOException("OpenVPN 2 compatibility mode cannot safely support one or more options in this profile. Use a supported provider profile or the provider's app.");
    }

    public static void z(String str) {
        if (str == null || !str.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")) {
            throw new IllegalArgumentException("Invalid profile identifier.");
        }
    }
}
