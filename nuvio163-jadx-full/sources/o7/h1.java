package o7;

/* JADX INFO: loaded from: classes3.dex */
public class h1 extends y0 {

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public final /* synthetic */ int f43584n;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ h1(int[] iArr, int i10) {
        super(iArr);
        this.f43584n = i10;
    }

    public int T(int i10) {
        return a.d(i10);
    }

    @Override // io.sentry.internal.debugmeta.c
    public final boolean y(b bVar, int i10, CharSequence charSequence) {
        int iCharCount;
        switch (this.f43584n) {
            case 0:
                int[] iArr = (int[]) this.f43681m;
                int length = iArr.length;
                for (int i11 = 0; i11 < length; i11++) {
                    int i12 = i10 + i11;
                    if (i12 >= bVar.f43534b) {
                        return false;
                    }
                    char cCharAt = charSequence.charAt(i12);
                    int i13 = iArr[i11];
                    if (i13 != cCharAt && i13 != a.d(cCharAt)) {
                        return false;
                    }
                    bVar.f43542j++;
                }
                return ((io.sentry.internal.debugmeta.c) this.f25625c).y(bVar, i10 + length, charSequence);
            case 1:
                for (int i14 : (int[]) this.f43681m) {
                    if (i10 >= bVar.f43534b) {
                        return false;
                    }
                    int iCodePointAt = Character.codePointAt(charSequence, i10);
                    if ((i14 != iCodePointAt && i14 != T(iCodePointAt)) || (i10 = i10 + (iCharCount = Character.charCount(iCodePointAt))) > bVar.f43534b) {
                        return false;
                    }
                    bVar.f43542j += iCharCount;
                }
                return ((io.sentry.internal.debugmeta.c) this.f25625c).y(bVar, i10, charSequence);
            default:
                int[] iArr2 = (int[]) this.f43681m;
                int length2 = iArr2.length;
                for (int i15 = 0; i15 < length2; i15++) {
                    int i16 = i10 + i15;
                    if (i16 >= bVar.f43534b) {
                        return false;
                    }
                    char cCharAt2 = charSequence.charAt(i16);
                    int i17 = iArr2[i15];
                    if (i17 != cCharAt2 && i17 != Character.toLowerCase(Character.toUpperCase((int) cCharAt2))) {
                        return false;
                    }
                    bVar.f43542j++;
                }
                return ((io.sentry.internal.debugmeta.c) this.f25625c).y(bVar, i10 + length2, charSequence);
        }
    }
}
