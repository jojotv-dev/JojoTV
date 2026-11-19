package androidx.media3.common;

import android.text.TextUtils;
import androidx.media3.common.util.Assertions;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.common.util.Util;
import androidx.media3.extractor.ts.TsExtractor;
import com.eclipsesource.v8.V8Value;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jcifs.internal.smb1.ServerMessageBlock;
import kf.ge;
import okhttp3.internal.http.HttpStatusCodesKt;

/* JADX INFO: loaded from: classes.dex */
public final class MimeTypes {
    public static final String APPLICATION_AIT = "application/vnd.dvb.ait";

    @UnstableApi
    public static final String APPLICATION_CAMERA_MOTION = "application/x-camera-motion";
    public static final String APPLICATION_CEA608 = "application/cea-608";
    public static final String APPLICATION_CEA708 = "application/cea-708";

    @UnstableApi
    public static final String APPLICATION_DEPTH_METADATA = "application/x-depth-metadata";
    public static final String APPLICATION_DVBSUBS = "application/dvbsubs";

    @UnstableApi
    public static final String APPLICATION_EMSG = "application/x-emsg";

    @UnstableApi
    public static final String APPLICATION_EXIF = "application/x-exif";

    @UnstableApi
    public static final String APPLICATION_EXTERNALLY_LOADED_IMAGE = "application/x-image-uri";

    @UnstableApi
    public static final String APPLICATION_ICY = "application/x-icy";
    public static final String APPLICATION_ID3 = "application/id3";
    public static final String APPLICATION_M3U8 = "application/x-mpegURL";
    public static final String APPLICATION_MATROSKA = "application/x-matroska";

    @UnstableApi
    public static final String APPLICATION_MEDIA3_CUES = "application/x-media3-cues";
    public static final String APPLICATION_MP4 = "application/mp4";
    public static final String APPLICATION_MP4CEA608 = "application/x-mp4-cea-608";
    public static final String APPLICATION_MP4VTT = "application/x-mp4-vtt";
    public static final String APPLICATION_MPD = "application/dash+xml";
    public static final String APPLICATION_PGS = "application/pgs";

    @Deprecated
    public static final String APPLICATION_RAWCC = "application/x-rawcc";
    public static final String APPLICATION_RTSP = "application/x-rtsp";

    @UnstableApi
    public static final String APPLICATION_SCTE35 = "application/x-scte35";
    public static final String APPLICATION_SDP = "application/sdp";
    public static final String APPLICATION_SS = "application/vnd.ms-sstr+xml";
    public static final String APPLICATION_SUBRIP = "application/x-subrip";
    public static final String APPLICATION_TTML = "application/ttml+xml";
    public static final String APPLICATION_TX3G = "application/x-quicktime-tx3g";
    public static final String APPLICATION_VOBSUB = "application/vobsub";
    public static final String APPLICATION_WEBM = "application/webm";
    public static final String AUDIO_AAC = "audio/mp4a-latm";
    public static final String AUDIO_AC3 = "audio/ac3";
    public static final String AUDIO_AC4 = "audio/ac4";
    public static final String AUDIO_ALAC = "audio/alac";
    public static final String AUDIO_ALAW = "audio/g711-alaw";
    public static final String AUDIO_AMR = "audio/amr";
    public static final String AUDIO_AMR_NB = "audio/3gpp";
    public static final String AUDIO_AMR_WB = "audio/amr-wb";
    public static final String AUDIO_DTS = "audio/vnd.dts";
    public static final String AUDIO_DTS_EXPRESS = "audio/vnd.dts.hd;profile=lbr";
    public static final String AUDIO_DTS_HD = "audio/vnd.dts.hd";

    @UnstableApi
    public static final String AUDIO_DTS_X = "audio/vnd.dts.uhd;profile=p2";

    @UnstableApi
    public static final String AUDIO_EXOPLAYER_MIDI = "audio/x-exoplayer-midi";
    public static final String AUDIO_E_AC3 = "audio/eac3";
    public static final String AUDIO_E_AC3_JOC = "audio/eac3-joc";
    public static final String AUDIO_FLAC = "audio/flac";

    @UnstableApi
    public static final String AUDIO_IAMF = "audio/iamf";

