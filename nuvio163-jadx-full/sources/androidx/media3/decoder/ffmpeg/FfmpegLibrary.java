package androidx.media3.decoder.ffmpeg;

import androidx.media3.common.MediaLibraryInfo;
import androidx.media3.common.util.LibraryLoader;
import androidx.media3.common.util.Log;
import androidx.media3.common.util.UnstableApi;
import jcifs.internal.smb1.ServerMessageBlock;
import jcifs.internal.smb2.info.Smb2QueryDirectoryRequest;

/* JADX INFO: loaded from: classes.dex */
@UnstableApi
public final class FfmpegLibrary {
    private static final LibraryLoader LOADER;
    private static final String TAG = "FfmpegLibrary";
    private static int inputBufferPaddingSize;
    private static String version;

    static {
        MediaLibraryInfo.registerModule("media3.decoder.ffmpeg");
        LOADER = new LibraryLoader("ffmpegJNI") { // from class: androidx.media3.decoder.ffmpeg.FfmpegLibrary.1
            @Override // androidx.media3.common.util.LibraryLoader
            public void loadLibrary(String str) {
                System.loadLibrary(str);
            }
        };
        inputBufferPaddingSize = -1;
    }

    private FfmpegLibrary() {
    }

    private static native int ffmpegGetInputBufferPaddingSize();

    private static native String ffmpegGetVersion();

    private static native boolean ffmpegHasDecoder(String str);

    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    public static String getCodecName(String str) {
        str.getClass();
        byte b5 = -1;
        switch (str.hashCode()) {
            case -2123537834:
                if (str.equals("audio/eac3-joc")) {
                    b5 = 0;
                }
                break;
            case -1662541442:
                if (str.equals("video/hevc")) {
                    b5 = 1;
                }
                break;
            case -1606874997:
                if (str.equals("audio/amr-wb")) {
                    b5 = 2;
                }
                break;
            case -1095064472:
                if (str.equals("audio/vnd.dts")) {
                    b5 = 3;
                }
                break;
            case -1003765268:
                if (str.equals("audio/vorbis")) {
                    b5 = 4;
                }
                break;
            case -432837260:
                if (str.equals("audio/mpeg-L1")) {
                    b5 = 5;
                }
                break;
            case -432837259:
                if (str.equals("audio/mpeg-L2")) {
                    b5 = 6;
                }
                break;
            case -53558318:
                if (str.equals("audio/mp4a-latm")) {
                    b5 = 7;
                }
                break;
            case 187078296:
                if (str.equals("audio/ac3")) {
                    b5 = 8;
                }
                break;
            case 1331836730:
                if (str.equals("video/avc")) {
                    b5 = 9;
                }
                break;
            case 1503095341:
                if (str.equals("audio/3gpp")) {
                    b5 = 10;
                }
                break;
            case 1504470054:
                if (str.equals("audio/alac")) {
                    b5 = ServerMessageBlock.SMB_COM_WRITE;
                }
                break;
            case 1504578661:
                if (str.equals("audio/eac3")) {
                    b5 = Smb2QueryDirectoryRequest.FILE_NAMES_INFO;
                }
                break;
            case 1504619009:
                if (str.equals("audio/flac")) {
                    b5 = 13;
                }
                break;
            case 1504831518:
                if (str.equals("audio/mpeg")) {
                    b5 = 14;
                }
                break;
            case 1504891608:
                if (str.equals("audio/opus")) {
                    b5 = 15;
                }
                break;
            case 1505942594:
                if (str.equals("audio/vnd.dts.hd")) {
                    b5 = 16;
                }
                break;
            case 1556697186:
                if (str.equals("audio/true-hd")) {
                    b5 = 17;
                }
                break;
            case 1903231877:
                if (str.equals("audio/g711-alaw")) {
                    b5 = ServerMessageBlock.SMB_COM_SEEK;
                }
                break;
            case 1903589369:
                if (str.equals("audio/g711-mlaw")) {
                    b5 = 19;
                }
                break;
        }
        switch (b5) {
            case 0:
            case 12:
                return "eac3";
            case 1:
                return "hevc";
            case 2:
                return "amrwb";
            case 3:
            case 16:
                return "dca";
            case 4:
                return "vorbis";
            case 5:
            case 6:
            case 14:
                return "mp3";
            case 7:
                return "aac";
            case 8:
                return "ac3";
            case 9:
                return "h264";
            case 10:
                return "amrnb";
            case 11:
                return "alac";
            case 13:
                return "flac";
            case 15:
                return "opus";
            case 17:
                return "truehd";
            case 18:
                return "pcm_alaw";
            case 19:
                return "pcm_mulaw";
            default:
                return null;
        }
    }

    public static int getInputBufferPaddingSize() {
        if (!isAvailable()) {
            return -1;
        }
        if (inputBufferPaddingSize == -1) {
            inputBufferPaddingSize = ffmpegGetInputBufferPaddingSize();
        }
        return inputBufferPaddingSize;
    }

    public static String getVersion() {
        if (!isAvailable()) {
            return null;
        }
        if (version == null) {
            version = ffmpegGetVersion();
        }
        return version;
    }

    public static boolean isAvailable() {
        return LOADER.isAvailable();
    }

    public static void setLibraries(String... strArr) {
        LOADER.setLibraries(strArr);
    }

    public static boolean supportsFormat(String str) {
        String codecName;
        if (!isAvailable() || (codecName = getCodecName(str)) == null) {
            return false;
        }
        if (ffmpegHasDecoder(codecName)) {
            return true;
        }
        Log.w(TAG, "No " + codecName + " decoder available. Check the FFmpeg build configuration.");
        return false;
    }
}
