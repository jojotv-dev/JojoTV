package i8;

/* JADX INFO: loaded from: classes.dex */
public enum a {
    /* JADX INFO: Fake field, exist only in values array */
    TERMINATOR(new int[]{0, 0, 0}, 0),
    NUMERIC(new int[]{10, 12, 14}, 1),
    ALPHANUMERIC(new int[]{9, 11, 13}, 2),
    /* JADX INFO: Fake field, exist only in values array */
    STRUCTURED_APPEND(new int[]{0, 0, 0}, 3),
    BYTE(new int[]{8, 16, 16}, 4),
    ECI(new int[]{0, 0, 0}, 7),
    KANJI(new int[]{8, 10, 12}, 8),
    FNC1_FIRST_POSITION(new int[]{0, 0, 0}, 5),
    /* JADX INFO: Fake field, exist only in values array */
    FNC1_SECOND_POSITION(new int[]{0, 0, 0}, 9),
    /* JADX INFO: Fake field, exist only in values array */
    HANZI(new int[]{8, 10, 12}, 13);


    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int[] f23829b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f23830c;

    a(int[] iArr, int i10) {
        this.f23829b = iArr;
        this.f23830c = i10;
    }

    public final int a(c cVar) {
        char c5;
        int i10 = cVar.f23835a;
        if (i10 <= 9) {
            c5 = 0;
        } else {
            c5 = i10 <= 26 ? (char) 1 : (char) 2;
        }
        return this.f23829b[c5];
    }
}
