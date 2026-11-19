package o7;

/* JADX INFO: loaded from: classes3.dex */
public final class c0 extends b0 implements Cloneable {

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ int f43549i;

    @Override // o7.b0
    public final boolean T(int i10) {
        switch (this.f43549i) {
            case 0:
                return Character.isLetterOrDigit(i10);
            case 1:
                return Character.isJavaIdentifierStart(i10);
            case 2:
                return Character.isJavaIdentifierPart(i10);
            case 3:
                return Character.isUnicodeIdentifierStart(i10);
            case 4:
                return Character.isUnicodeIdentifierPart(i10);
            case 5:
                return Character.isIdentifierIgnorable(i10);
            case 6:
                return Character.isSpaceChar(i10);
            case 7:
                return Character.isWhitespace(i10);
            case 8:
                return Character.isISOControl(i10);
            case 9:
                return Character.isMirrored(i10);
            case 10:
                return Character.isLowerCase(i10);
            case 11:
                return Character.isUpperCase(i10);
            case 12:
                return Character.isAlphabetic(i10);
            case 13:
                return Character.isIdeographic(i10);
            case 14:
                return Character.isTitleCase(i10);
            case 15:
                return Character.isDigit(i10);
            case 16:
                return Character.isDefined(i10);
            default:
                return Character.isLetter(i10);
        }
    }

    /* JADX INFO: renamed from: U, reason: merged with bridge method [inline-methods] */
    public final c0 clone() {
        try {
            return (c0) super.clone();
        } catch (CloneNotSupportedException e10) {
            throw new AssertionError(e10);
        }
    }
}
