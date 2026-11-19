package com.fasterxml.jackson.core.io;

import com.fasterxml.jackson.core.ErrorReportConfiguration;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.StreamWriteConstraints;
import com.fasterxml.jackson.core.util.BufferRecycler;
import com.fasterxml.jackson.core.util.ReadConstrainedTextBuffer;
import com.fasterxml.jackson.core.util.TextBuffer;

/* JADX INFO: loaded from: classes3.dex */
public class IOContext implements AutoCloseable {
    protected byte[] _base64Buffer;
    protected final BufferRecycler _bufferRecycler;
    private boolean _closed;
    protected char[] _concatCBuffer;
    protected final ContentReference _contentReference;
    protected JsonEncoding _encoding;
    protected final ErrorReportConfiguration _errorReportConfiguration;
    protected final boolean _managedResource;
    protected char[] _nameCopyBuffer;
    protected byte[] _readIOBuffer;
    protected boolean _releaseRecycler;

    @Deprecated
    protected final Object _sourceRef;
    protected final StreamReadConstraints _streamReadConstraints;
    protected final StreamWriteConstraints _streamWriteConstraints;
    protected char[] _tokenCBuffer;
    protected byte[] _writeEncodingBuffer;

    public IOContext(StreamReadConstraints streamReadConstraints, StreamWriteConstraints streamWriteConstraints, ErrorReportConfiguration errorReportConfiguration, BufferRecycler bufferRecycler, ContentReference contentReference, boolean z2) {
        this._releaseRecycler = true;
        this._closed = false;
        this._streamReadConstraints = streamReadConstraints;
        this._streamWriteConstraints = streamWriteConstraints;
        this._errorReportConfiguration = errorReportConfiguration;
        this._bufferRecycler = bufferRecycler;
        this._contentReference = contentReference;
        this._sourceRef = contentReference.getRawContent();
        this._managedResource = z2;
    }

    private IllegalArgumentException wrongBuf() {
        return new IllegalArgumentException("Trying to release buffer smaller than original");
    }

    public final void _verifyAlloc(Object obj) {
        if (obj != null) {
            throw new IllegalStateException("Trying to call same allocXxx() method second time");
        }
    }

    public final void _verifyRelease(byte[] bArr, byte[] bArr2) {
        if (bArr != bArr2 && bArr.length < bArr2.length) {
            throw wrongBuf();
        }
    }

    public byte[] allocBase64Buffer() {
        _verifyAlloc(this._base64Buffer);
        byte[] bArrAllocByteBuffer = this._bufferRecycler.allocByteBuffer(3);
        this._base64Buffer = bArrAllocByteBuffer;
        return bArrAllocByteBuffer;
    }

    public char[] allocConcatBuffer() {
        _verifyAlloc(this._concatCBuffer);
        char[] cArrAllocCharBuffer = this._bufferRecycler.allocCharBuffer(1);
        this._concatCBuffer = cArrAllocCharBuffer;
        return cArrAllocCharBuffer;
    }

    public char[] allocNameCopyBuffer(int i10) {
        _verifyAlloc(this._nameCopyBuffer);
        char[] cArrAllocCharBuffer = this._bufferRecycler.allocCharBuffer(3, i10);
        this._nameCopyBuffer = cArrAllocCharBuffer;
        return cArrAllocCharBuffer;
    }

    public byte[] allocReadIOBuffer() {
        _verifyAlloc(this._readIOBuffer);
        byte[] bArrAllocByteBuffer = this._bufferRecycler.allocByteBuffer(0);
        this._readIOBuffer = bArrAllocByteBuffer;
        return bArrAllocByteBuffer;
    }

    public char[] allocTokenBuffer() {
        _verifyAlloc(this._tokenCBuffer);
        char[] cArrAllocCharBuffer = this._bufferRecycler.allocCharBuffer(0);
        this._tokenCBuffer = cArrAllocCharBuffer;
        return cArrAllocCharBuffer;
    }

    public byte[] allocWriteEncodingBuffer() {
        _verifyAlloc(this._writeEncodingBuffer);
        byte[] bArrAllocByteBuffer = this._bufferRecycler.allocByteBuffer(1);
        this._writeEncodingBuffer = bArrAllocByteBuffer;
        return bArrAllocByteBuffer;
    }

    public BufferRecycler bufferRecycler() {
        return this._bufferRecycler;
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        if (this._closed) {
            return;
        }
        this._closed = true;
        if (this._releaseRecycler) {
            this._releaseRecycler = false;
            this._bufferRecycler.releaseToPool();
        }
    }

    public TextBuffer constructReadConstrainedTextBuffer() {
        return new ReadConstrainedTextBuffer(this._streamReadConstraints, this._bufferRecycler);
    }

    public TextBuffer constructTextBuffer() {
        return new TextBuffer(this._bufferRecycler);
    }

    public ContentReference contentReference() {
        return this._contentReference;
    }

