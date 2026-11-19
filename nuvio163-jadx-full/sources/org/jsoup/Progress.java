package org.jsoup;

/* JADX INFO: loaded from: classes5.dex */
@FunctionalInterface
public interface Progress<ProgressContext> {
    void onProgress(int i10, int i11, float f10, ProgressContext progresscontext);
}
