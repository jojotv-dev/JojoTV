package androidx.media3.session.legacy;

import android.annotation.SuppressLint;
import android.media.AudioAttributes;
import android.os.Build;
import android.util.SparseIntArray;
import androidx.media3.common.util.Assertions;
import androidx.media3.common.util.UnstableApi;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
@UnstableApi
public class AudioAttributesCompat {
    public static final int CONTENT_TYPE_MOVIE = 3;
    public static final int CONTENT_TYPE_MUSIC = 2;
    public static final int CONTENT_TYPE_SONIFICATION = 4;
    public static final int CONTENT_TYPE_SPEECH = 1;
    public static final int CONTENT_TYPE_UNKNOWN = 0;
    public static final int FLAG_AUDIBILITY_ENFORCED = 1;
    static final int FLAG_SCO = 4;
    static final int INVALID_STREAM_TYPE = -1;
    private static final int[] SDK_USAGES;
    private static final int SUPPRESSIBLE_CALL = 2;
    private static final int SUPPRESSIBLE_NOTIFICATION = 1;
    private static final SparseIntArray SUPPRESSIBLE_USAGES;
    public static final int USAGE_ALARM = 4;
    public static final int USAGE_ASSISTANCE_ACCESSIBILITY = 11;
    public static final int USAGE_ASSISTANCE_NAVIGATION_GUIDANCE = 12;
    public static final int USAGE_ASSISTANCE_SONIFICATION = 13;

    @SuppressLint({"InlinedApi"})
    public static final int USAGE_ASSISTANT = 16;
    public static final int USAGE_GAME = 14;
    public static final int USAGE_MEDIA = 1;
    public static final int USAGE_NOTIFICATION = 5;
    public static final int USAGE_NOTIFICATION_COMMUNICATION_DELAYED = 9;
    public static final int USAGE_NOTIFICATION_COMMUNICATION_INSTANT = 8;
    public static final int USAGE_NOTIFICATION_COMMUNICATION_REQUEST = 7;
    public static final int USAGE_NOTIFICATION_EVENT = 10;
    public static final int USAGE_NOTIFICATION_RINGTONE = 6;
    public static final int USAGE_UNKNOWN = 0;
    public static final int USAGE_VIRTUAL_SOURCE = 15;
    public static final int USAGE_VOICE_COMMUNICATION = 2;
    public static final int USAGE_VOICE_COMMUNICATION_SIGNALLING = 3;
    private final AudioAttributesImpl impl;

    /* JADX INFO: loaded from: classes3.dex */
    public interface AudioAttributesImpl {

        public interface Builder {
            AudioAttributesImpl build();

            Builder setContentType(int i10);

            Builder setFlags(int i10);

            Builder setLegacyStreamType(int i10);

            Builder setUsage(int i10);
        }

        Object getAudioAttributes();

        int getContentType();

        int getFlags();

        int getLegacyStreamType();

        int getUsage();
    }

    public static class AudioAttributesImplApi21 implements AudioAttributesImpl {
        public AudioAttributes audioAttributes;
        public final int legacyStreamType;

        public static class Builder implements AudioAttributesImpl.Builder {
            final AudioAttributes.Builder fwkBuilder;

            public Builder() {
                this.fwkBuilder = new AudioAttributes.Builder();
            }

            @Override // androidx.media3.session.legacy.AudioAttributesCompat.AudioAttributesImpl.Builder
            public AudioAttributesImpl build() {
                return new AudioAttributesImplApi21(this.fwkBuilder.build());
            }

            @Override // androidx.media3.session.legacy.AudioAttributesCompat.AudioAttributesImpl.Builder
            public Builder setContentType(int i10) {
                this.fwkBuilder.setContentType(i10);
                return this;
            }

            @Override // androidx.media3.session.legacy.AudioAttributesCompat.AudioAttributesImpl.Builder
            public Builder setFlags(int i10) {
                this.fwkBuilder.setFlags(i10);
                return this;
            }

            @Override // androidx.media3.session.legacy.AudioAttributesCompat.AudioAttributesImpl.Builder
            public Builder setLegacyStreamType(int i10) {
                this.fwkBuilder.setLegacyStreamType(i10);
                return this;
            }

            @Override // androidx.media3.session.legacy.AudioAttributesCompat.AudioAttributesImpl.Builder
            @SuppressLint({"WrongConstant"})
            public Builder setUsage(int i10) {
                if (i10 == 16) {
                    i10 = 12;
                }
                this.fwkBuilder.setUsage(i10);
                return this;
            }

            public Builder(Object obj) {
                this.fwkBuilder = new AudioAttributes.Builder((AudioAttributes) obj);
            }
        }

        public AudioAttributesImplApi21(AudioAttributes audioAttributes) {
            this(audioAttributes, -1);
        }

        public boolean equals(Object obj) {
            if (obj instanceof AudioAttributesImplApi21) {
                return Objects.equals(this.audioAttributes, ((AudioAttributesImplApi21) obj).audioAttributes);
            }
            return false;
        }

        @Override // androidx.media3.session.legacy.AudioAttributesCompat.AudioAttributesImpl
        public Object getAudioAttributes() {
            return this.audioAttributes;
        }

        @Override // androidx.media3.session.legacy.AudioAttributesCompat.AudioAttributesImpl
        public int getContentType() {
            return ((AudioAttributes) Assertions.checkNotNull(this.audioAttributes)).getContentType();
        }

        @Override // androidx.media3.session.legacy.AudioAttributesCompat.AudioAttributesImpl
        public int getFlags() {
            return ((AudioAttributes) Assertions.checkNotNull(this.audioAttributes)).getFlags();
        }

