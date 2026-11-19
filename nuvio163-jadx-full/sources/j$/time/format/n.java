package j$.time.format;

import java.text.ParsePosition;
import kotlin.jvm.internal.CharCompanionObject;

/* JADX INFO: loaded from: classes2.dex */
public class n {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public String f26596a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public String f26597b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final char f26598c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public n f26599d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public n f26600e;

    public boolean b(char c5, char c10) {
        return c5 == c10;
    }

    public n(String str, String str2, n nVar) {
        this.f26596a = str;
        this.f26597b = str2;
        this.f26599d = nVar;
        if (str.isEmpty()) {
            this.f26598c = CharCompanionObject.MAX_VALUE;
        } else {
            this.f26598c = this.f26596a.charAt(0);
        }
    }

    public final String c(CharSequence charSequence, ParsePosition parsePosition) {
        int index = parsePosition.getIndex();
        int length = charSequence.length();
        if (!e(charSequence, index, length)) {
            return null;
        }
        int length2 = this.f26596a.length() + index;
        n nVar = this.f26599d;
        if (nVar != null && length2 != length) {
            while (!b(nVar.f26598c, charSequence.charAt(length2))) {
                nVar = nVar.f26600e;
                if (nVar == null) {
                }
            }
            parsePosition.setIndex(length2);
            String strC = nVar.c(charSequence, parsePosition);
            if (strC != null) {
                return strC;
            }
        }
        parsePosition.setIndex(length2);
        return this.f26597b;
    }

    public n d(String str, String str2, n nVar) {
        return new n(str, str2, nVar);
    }

    public boolean e(CharSequence charSequence, int i10, int i11) {
        if (charSequence instanceof String) {
            return ((String) charSequence).startsWith(this.f26596a, i10);
        }
        int length = this.f26596a.length();
        if (length > i11 - i10) {
            return false;
        }
        int i12 = 0;
        while (true) {
            int i13 = length - 1;
            if (length <= 0) {
                return true;
            }
            int i14 = i12 + 1;
            int i15 = i10 + 1;
            if (!b(this.f26596a.charAt(i12), charSequence.charAt(i10))) {
                return false;
            }
            i10 = i15;
            length = i13;
            i12 = i14;
        }
    }

    public final boolean a(String str, String str2) {
        int i10 = 0;
        while (i10 < str.length() && i10 < this.f26596a.length() && b(str.charAt(i10), this.f26596a.charAt(i10))) {
            i10++;
        }
        if (i10 == this.f26596a.length()) {
            if (i10 < str.length()) {
                String strSubstring = str.substring(i10);
                for (n nVar = this.f26599d; nVar != null; nVar = nVar.f26600e) {
                    if (b(nVar.f26598c, strSubstring.charAt(0))) {
                        return nVar.a(strSubstring, str2);
                    }
                }
                n nVarD = d(strSubstring, str2, null);
                nVarD.f26600e = this.f26599d;
                this.f26599d = nVarD;
                return true;
            }
            this.f26597b = str2;
            return true;
        }
        n nVarD2 = d(this.f26596a.substring(i10), this.f26597b, this.f26599d);
        this.f26596a = str.substring(0, i10);
        this.f26599d = nVarD2;
        if (i10 < str.length()) {
            this.f26599d.f26600e = d(str.substring(i10), str2, null);
            this.f26597b = null;
            return true;
        }
        this.f26597b = str2;
        return true;
    }
}
