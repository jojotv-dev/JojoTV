package org.jsoup.parser;

/* JADX INFO: loaded from: classes5.dex */
public final /* synthetic */ class c implements CharacterReader.CharPredicate {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f44243a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ char[] f44244b;

    public /* synthetic */ c(int i10, char[] cArr) {
        this.f44243a = i10;
        this.f44244b = cArr;
    }

    @Override // org.jsoup.parser.CharacterReader.CharPredicate
    public final boolean test(char c5) {
        switch (this.f44243a) {
            case 0:
                return CharacterReader.lambda$consumeToAny$2(this.f44244b, c5);
            default:
                return CharacterReader.lambda$consumeToAnySorted$3(this.f44244b, c5);
        }
    }
}
