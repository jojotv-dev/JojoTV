package jcifs;

/* JADX INFO: loaded from: classes5.dex */
public interface SmbPipeResource extends SmbResource {
    public static final int PIPE_TYPE_CALL = 256;
    public static final int PIPE_TYPE_DCE_TRANSACT = 1536;
    public static final int PIPE_TYPE_RDONLY = 1;
    public static final int PIPE_TYPE_RDWR = 3;
    public static final int PIPE_TYPE_TRANSACT = 512;
    public static final int PIPE_TYPE_UNSHARED = 2048;
    public static final int PIPE_TYPE_WRONLY = 2;

    int getPipeType();

    SmbPipeHandle openPipe();
}
