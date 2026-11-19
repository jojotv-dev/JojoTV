package jcifs;

/* JADX INFO: loaded from: classes5.dex */
public interface FileNotifyInformation {
    public static final int FILE_ACTION_ADDED = 1;
    public static final int FILE_ACTION_ADDED_STREAM = 6;
    public static final int FILE_ACTION_MODIFIED = 3;
    public static final int FILE_ACTION_MODIFIED_STREAM = 8;
    public static final int FILE_ACTION_REMOVED = 2;
    public static final int FILE_ACTION_REMOVED_BY_DELETE = 9;
    public static final int FILE_ACTION_REMOVED_STREAM = 7;
    public static final int FILE_ACTION_RENAMED_NEW_NAME = 5;
    public static final int FILE_ACTION_RENAMED_OLD_NAME = 4;
    public static final int FILE_NOTIFY_CHANGE_ATTRIBUTES = 4;
    public static final int FILE_NOTIFY_CHANGE_CREATION = 64;
    public static final int FILE_NOTIFY_CHANGE_DIR_NAME = 2;
    public static final int FILE_NOTIFY_CHANGE_EA = 128;
    public static final int FILE_NOTIFY_CHANGE_FILE_NAME = 1;
    public static final int FILE_NOTIFY_CHANGE_LAST_ACCESS = 32;
    public static final int FILE_NOTIFY_CHANGE_LAST_WRITE = 16;
    public static final int FILE_NOTIFY_CHANGE_NAME = 3;
    public static final int FILE_NOTIFY_CHANGE_SECURITY = 256;
    public static final int FILE_NOTIFY_CHANGE_SIZE = 8;
    public static final int FILE_NOTIFY_CHANGE_STREAM_NAME = 512;
    public static final int FILE_NOTIFY_CHANGE_STREAM_SIZE = 1024;
    public static final int FILE_NOTIFY_CHANGE_STREAM_WRITE = 2048;

    int getAction();

    String getFileName();
}
