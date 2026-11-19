package jcifs;

/* JADX INFO: loaded from: classes5.dex */
public interface NetbiosAddress extends Address {
    byte[] getMacAddress(CIFSContext cIFSContext);

    NetbiosName getName();

    int getNameType();

    int getNodeType(CIFSContext cIFSContext);

    boolean isActive(CIFSContext cIFSContext);

    boolean isBeingDeleted(CIFSContext cIFSContext);

    boolean isGroupAddress(CIFSContext cIFSContext);

    boolean isInConflict(CIFSContext cIFSContext);

    boolean isPermanent(CIFSContext cIFSContext);
}
