package jcifs.internal.smb2;

/* JADX INFO: loaded from: classes5.dex */
public final class Smb2Constants {
    public static final byte SMB2_0_INFO_FILE = 1;
    public static final byte SMB2_0_INFO_FILESYSTEM = 2;
    public static final byte SMB2_0_INFO_QUOTA = 4;
    public static final byte SMB2_0_INFO_SECURITY = 3;
    public static final int SMB2_DIALECT_0202 = 514;
    public static final int SMB2_DIALECT_0210 = 528;
    public static final int SMB2_DIALECT_0300 = 768;
    public static final int SMB2_DIALECT_0302 = 770;
    public static final int SMB2_DIALECT_0311 = 785;
    public static final int SMB2_DIALECT_ANY = 767;
    public static final int SMB2_GLOBAL_CAP_DFS = 1;
    public static final int SMB2_GLOBAL_CAP_DIRECTORY_LEASING = 32;
    public static final int SMB2_GLOBAL_CAP_ENCRYPTION = 64;
    public static final int SMB2_GLOBAL_CAP_LARGE_MTU = 4;
    public static final int SMB2_GLOBAL_CAP_LEASING = 2;
    public static final int SMB2_GLOBAL_CAP_MULTI_CHANNEL = 8;
    public static final int SMB2_GLOBAL_CAP_PERSISTENT_HANDLES = 16;
    public static final int SMB2_HEADER_LENGTH = 64;
    public static final int SMB2_NEGOTIATE_SIGNING_ENABLED = 1;
    public static final int SMB2_NEGOTIATE_SIGNING_REQUIRED = 2;
    public static final byte[] UNSPECIFIED_FILEID = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    public static final long UNSPECIFIED_SESSIONID = -1;
    public static final int UNSPECIFIED_TREEID = -1;

    private Smb2Constants() {
    }
}