    public ErrorReportConfiguration errorReportConfiguration() {
        return this._errorReportConfiguration;
    }

    public JsonEncoding getEncoding() {
        return this._encoding;
    }

    @Deprecated
    public Object getSourceReference() {
        return this._sourceRef;
    }

    public boolean isResourceManaged() {
        return this._managedResource;
    }

    public IOContext markBufferRecyclerReleased() {
        this._releaseRecycler = false;
        return this;
    }

    public void releaseBase64Buffer(byte[] bArr) {
        if (bArr != null) {
            _verifyRelease(bArr, this._base64Buffer);
            this._base64Buffer = null;
            this._bufferRecycler.releaseByteBuffer(3, bArr);
        }
    }

    public void releaseConcatBuffer(char[] cArr) {
        if (cArr != null) {
            _verifyRelease(cArr, this._concatCBuffer);
            this._concatCBuffer = null;
            this._bufferRecycler.releaseCharBuffer(1, cArr);
        }
    }

    public void releaseNameCopyBuffer(char[] cArr) {
        if (cArr != null) {
            _verifyRelease(cArr, this._nameCopyBuffer);
            this._nameCopyBuffer = null;
            this._bufferRecycler.releaseCharBuffer(3, cArr);
        }
    }

    public void releaseReadIOBuffer(byte[] bArr) {
        if (bArr != null) {
            _verifyRelease(bArr, this._readIOBuffer);
            this._readIOBuffer = null;
            this._bufferRecycler.releaseByteBuffer(0, bArr);
        }
    }

    public void releaseTokenBuffer(char[] cArr) {
        if (cArr != null) {
            _verifyRelease(cArr, this._tokenCBuffer);
            this._tokenCBuffer = null;
            this._bufferRecycler.releaseCharBuffer(0, cArr);
        }
    }

    public void releaseWriteEncodingBuffer(byte[] bArr) {
        if (bArr != null) {
            _verifyRelease(bArr, this._writeEncodingBuffer);
            this._writeEncodingBuffer = null;
            this._bufferRecycler.releaseByteBuffer(1, bArr);
        }
    }

    public void setEncoding(JsonEncoding jsonEncoding) {
        this._encoding = jsonEncoding;
    }

    public StreamReadConstraints streamReadConstraints() {
        return this._streamReadConstraints;
    }

    public StreamWriteConstraints streamWriteConstraints() {
        return this._streamWriteConstraints;
    }

    public IOContext withEncoding(JsonEncoding jsonEncoding) {
        this._encoding = jsonEncoding;
        return this;
    }

    public final void _verifyRelease(char[] cArr, char[] cArr2) {
        if (cArr != cArr2 && cArr.length < cArr2.length) {
            throw wrongBuf();
        }
    }

    public byte[] allocBase64Buffer(int i10) {
        _verifyAlloc(this._base64Buffer);
        byte[] bArrAllocByteBuffer = this._bufferRecycler.allocByteBuffer(3, i10);
        this._base64Buffer = bArrAllocByteBuffer;
        return bArrAllocByteBuffer;
    }

    public byte[] allocReadIOBuffer(int i10) {
        _verifyAlloc(this._readIOBuffer);
        byte[] bArrAllocByteBuffer = this._bufferRecycler.allocByteBuffer(0, i10);
        this._readIOBuffer = bArrAllocByteBuffer;
        return bArrAllocByteBuffer;
    }

    public char[] allocTokenBuffer(int i10) {
        _verifyAlloc(this._tokenCBuffer);
        char[] cArrAllocCharBuffer = this._bufferRecycler.allocCharBuffer(0, i10);
        this._tokenCBuffer = cArrAllocCharBuffer;
        return cArrAllocCharBuffer;
    }

    public byte[] allocWriteEncodingBuffer(int i10) {
        _verifyAlloc(this._writeEncodingBuffer);
        byte[] bArrAllocByteBuffer = this._bufferRecycler.allocByteBuffer(1, i10);
        this._writeEncodingBuffer = bArrAllocByteBuffer;
        return bArrAllocByteBuffer;
    }

    @Deprecated
    public IOContext(StreamReadConstraints streamReadConstraints, BufferRecycler bufferRecycler, ContentReference contentReference, boolean z2) {
        this(streamReadConstraints, StreamWriteConstraints.defaults(), ErrorReportConfiguration.defaults(), bufferRecycler, contentReference, z2);
    }

    @Deprecated
    public IOContext(BufferRecycler bufferRecycler, ContentReference contentReference, boolean z2) {
        this(StreamReadConstraints.defaults(), StreamWriteConstraints.defaults(), ErrorReportConfiguration.defaults(), bufferRecycler, contentReference, z2);
    }

    @Deprecated
    public IOContext(BufferRecycler bufferRecycler, Object obj, boolean z2) {
        this(bufferRecycler, ContentReference.rawReference(obj), z2);
    }
}
