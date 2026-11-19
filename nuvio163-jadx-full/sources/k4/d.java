package k4;

import android.content.ClipData;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContentInfo;

/* JADX INFO: loaded from: classes3.dex */
public final class d implements e, g {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f27753a = 0;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Object f27754b;

    public d(ContentInfo contentInfo) {
        contentInfo.getClass();
        this.f27754b = contentInfo;
    }

    @Override // k4.g
    public ContentInfo a() {
        return (ContentInfo) this.f27754b;
    }

    @Override // k4.e
    public void b(Uri uri) {
        ((ContentInfo.Builder) this.f27754b).setLinkUri(uri);
    }

    @Override // k4.e
    public h build() {
        return new h(new d(((ContentInfo.Builder) this.f27754b).build()));
    }

    @Override // k4.g
    public ClipData c() {
        return ((ContentInfo) this.f27754b).getClip();
    }

    @Override // k4.g
    public int getFlags() {
        return ((ContentInfo) this.f27754b).getFlags();
    }

    @Override // k4.g
    public int getSource() {
        return ((ContentInfo) this.f27754b).getSource();
    }

    @Override // k4.e
    public void setExtras(Bundle bundle) {
        ((ContentInfo.Builder) this.f27754b).setExtras(bundle);
    }

    @Override // k4.e
    public void setFlags(int i10) {
        ((ContentInfo.Builder) this.f27754b).setFlags(i10);
    }

    public String toString() {
        switch (this.f27753a) {
            case 1:
                return "ContentInfoCompat{" + ((ContentInfo) this.f27754b) + "}";
            default:
                return super.toString();
        }
    }

    public d(ClipData clipData, int i10) {
        this.f27754b = c.a(clipData, i10);
    }
}