    @UnstableApi
    public static final String AUDIO_MATROSKA = "audio/x-matroska";
    public static final String AUDIO_MIDI = "audio/midi";
    public static final String AUDIO_MLAW = "audio/g711-mlaw";
    public static final String AUDIO_MP4 = "audio/mp4";
    public static final String AUDIO_MPEG = "audio/mpeg";
    public static final String AUDIO_MPEGH_MHA1 = "audio/mha1";
    public static final String AUDIO_MPEGH_MHM1 = "audio/mhm1";
    public static final String AUDIO_MPEG_L1 = "audio/mpeg-L1";
    public static final String AUDIO_MPEG_L2 = "audio/mpeg-L2";
    public static final String AUDIO_MSGSM = "audio/gsm";
    public static final String AUDIO_OGG = "audio/ogg";
    public static final String AUDIO_OPUS = "audio/opus";
    public static final String AUDIO_RAW = "audio/raw";
    public static final String AUDIO_TRUEHD = "audio/true-hd";

    @UnstableApi
    public static final String AUDIO_UNKNOWN = "audio/x-unknown";
    public static final String AUDIO_VORBIS = "audio/vorbis";
    public static final String AUDIO_WAV = "audio/wav";
    public static final String AUDIO_WEBM = "audio/webm";

    @UnstableApi
    public static final String BASE_TYPE_APPLICATION = "application";

    @UnstableApi
    public static final String BASE_TYPE_AUDIO = "audio";

    @UnstableApi
    public static final String BASE_TYPE_IMAGE = "image";

    @UnstableApi
    public static final String BASE_TYPE_TEXT = "text";

    @UnstableApi
    public static final String BASE_TYPE_VIDEO = "video";

    @UnstableApi
    public static final String CODEC_E_AC3_JOC = "ec+3";

    @UnstableApi
    public static final String IMAGE_AVIF = "image/avif";

    @UnstableApi
    public static final String IMAGE_BMP = "image/bmp";

    @UnstableApi
    public static final String IMAGE_HEIC = "image/heic";

    @UnstableApi
    public static final String IMAGE_HEIF = "image/heif";
    public static final String IMAGE_JPEG = "image/jpeg";

    @UnstableApi
    public static final String IMAGE_JPEG_R = "image/jpeg_r";

    @UnstableApi
    public static final String IMAGE_PNG = "image/png";

    @UnstableApi
    public static final String IMAGE_RAW = "image/raw";

    @UnstableApi
    public static final String IMAGE_WEBP = "image/webp";
    public static final String TEXT_SSA = "text/x-ssa";

    @UnstableApi
    public static final String TEXT_UNKNOWN = "text/x-unknown";
    public static final String TEXT_VTT = "text/vtt";

    @UnstableApi
    public static final String VIDEO_APV = "video/apv";
    public static final String VIDEO_AV1 = "video/av01";
    public static final String VIDEO_AVI = "video/x-msvideo";
    public static final String VIDEO_DIVX = "video/divx";
    public static final String VIDEO_DOLBY_VISION = "video/dolby-vision";

    @UnstableApi
    public static final String VIDEO_FLV = "video/x-flv";
    public static final String VIDEO_H263 = "video/3gpp";
    public static final String VIDEO_H264 = "video/avc";
    public static final String VIDEO_H265 = "video/hevc";

    @UnstableApi
    public static final String VIDEO_MATROSKA = "video/x-matroska";
    public static final String VIDEO_MJPEG = "video/mjpeg";
    public static final String VIDEO_MP2T = "video/mp2t";
    public static final String VIDEO_MP4 = "video/mp4";
    public static final String VIDEO_MP42 = "video/mp42";
    public static final String VIDEO_MP43 = "video/mp43";
    public static final String VIDEO_MP4V = "video/mp4v-es";
    public static final String VIDEO_MPEG = "video/mpeg";
    public static final String VIDEO_MPEG2 = "video/mpeg2";

    @UnstableApi
    public static final String VIDEO_MV_HEVC = "video/mv-hevc";
    public static final String VIDEO_OGG = "video/ogg";
    public static final String VIDEO_PS = "video/mp2p";

    @UnstableApi
    public static final String VIDEO_RAW = "video/raw";

    @UnstableApi
    public static final String VIDEO_UNKNOWN = "video/x-unknown";
    public static final String VIDEO_VC1 = "video/wvc1";

    @UnstableApi
    public static final String VIDEO_VP8 = "video/x-vnd.on2.vp8";

