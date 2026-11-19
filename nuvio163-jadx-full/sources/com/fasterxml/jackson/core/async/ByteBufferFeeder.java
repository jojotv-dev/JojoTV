package com.fasterxml.jackson.core.async;

import java.nio.ByteBuffer;

/* JADX INFO: loaded from: classes3.dex */
public interface ByteBufferFeeder extends NonBlockingInputFeeder {
    void feedInput(ByteBuffer byteBuffer);
}
