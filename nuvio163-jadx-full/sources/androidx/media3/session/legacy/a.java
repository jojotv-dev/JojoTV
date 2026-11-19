package androidx.media3.session.legacy;

import android.content.Context;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.RenderNode;
import android.media.session.MediaSession;
import android.os.Bundle;

/* JADX INFO: loaded from: classes.dex */
public abstract /* synthetic */ class a {
    public static /* synthetic */ BlendModeColorFilter a(int i10, BlendMode blendMode) {
        return new BlendModeColorFilter(i10, blendMode);
    }

    public static /* synthetic */ RenderNode b() {
        return new RenderNode("AndroidEdgeEffectOverscrollEffect");
    }

    public static /* synthetic */ MediaSession c(Context context, String str, Bundle bundle) {
        return new MediaSession(context, str, bundle);
    }

    public static /* synthetic */ void d() {
    }
}