    @UnstableApi
    public static final String VIDEO_VP9 = "video/x-vnd.on2.vp9";
    public static final String VIDEO_WEBM = "video/webm";
    private static final ArrayList<CustomMimeType> customMimeTypes = new ArrayList<>();
    private static final Pattern MP4A_RFC_6381_CODEC_PATTERN = Pattern.compile("^mp4a\\.([a-zA-Z0-9]{2})(?:\\.([0-9]{1,2}))?$");

    /* JADX INFO: loaded from: classes3.dex */
    public static final class CustomMimeType {
        public final String codecPrefix;
        public final String mimeType;
        public final int trackType;

        public CustomMimeType(String str, String str2, int i10) {
            this.mimeType = str;
            this.codecPrefix = str2;
            this.trackType = i10;
        }
    }

    public static final class Mp4aObjectType {
        public final int audioObjectTypeIndication;
        public final int objectTypeIndication;

        public Mp4aObjectType(int i10, int i11) {
            this.objectTypeIndication = i10;
            this.audioObjectTypeIndication = i11;
        }

        public int getEncoding() {
            int i10 = this.audioObjectTypeIndication;
            if (i10 == 2) {
                return 10;
            }
            if (i10 == 5) {
                return 11;
            }
            if (i10 == 29) {
                return 12;
            }
            if (i10 == 42) {
                return 16;
            }
            if (i10 != 22) {
                return i10 != 23 ? 0 : 15;
            }
            return 1073741824;
        }
    }

    private MimeTypes() {
    }

    @UnstableApi
    public static boolean allSamplesAreSyncSamples(String str, String str2) {
        Mp4aObjectType objectTypeFromMp4aRFC6381CodecString;
        int encoding;
        if (str == null) {
            return false;
        }
        switch (str) {
            case "audio/eac3-joc":
            case "audio/mpeg-L1":
            case "audio/mpeg-L2":
            case "audio/ac3":
            case "audio/raw":
            case "audio/eac3":
            case "audio/flac":
            case "audio/mpeg":
            case "audio/g711-alaw":
            case "audio/g711-mlaw":
                return true;
            case "audio/mp4a-latm":
                return (str2 == null || (objectTypeFromMp4aRFC6381CodecString = getObjectTypeFromMp4aRFC6381CodecString(str2)) == null || (encoding = objectTypeFromMp4aRFC6381CodecString.getEncoding()) == 0 || encoding == 16) ? false : true;
            default:
                return false;
        }
    }

    @UnstableApi
    public static boolean containsCodecsCorrespondingToMimeType(String str, String str2) {
        return getCodecsCorrespondingToMimeType(str, str2) != null;
    }

    @UnstableApi
    public static String getAudioMediaMimeType(String str) {
        if (str == null) {
            return null;
        }
        for (String str2 : Util.splitCodecs(str)) {
            String mediaMimeType = getMediaMimeType(str2);
            if (mediaMimeType != null && isAudio(mediaMimeType)) {
                return mediaMimeType;
            }
        }
        return null;
    }

    @UnstableApi
    public static String getCodecsCorrespondingToMimeType(String str, String str2) {
        if (str != null && str2 != null) {
            String[] strArrSplitCodecs = Util.splitCodecs(str);
            StringBuilder sb2 = new StringBuilder();
            for (String str3 : strArrSplitCodecs) {
                if (str2.equals(getMediaMimeType(str3))) {
                    if (sb2.length() > 0) {
                        sb2.append(",");
                    }
                    sb2.append(str3);
                }
            }
            if (sb2.length() > 0) {
                return sb2.toString();
            }
        }
        return null;
    }

