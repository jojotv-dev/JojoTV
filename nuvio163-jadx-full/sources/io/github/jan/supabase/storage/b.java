package io.github.jan.supabase.storage;

import java.util.Collection;
import kotlin.jvm.functions.Function1;

/* JADX INFO: loaded from: classes5.dex */
public final /* synthetic */ class b implements Function1 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f24321b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Collection f24322c;

    public /* synthetic */ b(Collection collection, int i10) {
        this.f24321b = i10;
        this.f24322c = collection;
    }

    @Override // kotlin.jvm.functions.Function1
    public final Object invoke(Object obj) {
        switch (this.f24321b) {
            case 0:
                return BucketApiImpl.createSignedUrls_KLykuaI$lambda$11$lambda$10(this.f24322c, (oi.g) obj);
            case 1:
                return BucketApiImpl.delete$lambda$2$lambda$1(this.f24322c, (oi.g) obj);
            case 2:
                return Boolean.valueOf(this.f24322c.contains(obj));
            default:
                return Boolean.valueOf(this.f24322c.contains(obj));
        }
    }
}
