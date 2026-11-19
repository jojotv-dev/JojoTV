package io.sentry.android.replay.util;

import android.graphics.Bitmap;
import android.graphics.Paint;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;

/* JADX INFO: loaded from: classes5.dex */
public final class c extends Lambda implements Function0 {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final c f25304c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final c f25305e;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f25306b;

    static {
        int i10 = 0;
        f25304c = new c(i10, 0);
        f25305e = new c(i10, 1);
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ c(int i10, int i11) {
        super(i10);
        this.f25306b = i11;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Object invoke() {
        switch (this.f25306b) {
            case 0:
                return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            default:
                return new Paint();
        }
    }
}
