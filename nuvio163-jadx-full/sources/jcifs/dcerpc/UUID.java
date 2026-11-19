package jcifs.dcerpc;

import androidx.media3.container.MdtaMetadataEntry;
import androidx.media3.container.NalUnitUtil;
import com.eclipsesource.v8.V8Value;
import com.fasterxml.jackson.core.io.doubleparser.FastDoubleMath;
import is.xyz.mpv.MPV;
import okhttp3.internal.http.HttpStatusCodesKt;

/* JADX INFO: loaded from: classes5.dex */
public class UUID extends rpc.uuid_t {
    static final char[] HEXCHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public UUID(rpc.uuid_t uuid_tVar) {
        this.time_low = uuid_tVar.time_low;
        this.time_mid = uuid_tVar.time_mid;
        this.time_hi_and_version = uuid_tVar.time_hi_and_version;
        this.clock_seq_hi_and_reserved = uuid_tVar.clock_seq_hi_and_reserved;
        this.clock_seq_low = uuid_tVar.clock_seq_low;
        this.node = new byte[]{bArr[0], bArr[1], bArr[2], bArr[3], bArr[4], bArr[5]};
        byte[] bArr = uuid_tVar.node;
    }

    private static byte B(int i10) {
        return (byte) (i10 & 255);
    }

    private static short S(int i10) {
        return (short) (i10 & 65535);
    }

    private static String bin_to_hex(int i10, int i11) {
        char[] cArr = new char[i11];
        while (true) {
            int i12 = i11 - 1;
            if (i11 <= 0) {
                return new String(cArr);
            }
            cArr[i12] = HEXCHARS[i10 & 15];
            i10 >>>= 4;
            i11 = i12;
        }
    }

    private static int hex_to_bin(char[] cArr, int i10, int i11) {
        int i12;
        int i13 = 0;
        int i14 = 0;
        for (int i15 = i10; i15 < cArr.length && i13 < i11; i15++) {
            int i16 = i14 << 4;
            char c5 = cArr[i15];
            switch (c5) {
                case NalUnitUtil.H265_NAL_UNIT_TYPE_UNSPECIFIED /* 48 */:
                case '1':
                case '2':
                case '3':
                case '4':
                case FastDoubleMath.DOUBLE_SIGNIFICAND_WIDTH /* 53 */:
                case '6':
                case '7':
                case '8':
                case '9':
                    i12 = c5 - '0';
                    break;
                default:
                    switch (c5) {
                        case 'A':
                        case 'B':
                        case MdtaMetadataEntry.TYPE_INDICATOR_INT32 /* 67 */:
                        case 'D':
                        case 'E':
                        case MPV.mpvLogLevel.MPV_LOG_LEVEL_TRACE /* 70 */:
                            i12 = c5 - '7';
                            break;
                        default:
                            switch (c5) {
                                case 'a':
                                case 'b':
                                case V8Value.UNDEFINED /* 99 */:
                                case 'd':
                                case HttpStatusCodesKt.HTTP_SWITCHING_PROTOCOLS /* 101 */:
                                case HttpStatusCodesKt.HTTP_PROCESSING /* 102 */:
                                    i12 = c5 - 'W';
                                    break;
                                default:
                                    throw new IllegalArgumentException(new String(cArr, i10, i11));
                            }
                            break;
                    }
                    break;
            }
            i14 = i12 + i16;
            i13++;
        }
        return i14;
    }

    public String toString() {
        return bin_to_hex(this.time_low, 8) + '-' + bin_to_hex(this.time_mid, 4) + '-' + bin_to_hex(this.time_hi_and_version, 4) + '-' + bin_to_hex(this.clock_seq_hi_and_reserved, 2) + bin_to_hex(this.clock_seq_low, 2) + '-' + bin_to_hex(this.node[0], 2) + bin_to_hex(this.node[1], 2) + bin_to_hex(this.node[2], 2) + bin_to_hex(this.node[3], 2) + bin_to_hex(this.node[4], 2) + bin_to_hex(this.node[5], 2);
    }

    public UUID(String str) {
        char[] charArray = str.toCharArray();
        this.time_low = hex_to_bin(charArray, 0, 8);
        this.time_mid = S(hex_to_bin(charArray, 9, 4));
        this.time_hi_and_version = S(hex_to_bin(charArray, 14, 4));
        this.clock_seq_hi_and_reserved = B(hex_to_bin(charArray, 19, 2));
        this.clock_seq_low = B(hex_to_bin(charArray, 21, 2));
        byte[] bArr = new byte[6];
        this.node = bArr;
        bArr[0] = B(hex_to_bin(charArray, 24, 2));
        this.node[1] = B(hex_to_bin(charArray, 26, 2));
        this.node[2] = B(hex_to_bin(charArray, 28, 2));
        this.node[3] = B(hex_to_bin(charArray, 30, 2));
        this.node[4] = B(hex_to_bin(charArray, 32, 2));
        this.node[5] = B(hex_to_bin(charArray, 34, 2));
    }
}
