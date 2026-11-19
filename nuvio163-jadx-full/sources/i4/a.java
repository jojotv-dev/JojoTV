package i4;

/* JADX INFO: loaded from: classes3.dex */
public final class a {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final byte[] f23767e = new byte[1792];

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final CharSequence f23768a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f23769b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f23770c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public char f23771d;

    static {
        for (int i10 = 0; i10 < 1792; i10++) {
            f23767e[i10] = Character.getDirectionality(i10);
        }
    }

    public a(CharSequence charSequence) {
        this.f23768a = charSequence;
        this.f23769b = charSequence.length();
    }

    public final byte a() {
        int i10 = this.f23770c - 1;
        CharSequence charSequence = this.f23768a;
        char cCharAt = charSequence.charAt(i10);
        this.f23771d = cCharAt;
        if (Character.isLowSurrogate(cCharAt)) {
            int iCodePointBefore = Character.codePointBefore(charSequence, this.f23770c);
            this.f23770c -= Character.charCount(iCodePointBefore);
            return Character.getDirectionality(iCodePointBefore);
        }
        this.f23770c--;
        char c5 = this.f23771d;
        return c5 < 1792 ? f23767e[c5] : Character.getDirectionality(c5);
    }
}
