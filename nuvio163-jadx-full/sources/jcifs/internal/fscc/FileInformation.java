package jcifs.internal.fscc;

import jcifs.Decodable;
import jcifs.Encodable;

/* JADX INFO: loaded from: classes5.dex */
public interface FileInformation extends Decodable, Encodable {
    public static final byte FILE_BASIC_INFO = 4;
    public static final byte FILE_ENDOFFILE_INFO = 20;
    public static final byte FILE_INTERNAL_INFO = 6;
    public static final byte FILE_RENAME_INFO = 10;
    public static final byte FILE_STANDARD_INFO = 5;

    byte getFileInformationLevel();
}
