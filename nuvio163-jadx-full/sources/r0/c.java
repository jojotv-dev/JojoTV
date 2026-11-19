package r0;

import android.app.PendingIntent;
import android.content.Context;
import android.view.MenuItem;
import android.view.textclassifier.TextClassification;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class c implements MenuItem.OnMenuItemClickListener {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f50973a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ Object f50974b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Object f50975c;

    public /* synthetic */ c(Object obj, Object obj2, int i10) {
        this.f50973a = i10;
        this.f50974b = obj;
        this.f50975c = obj2;
    }

    @Override // android.view.MenuItem.OnMenuItemClickListener
    public final boolean onMenuItemClick(MenuItem menuItem) throws PendingIntent.CanceledException {
        switch (this.f50973a) {
            case 0:
                ((p0.d) this.f50974b).f44270d.invoke(((d) this.f50975c).f50976a);
                break;
            default:
                r.a((Context) this.f50974b, (TextClassification) this.f50975c);
                break;
        }
        return true;
    }
}