        @Override // androidx.media3.session.legacy.AudioAttributesCompat.AudioAttributesImpl
        public int getLegacyStreamType() {
            int i10 = this.legacyStreamType;
            return i10 != -1 ? i10 : AudioAttributesCompat.toVolumeStreamType(getFlags(), getUsage());
        }

        @Override // androidx.media3.session.legacy.AudioAttributesCompat.AudioAttributesImpl
        public int getUsage() {
            return ((AudioAttributes) Assertions.checkNotNull(this.audioAttributes)).getUsage();
        }

        public int hashCode() {
            return ((AudioAttributes) Assertions.checkNotNull(this.audioAttributes)).hashCode();
        }

        public String toString() {
            return "AudioAttributesCompat: audioattributes=" + this.audioAttributes;
        }

        public AudioAttributesImplApi21(AudioAttributes audioAttributes, int i10) {
            this.audioAttributes = audioAttributes;
            this.legacyStreamType = i10;
        }
    }

    public static class AudioAttributesImplApi26 extends AudioAttributesImplApi21 {

        public static class Builder extends AudioAttributesImplApi21.Builder {
            public Builder() {
            }

            @Override // androidx.media3.session.legacy.AudioAttributesCompat.AudioAttributesImplApi21.Builder, androidx.media3.session.legacy.AudioAttributesCompat.AudioAttributesImpl.Builder
            public AudioAttributesImpl build() {
                return new AudioAttributesImplApi26(this.fwkBuilder.build());
            }

            public Builder(Object obj) {
                super(obj);
            }

            @Override // androidx.media3.session.legacy.AudioAttributesCompat.AudioAttributesImplApi21.Builder, androidx.media3.session.legacy.AudioAttributesCompat.AudioAttributesImpl.Builder
            @SuppressLint({"WrongConstant"})
            public Builder setUsage(int i10) {
                this.fwkBuilder.setUsage(i10);
                return this;
            }
        }

        public AudioAttributesImplApi26(AudioAttributes audioAttributes) {
            super(audioAttributes, -1);
        }
    }

    /* JADX INFO: loaded from: classes3.dex */
    public static abstract class AudioManagerHidden {
        public static final int STREAM_ACCESSIBILITY = 10;
        public static final int STREAM_BLUETOOTH_SCO = 6;
        public static final int STREAM_SYSTEM_ENFORCED = 7;

        private AudioManagerHidden() {
        }
    }

    static {
        SparseIntArray sparseIntArray = new SparseIntArray();
        SUPPRESSIBLE_USAGES = sparseIntArray;
        sparseIntArray.put(5, 1);
        sparseIntArray.put(6, 2);
        sparseIntArray.put(7, 2);
        sparseIntArray.put(8, 1);
        sparseIntArray.put(9, 1);
        sparseIntArray.put(10, 1);
        SDK_USAGES = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 16};
    }

    public AudioAttributesCompat(AudioAttributesImpl audioAttributesImpl) {
        this.impl = audioAttributesImpl;
    }

    public static int toVolumeStreamType(int i10, int i11) {
        if ((i10 & 1) == 1) {
            return 7;
        }
        if ((i10 & 4) == 4) {
            return 6;
        }
        switch (i11) {
            case 2:
                return 0;
            case 3:
                return 8;
            case 4:
                return 4;
            case 5:
            case 7:
            case 8:
            case 9:
            case 10:
                return 5;
            case 6:
                return 2;
            case 11:
                return 10;
            case 12:
            default:
                return 3;
            case 13:
                return 1;
        }
    }

    public static AudioAttributesCompat wrap(Object obj) {
        return Build.VERSION.SDK_INT >= 26 ? new AudioAttributesCompat(new AudioAttributesImplApi26((AudioAttributes) obj)) : new AudioAttributesCompat(new AudioAttributesImplApi21((AudioAttributes) obj));
    }

    public boolean equals(Object obj) {
        if (obj instanceof AudioAttributesCompat) {
            return this.impl.equals(((AudioAttributesCompat) obj).impl);
        }
        return false;
    }

    public int getContentType() {
        return this.impl.getContentType();
    }

    public int getFlags() {
        return this.impl.getFlags();
    }

    public int getLegacyStreamType() {
        return this.impl.getLegacyStreamType();
    }

    public int getUsage() {
        return this.impl.getUsage();
    }

    public int hashCode() {
        return this.impl.hashCode();
    }

    public String toString() {
        return this.impl.toString();
    }

    public Object unwrap() {
        return this.impl.getAudioAttributes();
    }

    public static class Builder {
        final AudioAttributesImpl.Builder builderImpl;

        public Builder() {
            if (Build.VERSION.SDK_INT >= 26) {
                this.builderImpl = new AudioAttributesImplApi26.Builder();
            } else {
                this.builderImpl = new AudioAttributesImplApi21.Builder();
            }
        }

        public AudioAttributesCompat build() {
            return new AudioAttributesCompat(this.builderImpl.build());
        }

        public Builder setContentType(int i10) {
            this.builderImpl.setContentType(i10);
            return this;
        }

        public Builder setFlags(int i10) {
            this.builderImpl.setFlags(i10);
            return this;
        }

        public Builder setLegacyStreamType(int i10) {
            this.builderImpl.setLegacyStreamType(i10);
            return this;
        }

        public Builder setUsage(int i10) {
            this.builderImpl.setUsage(i10);
            return this;
        }

        public Builder(AudioAttributesCompat audioAttributesCompat) {
            if (Build.VERSION.SDK_INT >= 26) {
                this.builderImpl = new AudioAttributesImplApi26.Builder(Assertions.checkNotNull(audioAttributesCompat.unwrap()));
            } else {
                this.builderImpl = new AudioAttributesImplApi21.Builder(Assertions.checkNotNull(audioAttributesCompat.unwrap()));
            }
        }
    }
}