    private static String getCustomMimeTypeForCodec(String str) {
        int size = customMimeTypes.size();
        for (int i10 = 0; i10 < size; i10++) {
            CustomMimeType customMimeType = customMimeTypes.get(i10);
            if (str.startsWith(customMimeType.codecPrefix)) {
                return customMimeType.mimeType;
            }
        }
        return null;
    }

    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    @UnstableApi
    public static int getEncoding(String str, String str2) {
        Mp4aObjectType objectTypeFromMp4aRFC6381CodecString;
        str.getClass();
        byte b5 = -1;
        switch (str.hashCode()) {
            case -2123537834:
                if (str.equals("audio/eac3-joc")) {
                    b5 = 0;
                }
                break;
            case -1365340241:
                if (str.equals("audio/vnd.dts.hd;profile=lbr")) {
                    b5 = 1;
                }
                break;
            case -1095064472:
                if (str.equals("audio/vnd.dts")) {
                    b5 = 2;
                }
                break;
            case -53558318:
                if (str.equals("audio/mp4a-latm")) {
                    b5 = 3;
                }
                break;
            case 187078296:
                if (str.equals("audio/ac3")) {
                    b5 = 4;
                }
                break;
            case 187078297:
                if (str.equals("audio/ac4")) {
                    b5 = 5;
                }
                break;
            case 550520934:
                if (str.equals("audio/vnd.dts.uhd;profile=p2")) {
                    b5 = 6;
                }
                break;
            case 1504578661:
                if (str.equals("audio/eac3")) {
                    b5 = 7;
                }
                break;
            case 1504831518:
                if (str.equals("audio/mpeg")) {
                    b5 = 8;
                }
                break;
            case 1504891608:
                if (str.equals("audio/opus")) {
                    b5 = 9;
                }
                break;
            case 1505942594:
                if (str.equals("audio/vnd.dts.hd")) {
                    b5 = 10;
                }
                break;
            case 1556697186:
                if (str.equals("audio/true-hd")) {
                    b5 = ServerMessageBlock.SMB_COM_WRITE;
                }
                break;
        }
        switch (b5) {
            case 0:
                return 18;
            case 1:
                return 8;
            case 2:
                return 7;
            case 3:
                if (str2 == null || (objectTypeFromMp4aRFC6381CodecString = getObjectTypeFromMp4aRFC6381CodecString(str2)) == null) {
                    return 0;
                }
                return objectTypeFromMp4aRFC6381CodecString.getEncoding();
            case 4:
                return 5;
            case 5:
                return 17;
            case 6:
                return 30;
            case 7:
                return 6;
            case 8:
                return 9;
            case 9:
                return 20;
            case 10:
                return 8;
            case 11:
                return 14;
            default:
                return 0;
        }
    }

    @UnstableApi
    public static String getMediaMimeType(String str) {
        Mp4aObjectType objectTypeFromMp4aRFC6381CodecString;
        String mimeTypeFromMp4ObjectType = null;
        if (str == null) {
            return null;
        }
        String strR0 = ge.r0(str.trim());
        if (strR0.startsWith("avc1") || strR0.startsWith("avc3")) {
            return "video/avc";
        }
        if (strR0.startsWith("hev1") || strR0.startsWith("hvc1")) {
            return "video/hevc";
        }
        if (strR0.startsWith("dvav") || strR0.startsWith("dva1") || strR0.startsWith("dvhe") || strR0.startsWith("dvh1")) {
            return "video/dolby-vision";
        }
        if (strR0.startsWith("av01")) {
            return "video/av01";
        }
        if (strR0.startsWith("vp9") || strR0.startsWith("vp09")) {
            return "video/x-vnd.on2.vp9";
        }
        if (strR0.startsWith("vp8") || strR0.startsWith("vp08")) {
            return "video/x-vnd.on2.vp8";
        }
        if (strR0.startsWith("mp4a")) {
            if (strR0.startsWith("mp4a.") && (objectTypeFromMp4aRFC6381CodecString = getObjectTypeFromMp4aRFC6381CodecString(strR0)) != null) {
                mimeTypeFromMp4ObjectType = getMimeTypeFromMp4ObjectType(objectTypeFromMp4aRFC6381CodecString.objectTypeIndication);
            }
            return mimeTypeFromMp4ObjectType == null ? "audio/mp4a-latm" : mimeTypeFromMp4ObjectType;
        }
        if (strR0.startsWith("mha1")) {
            return "audio/mha1";
        }
        if (strR0.startsWith("mhm1")) {
            return "audio/mhm1";
        }
        if (strR0.startsWith("ac-3") || strR0.startsWith("dac3")) {
            return "audio/ac3";
        }
        if (strR0.startsWith("ec-3") || strR0.startsWith("dec3")) {
            return "audio/eac3";
        }
        if (strR0.startsWith("ec+3")) {
            return "audio/eac3-joc";
        }
        if (strR0.startsWith("ac-4") || strR0.startsWith("dac4")) {
            return "audio/ac4";
        }
        if (strR0.startsWith("dtsc")) {
            return "audio/vnd.dts";
        }
        if (strR0.startsWith("dtse")) {
            return "audio/vnd.dts.hd;profile=lbr";
        }
        if (strR0.startsWith("dtsh") || strR0.startsWith("dtsl")) {
            return "audio/vnd.dts.hd";
        }
        if (strR0.startsWith("dtsx")) {
            return "audio/vnd.dts.uhd;profile=p2";
        }
        if (strR0.startsWith("opus")) {
            return "audio/opus";
        }
        if (strR0.startsWith("vorbis")) {
            return "audio/vorbis";
        }
        if (strR0.startsWith("flac")) {
            return "audio/flac";
        }
        if (strR0.startsWith("stpp")) {
            return "application/ttml+xml";
        }
        if (strR0.startsWith("wvtt")) {
            return "text/vtt";
        }
        if (strR0.contains("cea708")) {
            return "application/cea-708";
        }
        return (strR0.contains("eia608") || strR0.contains("cea608")) ? "application/cea-608" : getCustomMimeTypeForCodec(strR0);
    }

