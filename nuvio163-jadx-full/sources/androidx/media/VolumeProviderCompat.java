package androidx.media;

import android.media.VolumeProvider;
import android.os.Build;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes3.dex */
public abstract class VolumeProviderCompat {
    public static final int VOLUME_CONTROL_ABSOLUTE = 2;
    public static final int VOLUME_CONTROL_FIXED = 0;
    public static final int VOLUME_CONTROL_RELATIVE = 1;
    private Callback mCallback;
    private final String mControlId;
    private final int mControlType;
    private int mCurrentVolume;
    private final int mMaxVolume;
    private VolumeProvider mVolumeProviderFwk;

    public static class Api21Impl {
        private Api21Impl() {
        }

        public static void setCurrentVolume(VolumeProvider volumeProvider, int i10) {
            volumeProvider.setCurrentVolume(i10);
        }
    }

    public static abstract class Callback {
        public abstract void onVolumeChanged(VolumeProviderCompat volumeProviderCompat);
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface ControlType {
    }

    public VolumeProviderCompat(int i10, int i11, int i12) {
        this(i10, i11, i12, null);
    }

    public final int getCurrentVolume() {
        return this.mCurrentVolume;
    }

    public final int getMaxVolume() {
        return this.mMaxVolume;
    }

    public final int getVolumeControl() {
        return this.mControlType;
    }

    public final String getVolumeControlId() {
        return this.mControlId;
    }

    public Object getVolumeProvider() {
        VolumeProviderCompat volumeProviderCompat;
        if (this.mVolumeProviderFwk != null) {
            volumeProviderCompat = this;
        } else if (Build.VERSION.SDK_INT >= 30) {
            volumeProviderCompat = this;
            volumeProviderCompat.mVolumeProviderFwk = new VolumeProvider(this.mControlType, this.mMaxVolume, this.mCurrentVolume, this.mControlId) { // from class: androidx.media.VolumeProviderCompat.1
                @Override // android.media.VolumeProvider
                public void onAdjustVolume(int i10) {
                    VolumeProviderCompat.this.onAdjustVolume(i10);
                }

                @Override // android.media.VolumeProvider
                public void onSetVolumeTo(int i10) {
                    VolumeProviderCompat.this.onSetVolumeTo(i10);
                }
            };
        } else {
            volumeProviderCompat = this;
            volumeProviderCompat.mVolumeProviderFwk = new VolumeProvider(volumeProviderCompat.mControlType, volumeProviderCompat.mMaxVolume, volumeProviderCompat.mCurrentVolume) { // from class: androidx.media.VolumeProviderCompat.2
                @Override // android.media.VolumeProvider
                public void onAdjustVolume(int i10) {
                    VolumeProviderCompat.this.onAdjustVolume(i10);
                }

                @Override // android.media.VolumeProvider
                public void onSetVolumeTo(int i10) {
                    VolumeProviderCompat.this.onSetVolumeTo(i10);
                }
            };
        }
        return volumeProviderCompat.mVolumeProviderFwk;
    }

    public void onAdjustVolume(int i10) {
    }

    public void onSetVolumeTo(int i10) {
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public final void setCurrentVolume(int i10) {
        this.mCurrentVolume = i10;
        Api21Impl.setCurrentVolume((VolumeProvider) getVolumeProvider(), i10);
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onVolumeChanged(this);
        }
    }

    public VolumeProviderCompat(int i10, int i11, int i12, String str) {
        this.mControlType = i10;
        this.mMaxVolume = i11;
        this.mCurrentVolume = i12;
        this.mControlId = str;
    }
}
