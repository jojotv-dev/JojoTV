package androidx.media3.effect;

import android.content.Context;
import androidx.media3.common.Effect;
import androidx.media3.common.util.UnstableApi;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public interface GlEffect extends Effect {
    default boolean isNoOp(int i10, int i11) {
        return false;
    }

    GlShaderProgram toGlShaderProgram(Context context, boolean z2);
}
