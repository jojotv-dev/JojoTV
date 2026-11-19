package org.jsoup.internal;

import java.util.function.Function;

/* JADX INFO: loaded from: classes5.dex */
public final /* synthetic */ class a implements Function {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f44229a;

    public /* synthetic */ a(int i10) {
        this.f44229a = i10;
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        switch (this.f44229a) {
            case 0:
                return Functions.lambda$static$0(obj);
            case 1:
                return Functions.lambda$static$1(obj);
            case 2:
                return Functions.lambda$static$2(obj);
            case 3:
                return Functions.lambda$static$3(obj);
            default:
                return ((StringUtil.StringJoiner) obj).complete();
        }
    }
}
