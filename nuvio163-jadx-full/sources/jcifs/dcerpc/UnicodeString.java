package jcifs.dcerpc;

/* JADX INFO: loaded from: classes5.dex */
public class UnicodeString extends rpc.unicode_string {
    boolean zterm;

    public UnicodeString(boolean z2) {
        this.zterm = z2;
    }

    public String toString() {
        int i10 = (this.length / 2) - (this.zterm ? 1 : 0);
        char[] cArr = new char[i10];
        for (int i11 = 0; i11 < i10; i11++) {
            cArr[i11] = (char) this.buffer[i11];
        }
        return new String(cArr, 0, i10);
    }

    public UnicodeString(rpc.unicode_string unicode_stringVar, boolean z2) {
        this.length = unicode_stringVar.length;
        this.maximum_length = unicode_stringVar.maximum_length;
        this.buffer = unicode_stringVar.buffer;
        this.zterm = z2;
    }

    public UnicodeString(String str, boolean z2) {
        this.zterm = z2;
        int length = str.length();
        int i10 = length + (z2 ? 1 : 0);
        short s2 = (short) (i10 * 2);
        this.maximum_length = s2;
        this.length = s2;
        this.buffer = new short[i10];
        int i11 = 0;
        while (i11 < length) {
            this.buffer[i11] = (short) str.charAt(i11);
            i11++;
        }
        if (z2) {
            this.buffer[i11] = 0;
        }
    }
}
