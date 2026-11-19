package io.sentry;

import android.os.Build;
import android.os.Process;
import androidx.compose.ui.platform.AndroidComposeView;
import io.sentry.android.ndk.SentryNdk;
import kotlin.Unit;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class d5 implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f25470b;

    public /* synthetic */ d5(int i10) {
        this.f25470b = i10;
    }

    private final void a() {
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f25470b) {
            case 0:
                return;
            case 1:
                io.sentry.android.core.internal.util.e.f24784b = Process.myTid();
                return;
            case 2:
                SentryNdk.lambda$static$0();
                return;
            default:
                t.m0 m0Var = AndroidComposeView.Y0;
                synchronized (m0Var) {
                    try {
                        int i10 = 0;
                        if (Build.VERSION.SDK_INT < 30) {
                            Object[] objArr = m0Var.f57101a;
                            int i11 = m0Var.f57102b;
                            while (i10 < i11) {
                                AndroidComposeView androidComposeView = (AndroidComposeView) objArr[i10];
                                boolean showLayoutBounds = androidComposeView.getShowLayoutBounds();
                                Class cls = AndroidComposeView.V0;
                                androidComposeView.setShowLayoutBounds(x2.q1.g());
                                if (showLayoutBounds != androidComposeView.getShowLayoutBounds()) {
                                    androidComposeView.post(new x2.j(androidComposeView, 2));
                                }
                                i10++;
                            }
                        } else {
                            Object[] objArr2 = m0Var.f57101a;
                            int i12 = m0Var.f57102b;
                            while (i10 < i12) {
                                AndroidComposeView androidComposeView2 = (AndroidComposeView) objArr2[i10];
                                androidComposeView2.post(new x2.j(androidComposeView2, 3));
                                i10++;
                            }
                        }
                        Unit unit = Unit.INSTANCE;
                    } catch (Throwable th2) {
                        throw th2;
                    }
                }
                return;
        }
    }
}
