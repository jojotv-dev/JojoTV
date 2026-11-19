package org.jsoup.parser;

import org.jsoup.internal.StringUtil;

/* JADX INFO: loaded from: classes5.dex */
public final /* synthetic */ class b implements CharacterReader.CharPredicate {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f44242a;

    public /* synthetic */ b(int i10) {
        this.f44242a = i10;
    }

    @Override // org.jsoup.parser.CharacterReader.CharPredicate
    public final boolean test(char c5) {
        switch (this.f44242a) {
            case 0:
                return Character.isLetter(c5);
            case 1:
                return CharacterReader.lambda$consumeRawData$6(c5);
            case 2:
                return CharacterReader.lambda$consumeTagName$7(c5);
            case 3:
                return CharacterReader.lambda$consumeData$4(c5);
            case 4:
                return CharacterReader.lambda$consumeDigitSequence$8(c5);
            case 5:
                return StringUtil.isHexDigit(c5);
            default:
                return TokenQueue.isIdent(c5);
        }
    }
}
