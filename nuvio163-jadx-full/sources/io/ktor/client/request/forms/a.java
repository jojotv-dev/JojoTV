package io.ktor.client.request.forms;

import kotlin.jvm.functions.Function0;

/* JADX INFO: loaded from: classes5.dex */
public final /* synthetic */ class a implements Function0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f24390b = 0;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ byte[] f24391c;

    public /* synthetic */ a(Object obj) {
        this.f24391c = (byte[]) obj;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        switch (this.f24390b) {
            case 0:
                return FormDslKt.formData$lambda$9$lambda$4(this.f24391c);
            default:
                return MultiPartFormDataContent.rawParts$lambda$3$lambda$2(this.f24391c);
        }
    }

    public /* synthetic */ a(byte[] bArr) {
        this.f24391c = bArr;
    }
}
