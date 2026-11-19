package jcifs.util.transport;

/* JADX INFO: loaded from: classes5.dex */
public interface Request extends Message {
    int getCreditCost();

    Request getNext();

    Response getResponse();

    boolean isCancel();

    void setRequestCredits(int i10);
}
