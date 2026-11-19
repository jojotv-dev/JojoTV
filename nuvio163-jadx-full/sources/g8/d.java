package g8;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes4.dex */
public final class d {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final ArrayList f18826c = new ArrayList();

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final CharsetEncoder[] f18827a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f18828b;

    static {
        String[] strArr = {"IBM437", "ISO-8859-2", "ISO-8859-3", "ISO-8859-4", "ISO-8859-5", "ISO-8859-6", "ISO-8859-7", "ISO-8859-8", "ISO-8859-9", "ISO-8859-10", "ISO-8859-11", "ISO-8859-13", "ISO-8859-14", "ISO-8859-15", "ISO-8859-16", "windows-1250", "windows-1251", "windows-1252", "windows-1256", "Shift_JIS"};
        for (int i10 = 0; i10 < 20; i10++) {
            String str = strArr[i10];
            if (((c) c.f18822i.get(str)) != null) {
                try {
                    f18826c.add(Charset.forName(str).newEncoder());
                } catch (UnsupportedCharsetException unused) {
                }
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public d(String str, Charset charset) {
        int i10;
        boolean z2;
        ArrayList arrayList = new ArrayList();
        arrayList.add(StandardCharsets.ISO_8859_1.newEncoder());
        int i11 = 0;
        boolean z10 = charset != null && charset.name().startsWith("UTF");
        int i12 = 0;
        while (true) {
            i10 = -1;
            if (i12 >= str.length()) {
                break;
            }
            Iterator it = arrayList.iterator();
            while (true) {
                if (!it.hasNext()) {
                    z2 = false;
                    break;
                }
                CharsetEncoder charsetEncoder = (CharsetEncoder) it.next();
                char cCharAt = str.charAt(i12);
                if (cCharAt == -1 || charsetEncoder.canEncode(cCharAt)) {
                    z2 = true;
                    break;
                }
            }
            if (!z2) {
                for (CharsetEncoder charsetEncoder2 : f18826c) {
                    if (charsetEncoder2.canEncode(str.charAt(i12))) {
                        arrayList.add(charsetEncoder2);
                        z2 = true;
                        break;
                    }
                }
            }
            if (!z2) {
                z10 = true;
            }
            i12++;
        }
        if (arrayList.size() != 1 || z10) {
            this.f18827a = new CharsetEncoder[arrayList.size() + 2];
            Iterator it2 = arrayList.iterator();
            int i13 = 0;
            while (it2.hasNext()) {
                this.f18827a[i13] = (CharsetEncoder) it2.next();
                i13++;
            }
            this.f18827a[i13] = StandardCharsets.UTF_8.newEncoder();
            this.f18827a[i13 + 1] = StandardCharsets.UTF_16BE.newEncoder();
        } else {
            this.f18827a = new CharsetEncoder[]{(CharsetEncoder) arrayList.get(0)};
        }
        if (charset != null) {
            while (true) {
                CharsetEncoder[] charsetEncoderArr = this.f18827a;
                if (i11 >= charsetEncoderArr.length) {
                    break;
                }
                if (charsetEncoderArr[i11] != null && charset.name().equals(this.f18827a[i11].charset().name())) {
                    i10 = i11;
                    break;
                }
                i11++;
            }
        }
        this.f18828b = i10;
    }
}
