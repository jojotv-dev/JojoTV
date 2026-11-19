package com.fasterxml.jackson.core.util;

import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.exc.StreamConstraintsException;

/* JADX INFO: loaded from: classes3.dex */
public final class ReadConstrainedTextBuffer extends TextBuffer {
    private final StreamReadConstraints _streamReadConstraints;

    public ReadConstrainedTextBuffer(StreamReadConstraints streamReadConstraints, BufferRecycler bufferRecycler) {
        super(bufferRecycler);
        this._streamReadConstraints = streamReadConstraints;
    }

    @Override // com.fasterxml.jackson.core.util.TextBuffer
    public void validateStringLength(int i10) throws StreamConstraintsException {
        this._streamReadConstraints.validateStringLength(i10);
    }
}
