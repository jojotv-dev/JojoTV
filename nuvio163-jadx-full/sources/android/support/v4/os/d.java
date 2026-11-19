package android.support.v4.os;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

/* JADX INFO: loaded from: classes3.dex */
public final class d implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f1942b = 0;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f1943c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final Object f1944e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ Object f1945i;

    public d(e eVar, int i10, Bundle bundle) {
        this.f1945i = eVar;
        this.f1943c = i10;
        this.f1944e = bundle;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f1942b) {
            case 0:
                ((e) this.f1945i).onReceiveResult(this.f1943c, (Bundle) this.f1944e);
                break;
            default:
                ((TextView) this.f1944e).setTypeface((Typeface) this.f1945i, this.f1943c);
                break;
        }
    }

    public d(TextView textView, Typeface typeface, int i10) {
        this.f1944e = textView;
        this.f1945i = typeface;
        this.f1943c = i10;
    }
}
