package io.ktor.http.content;

import kotlin.jvm.functions.Function2;

/* JADX INFO: loaded from: classes5.dex */
public final /* synthetic */ class c implements Function2 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f24408b;

    public /* synthetic */ c(int i10) {
        this.f24408b = i10;
    }

    @Override // kotlin.jvm.functions.Function2
    public final Object invoke(Object obj, Object obj2) {
        boolean zHeaders_delegate$lambda$2$lambda$1$lambda$0;
        String str = (String) obj;
        String str2 = (String) obj2;
        switch (this.f24408b) {
            case 0:
                zHeaders_delegate$lambda$2$lambda$1$lambda$0 = CompressedReadChannelResponse.headers_delegate$lambda$2$lambda$1$lambda$0(str, str2);
                break;
            default:
                zHeaders_delegate$lambda$2$lambda$1$lambda$0 = CompressedWriteChannelResponse.headers_delegate$lambda$2$lambda$1$lambda$0(str, str2);
                break;
        }
        return Boolean.valueOf(zHeaders_delegate$lambda$2$lambda$1$lambda$0);
    }
}
