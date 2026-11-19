package jcifs.internal;

/* JADX INFO: loaded from: classes5.dex */
public interface TreeConnectResponse extends CommonServerMessageBlockResponse {
    String getService();

    int getTid();

    boolean isShareDfs();

    boolean isValidTid();
}
