package kotlin.text;

import kotlin.jvm.functions.Function1;

/* JADX INFO: loaded from: classes5.dex */
public final /* synthetic */ class c implements Function1 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f31776b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Object f31777c;

    public /* synthetic */ c(Object obj, int i10) {
        this.f31776b = i10;
        this.f31777c = obj;
    }

    @Override // kotlin.jvm.functions.Function1
    public final Object invoke(Object obj) {
        switch (this.f31776b) {
            case 0:
                return StringsKt__IndentKt.prependIndent$lambda$0$StringsKt__IndentKt((String) this.f31777c, (String) obj);
            case 1:
                return StringsKt__IndentKt.getIndentFunction$lambda$1$StringsKt__IndentKt((String) this.f31777c, (String) obj);
            default:
                return ((MatcherMatchResult$groups$1) this.f31777c).get(((Integer) obj).intValue());
        }
    }
}
