package jcifs.smb;

/* JADX INFO: loaded from: classes5.dex */
public interface FileEntry {
    long createTime();

    int getAttributes();

    int getFileIndex();

    String getName();

    int getType();

    long lastAccess();

    long lastModified();

    long length();
}
