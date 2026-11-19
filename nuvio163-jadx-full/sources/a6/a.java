package a6;

import android.content.ContentValues;
import android.net.Uri;
import j$.util.DesugarTimeZone;
import java.text.SimpleDateFormat;

/* JADX INFO: loaded from: classes.dex */
public abstract class a extends c {
    static {
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").setTimeZone(DesugarTimeZone.getTimeZone("GMT-0"));
    }

    public a() {
        this.f589b = new ContentValues();
    }

    public final void r(int i10) {
        ((ContentValues) this.f589b).put("duration_millis", Integer.valueOf(i10));
    }

    public final void s(Uri uri) {
        ((ContentValues) this.f589b).put("intent_uri", uri == null ? null : uri.toString());
    }

    public final void t(int i10) {
        ((ContentValues) this.f589b).put("last_playback_position_millis", Integer.valueOf(i10));
    }
}
