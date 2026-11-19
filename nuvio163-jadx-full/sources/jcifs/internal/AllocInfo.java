package jcifs.internal;

import jcifs.internal.fscc.FileSystemInformation;

/* JADX INFO: loaded from: classes5.dex */
public interface AllocInfo extends FileSystemInformation {
    long getCapacity();

    long getFree();
}
