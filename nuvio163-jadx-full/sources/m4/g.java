package m4;

import android.content.ClipDescription;
import android.net.Uri;
import android.view.inputmethod.InputContentInfo;

/* JADX INFO: loaded from: classes3.dex */
public final class g implements h {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final InputContentInfo f34826a;

    public g(Object obj) {
        this.f34826a = (InputContentInfo) obj;
    }

    @Override // m4.h
    public final Object a() {
        return this.f34826a;
    }

    @Override // m4.h
    public final Uri b() {
        return this.f34826a.getContentUri();
    }

    @Override // m4.h
    public final void c() {
        this.f34826a.requestPermission();
    }

    @Override // m4.h
    public final Uri d() {
        return this.f34826a.getLinkUri();
    }

    @Override // m4.h
    public final ClipDescription getDescription() {
        return this.f34826a.getDescription();
    }

    public g(Uri uri, ClipDescription clipDescription, Uri uri2) {
        this.f34826a = new InputContentInfo(uri, clipDescription, uri2);
    }
}
