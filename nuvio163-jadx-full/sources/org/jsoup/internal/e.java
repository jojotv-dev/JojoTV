package org.jsoup.internal;

import java.util.function.Supplier;

/* JADX INFO: loaded from: classes5.dex */
public final /* synthetic */ class e implements Supplier {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f44238a;

    public /* synthetic */ e(int i10) {
        this.f44238a = i10;
    }

    @Override // java.util.function.Supplier
    public final Object get() {
        switch (this.f44238a) {
            case 0:
                return SimpleBufferedInput.lambda$static$0();
            case 1:
                return SoftPool.lambda$new$0();
            default:
                return StringUtil.lambda$static$0();
        }
    }
}
