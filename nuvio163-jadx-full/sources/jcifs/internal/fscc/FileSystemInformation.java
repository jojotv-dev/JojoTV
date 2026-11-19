package jcifs.internal.fscc;

import jcifs.Decodable;

/* JADX INFO: loaded from: classes5.dex */
public interface FileSystemInformation extends Decodable {
    public static final byte FS_FULL_SIZE_INFO = 7;
    public static final byte FS_SIZE_INFO = 3;
    public static final byte SMB_INFO_ALLOCATION = -1;

    byte getFileSystemInformationClass();
}
