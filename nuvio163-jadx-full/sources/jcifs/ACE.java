package jcifs;

/* JADX INFO: loaded from: classes5.dex */
public interface ACE {
    public static final int DELETE = 65536;
    public static final int FILE_APPEND_DATA = 4;
    public static final int FILE_DELETE = 64;
    public static final int FILE_EXECUTE = 32;
    public static final int FILE_READ_ATTRIBUTES = 128;
    public static final int FILE_READ_DATA = 1;
    public static final int FILE_READ_EA = 8;
    public static final int FILE_WRITE_ATTRIBUTES = 256;
    public static final int FILE_WRITE_DATA = 2;
    public static final int FILE_WRITE_EA = 16;
    public static final int FLAGS_CONTAINER_INHERIT = 2;
    public static final int FLAGS_INHERITED = 16;
    public static final int FLAGS_INHERIT_ONLY = 8;
    public static final int FLAGS_NO_PROPAGATE = 4;
    public static final int FLAGS_OBJECT_INHERIT = 1;
    public static final int GENERIC_ALL = 268435456;
    public static final int GENERIC_EXECUTE = 536870912;
    public static final int GENERIC_READ = Integer.MIN_VALUE;
    public static final int GENERIC_WRITE = 1073741824;
    public static final int READ_CONTROL = 131072;
    public static final int SYNCHRONIZE = 1048576;
    public static final int WRITE_DAC = 262144;
    public static final int WRITE_OWNER = 524288;

    int getAccessMask();

    String getApplyToText();

    int getFlags();

    SID getSID();

    boolean isAllow();

    boolean isInherited();
}