    @UnstableApi
    public static String getMimeTypeFromMp4ObjectType(int i10) {
        if (i10 == 32) {
            return "video/mp4v-es";
        }
        if (i10 == 33) {
            return "video/avc";
        }
        if (i10 == 35) {
            return "video/hevc";
        }
        if (i10 == 64) {
            return "audio/mp4a-latm";
        }
        if (i10 == 163) {
            return "video/wvc1";
        }
        if (i10 == 177) {
            return "video/x-vnd.on2.vp9";
        }
        if (i10 == 221) {
            return "audio/vorbis";
        }
        if (i10 == 165) {
            return "audio/ac3";
        }
        if (i10 == 166) {
            return "audio/eac3";
        }
        switch (i10) {
            case 96:
            case 97:
            case 98:
            case V8Value.UNDEFINED /* 99 */:
            case 100:
            case HttpStatusCodesKt.HTTP_SWITCHING_PROTOCOLS /* 101 */:
                return "video/mpeg2";
            case HttpStatusCodesKt.HTTP_PROCESSING /* 102 */:
            case HttpStatusCodesKt.HTTP_EARLY_HINTS /* 103 */:
            case 104:
                return "audio/mp4a-latm";
            case 105:
            case 107:
                return "audio/mpeg";
            case 106:
                return "video/mpeg";
            case 108:
                return "image/jpeg";
            default:
                switch (i10) {
                    case 169:
                    case TsExtractor.TS_STREAM_TYPE_AC4 /* 172 */:
                        return "audio/vnd.dts";
                    case 170:
                    case 171:
                        return "audio/vnd.dts.hd";
                    case 173:
                        return "audio/opus";
                    case 174:
                        return "audio/ac4";
                    default:
                        return null;
                }
        }
    }

    @UnstableApi
    public static Byte getMp4ObjectTypeFromMimeType(String str) {
        str.getClass();
        switch (str) {
            case "audio/vorbis":
                return (byte) -35;
            case "audio/mp4a-latm":
                return (byte) 64;
            case "video/mp4v-es":
                return (byte) 32;
            default:
                return null;
        }
    }

    public static Mp4aObjectType getObjectTypeFromMp4aRFC6381CodecString(String str) {
        Matcher matcher = MP4A_RFC_6381_CODEC_PATTERN.matcher(str);
        if (!matcher.matches()) {
            return null;
        }
        String str2 = (String) Assertions.checkNotNull(matcher.group(1));
        String strGroup = matcher.group(2);
        try {
            return new Mp4aObjectType(Integer.parseInt(str2, 16), strGroup != null ? Integer.parseInt(strGroup) : 0);
        } catch (NumberFormatException unused) {
            return null;
        }
    }

    @UnstableApi
    public static String getTextMediaMimeType(String str) {
        if (str == null) {
            return null;
        }
        for (String str2 : Util.splitCodecs(str)) {
            String mediaMimeType = getMediaMimeType(str2);
            if (mediaMimeType != null && isText(mediaMimeType)) {
                return mediaMimeType;
            }
        }
        return null;
    }

    @UnstableApi
    private static String getTopLevelType(String str) {
        int iIndexOf;
        if (str == null || (iIndexOf = str.indexOf(47)) == -1) {
            return null;
        }
        return str.substring(0, iIndexOf);
    }

    @UnstableApi
    public static int getTrackType(String str) {
        if (TextUtils.isEmpty(str)) {
            return -1;
        }
        if (isAudio(str)) {
            return 1;
        }
        if (isVideo(str)) {
            return 2;
        }
        if (isText(str)) {
            return 3;
        }
        if (isImage(str)) {
            return 4;
        }
        if ("application/id3".equals(str) || "application/x-emsg".equals(str) || "application/x-scte35".equals(str) || "application/x-icy".equals(str) || "application/vnd.dvb.ait".equals(str)) {
            return 5;
        }
        if ("application/x-camera-motion".equals(str)) {
            return 6;
        }
        return getTrackTypeForCustomMimeType(str);
    }

