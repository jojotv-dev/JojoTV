package kotlin.text;

import kotlin.jvm.functions.Function1;

/* JADX INFO: loaded from: classes5.dex */
public final /* synthetic */ class d implements Function1 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f31778b;

    public /* synthetic */ d(int i10) {
        this.f31778b = i10;
    }

    @Override // kotlin.jvm.functions.Function1
    public final Object invoke(Object obj) {
        switch (this.f31778b) {
            case 0:
                return StringsKt__IndentKt.getIndentFunction$lambda$0$StringsKt__IndentKt((String) obj);
            case 1:
                return StringsKt___StringsKt.windowed$lambda$0$StringsKt___StringsKt((CharSequence) obj);
            case 2:
                return StringsKt___StringsKt.windowedSequence$lambda$0$StringsKt___StringsKt((CharSequence) obj);
            default:
                return StringsKt___StringsKt.chunkedSequence$lambda$0$StringsKt___StringsKt((CharSequence) obj);
        }
    }
}
