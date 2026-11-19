package q7;

/* JADX INFO: loaded from: classes3.dex */
public final class e extends c {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final int f45744c = Integer.numberOfLeadingZeros(31);

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final e f45745e = new e("CharMatcher.whitespace()");

    @Override // q7.a
    public final boolean a(char c5) {
        return "\u2002\u3000\r\u0085\u200a\u2005\u2000\u3000\u2029\u000b\u3000\u2008\u2003\u205f\u3000\u1680\t \u2006\u2001  \f\u2009\u3000\u2004\u3000\u3000\u2028\n \u3000".charAt((48906 * c5) >>> f45744c) == c5;
    }
}