    private static int getTrackTypeForCustomMimeType(String str) {
        int size = customMimeTypes.size();
        for (int i10 = 0; i10 < size; i10++) {
            CustomMimeType customMimeType = customMimeTypes.get(i10);
            if (str.equals(customMimeType.mimeType)) {
                return customMimeType.trackType;
            }
        }
        return -1;
    }

    @UnstableApi
    public static int getTrackTypeOfCodec(String str) {
        return getTrackType(getMediaMimeType(str));
    }

    @UnstableApi
    public static String getVideoMediaMimeType(String str) {
        if (str == null) {
            return null;
        }
        for (String str2 : Util.splitCodecs(str)) {
            String mediaMimeType = getMediaMimeType(str2);
            if (mediaMimeType != null && isVideo(mediaMimeType)) {
                return mediaMimeType;
            }
        }
        return null;
    }

    @UnstableApi
    public static boolean isAudio(String str) {
        return "audio".equals(getTopLevelType(str));
    }

    @UnstableApi
    public static boolean isDolbyVisionCodec(String str, String str2) {
        if (str == null) {
            return false;
        }
        if (str.startsWith("dvhe") || str.startsWith("dvh1")) {
            return true;
        }
        if (str2 == null) {
            return false;
        }
        return (str2.startsWith("dvhe") && str.startsWith("hev1")) || (str2.startsWith("dvh1") && str.startsWith("hvc1")) || ((str2.startsWith("dvav") && str.startsWith("avc3")) || ((str2.startsWith("dva1") && str.startsWith("avc1")) || (str2.startsWith("dav1") && str.startsWith("av01"))));
    }

    @UnstableApi
    public static boolean isImage(String str) {
        return "image".equals(getTopLevelType(str)) || "application/x-image-uri".equals(str);
    }

    @UnstableApi
    public static boolean isMatroska(String str) {
        if (str == null) {
            return false;
        }
        return str.startsWith("video/webm") || str.startsWith("audio/webm") || str.startsWith("application/webm") || str.startsWith("video/x-matroska") || str.startsWith("audio/x-matroska") || str.startsWith("application/x-matroska");
    }

    @UnstableApi
    public static boolean isText(String str) {
        return "text".equals(getTopLevelType(str)) || "application/x-media3-cues".equals(str) || "application/cea-608".equals(str) || "application/cea-708".equals(str) || "application/x-mp4-cea-608".equals(str) || "application/x-subrip".equals(str) || "application/ttml+xml".equals(str) || "application/x-quicktime-tx3g".equals(str) || "application/x-mp4-vtt".equals(str) || APPLICATION_RAWCC.equals(str) || "application/vobsub".equals(str) || "application/pgs".equals(str) || "application/dvbsubs".equals(str);
    }

    @UnstableApi
    public static boolean isVideo(String str) {
        return "video".equals(getTopLevelType(str));
    }

    @UnstableApi
    public static String normalizeMimeType(String str) {
        if (str == null) {
            return null;
        }
        String strR0 = ge.r0(str);
        strR0.getClass();
        switch (strR0) {
            case "video/x-mvhevc":
                return "video/mv-hevc";
            case "audio/x-flac":
                return "audio/flac";
            case "application/x-mpegurl":
                return "application/x-mpegURL";
            case "audio/x-wav":
                return "audio/wav";
            case "audio/mpeg-l1":
                return "audio/mpeg-L1";
            case "audio/mpeg-l2":
                return "audio/mpeg-L2";
            case "audio/mp3":
                return "audio/mpeg";
            default:
                return strR0;
        }
    }

    @UnstableApi
    public static void registerCustomMimeType(String str, String str2, int i10) {
        CustomMimeType customMimeType = new CustomMimeType(str, str2, i10);
        int size = customMimeTypes.size();
        for (int i11 = 0; i11 < size; i11++) {
            ArrayList<CustomMimeType> arrayList = customMimeTypes;
            if (str.equals(arrayList.get(i11).mimeType)) {
                arrayList.remove(i11);
                break;
            }
        }
        customMimeTypes.add(customMimeType);
    }
}
