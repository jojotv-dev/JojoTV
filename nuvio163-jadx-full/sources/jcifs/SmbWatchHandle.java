package jcifs;

import java.util.List;
import java.util.concurrent.Callable;

/* JADX INFO: loaded from: classes5.dex */
public interface SmbWatchHandle extends AutoCloseable, Callable<List<FileNotifyInformation>> {
    @Override // java.util.concurrent.Callable
    List<FileNotifyInformation> call();

    @Override // java.lang.AutoCloseable
    void close();

    List<FileNotifyInformation> watch();
}
