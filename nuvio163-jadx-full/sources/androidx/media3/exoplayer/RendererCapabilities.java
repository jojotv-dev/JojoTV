package androidx.media3.exoplayer;

import android.annotation.SuppressLint;
import androidx.media3.common.Format;
import androidx.media3.common.util.UnstableApi;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* JADX INFO: loaded from: classes.dex */
@UnstableApi
public interface RendererCapabilities {
    public static final int ADAPTIVE_NOT_SEAMLESS = 8;
    public static final int ADAPTIVE_NOT_SUPPORTED = 0;
    public static final int ADAPTIVE_SEAMLESS = 16;
    public static final int ADAPTIVE_SUPPORT_MASK = 24;
    public static final int AUDIO_OFFLOAD_GAPLESS_SUPPORTED = 1024;
    public static final int AUDIO_OFFLOAD_NOT_SUPPORTED = 0;
    public static final int AUDIO_OFFLOAD_SPEED_CHANGE_SUPPORTED = 2048;
    public static final int AUDIO_OFFLOAD_SUPPORTED = 512;
    public static final int AUDIO_OFFLOAD_SUPPORT_MASK = 3584;
    public static final int DECODER_SUPPORT_FALLBACK = 0;
    public static final int DECODER_SUPPORT_FALLBACK_MIMETYPE = 256;
    public static final int DECODER_SUPPORT_MASK = 384;
    public static final int DECODER_SUPPORT_PRIMARY = 128;
    public static final int FORMAT_SUPPORT_MASK = 7;
    public static final int HARDWARE_ACCELERATION_NOT_SUPPORTED = 0;
    public static final int HARDWARE_ACCELERATION_SUPPORTED = 64;
    public static final int HARDWARE_ACCELERATION_SUPPORT_MASK = 64;
    public static final int TUNNELING_NOT_SUPPORTED = 0;
    public static final int TUNNELING_SUPPORTED = 32;
    public static final int TUNNELING_SUPPORT_MASK = 32;

    /* JADX INFO: loaded from: classes3.dex */
    @Target({ElementType.TYPE_USE})
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface AdaptiveSupport {
    }

    /* JADX INFO: loaded from: classes3.dex */
    @Target({ElementType.TYPE_USE})
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface AudioOffloadSupport {
    }

    /* JADX INFO: loaded from: classes3.dex */
    @Target({ElementType.TYPE_USE})
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface Capabilities {
    }

    /* JADX INFO: loaded from: classes3.dex */
    @Target({ElementType.TYPE_USE})
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface DecoderSupport {
    }

    /* JADX INFO: loaded from: classes3.dex */
    @Target({ElementType.TYPE_USE})
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface HardwareAccelerationSupport {
    }

    /* JADX INFO: loaded from: classes3.dex */
    public interface Listener {
        void onRendererCapabilitiesChanged(Renderer renderer);
    }

    /* JADX INFO: loaded from: classes3.dex */
    @Target({ElementType.TYPE_USE})
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface TunnelingSupport {
    }

    @SuppressLint({"WrongConstant"})
    static int create(int i10, int i11, int i12, int i13, int i14, int i15) {
        return i10 | i11 | i12 | i13 | i14 | i15;
    }

    @SuppressLint({"WrongConstant"})
    static int getAdaptiveSupport(int i10) {
        return i10 & 24;
    }

    @SuppressLint({"WrongConstant"})
    static int getAudioOffloadSupport(int i10) {
        return i10 & AUDIO_OFFLOAD_SUPPORT_MASK;
    }

    @SuppressLint({"WrongConstant"})
    static int getDecoderSupport(int i10) {
        return i10 & DECODER_SUPPORT_MASK;
    }

    @SuppressLint({"WrongConstant"})
    static int getFormatSupport(int i10) {
        return i10 & 7;
    }

    @SuppressLint({"WrongConstant"})
    static int getHardwareAccelerationSupport(int i10) {
        return i10 & 64;
    }

    @SuppressLint({"WrongConstant"})
    static int getTunnelingSupport(int i10) {
        return i10 & 32;
    }

    static boolean isFormatSupported(int i10, boolean z2) {
        int formatSupport = getFormatSupport(i10);
        if (formatSupport != 4) {
            return z2 && formatSupport == 3;
        }
        return true;
    }

    default void clearListener() {
    }

    String getName();

    int getTrackType();

    default void setListener(Listener listener) {
    }

    int supportsFormat(Format format);

    int supportsMixedMimeTypeAdaptation();

    static int create(int i10) {
        return create(i10, 0, 0, 0);
    }

    static int create(int i10, int i11, int i12) {
        return create(i10, i11, i12, 0, 128, 0);
    }

    static int create(int i10, int i11, int i12, int i13) {
        return create(i10, i11, i12, 0, 128, i13);
    }

    static int create(int i10, int i11, int i12, int i13, int i14) {
        return create(i10, i11, i12, i13, i14, 0);
    }
}
