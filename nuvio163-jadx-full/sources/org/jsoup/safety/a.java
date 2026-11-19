package org.jsoup.safety;

import java.util.function.Function;

/* JADX INFO: loaded from: classes5.dex */
public final /* synthetic */ class a implements Function {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f44248a;

    public /* synthetic */ a(int i10) {
        this.f44248a = i10;
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        switch (this.f44248a) {
            case 0:
                return Safelist.lambda$addAttributes$0((Safelist.TagName) obj);
            case 1:
                return Safelist.lambda$addProtocols$2((Safelist.TagName) obj);
            case 2:
                return Safelist.lambda$addProtocols$3((Safelist.AttributeKey) obj);
            default:
                return Safelist.lambda$addEnforcedAttribute$1((Safelist.TagName) obj);
        }
    }
}
