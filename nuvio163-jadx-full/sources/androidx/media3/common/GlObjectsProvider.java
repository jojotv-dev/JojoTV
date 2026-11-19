package androidx.media3.common;

import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import androidx.media3.common.util.UnstableApi;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public interface GlObjectsProvider {
    GlTextureInfo createBuffersForTexture(int i10, int i11, int i12);

    EGLContext createEglContext(EGLDisplay eGLDisplay, int i10, int[] iArr);

    EGLSurface createEglSurface(EGLDisplay eGLDisplay, Object obj, int i10, boolean z2);

    EGLSurface createFocusedPlaceholderEglSurface(EGLContext eGLContext, EGLDisplay eGLDisplay);

    void release(EGLDisplay eGLDisplay);
}
