package com.fasterxml.jackson.core;

import a0.i;
import com.fasterxml.jackson.core.exc.StreamConstraintsException;
import java.io.Serializable;

/* JADX INFO: loaded from: classes3.dex */
public class StreamReadConstraints implements Serializable {
    public static final int DEFAULT_MAX_DEPTH = 1000;
    public static final long DEFAULT_MAX_DOC_LEN = -1;
    public static final int DEFAULT_MAX_NAME_LEN = 50000;
    public static final int DEFAULT_MAX_NUM_LEN = 1000;
    private static final int MAX_BIGINT_SCALE_MAGNITUDE = 100000;
    private static final long serialVersionUID = 1;
    protected final long _maxDocLen;
    protected final int _maxNameLen;
    protected final int _maxNestingDepth;
    protected final int _maxNumLen;
    protected final int _maxStringLen;
    public static final int DEFAULT_MAX_STRING_LEN = 20000000;
    private static StreamReadConstraints DEFAULT = new StreamReadConstraints(1000, -1, 1000, DEFAULT_MAX_STRING_LEN, 50000);

    public static final class Builder {
        private long maxDocLen;
        private int maxNameLen;
        private int maxNestingDepth;
        private int maxNumLen;
        private int maxStringLen;

        public Builder() {
            this(1000, -1L, 1000, StreamReadConstraints.DEFAULT_MAX_STRING_LEN, 50000);
        }

        public StreamReadConstraints build() {
            return new StreamReadConstraints(this.maxNestingDepth, this.maxDocLen, this.maxNumLen, this.maxStringLen, this.maxNameLen);
        }

        public Builder maxDocumentLength(long j10) {
            if (j10 <= 0) {
                j10 = -1;
            }
            this.maxDocLen = j10;
            return this;
        }

        public Builder maxNameLength(int i10) {
            if (i10 < 0) {
                throw new IllegalArgumentException("Cannot set maxNameLen to a negative value");
            }
            this.maxNameLen = i10;
            return this;
        }

        public Builder maxNestingDepth(int i10) {
            if (i10 < 0) {
                throw new IllegalArgumentException("Cannot set maxNestingDepth to a negative value");
            }
            this.maxNestingDepth = i10;
            return this;
        }

        public Builder maxNumberLength(int i10) {
            if (i10 < 0) {
                throw new IllegalArgumentException("Cannot set maxNumberLength to a negative value");
            }
            this.maxNumLen = i10;
            return this;
        }

        public Builder maxStringLength(int i10) {
            if (i10 < 0) {
                throw new IllegalArgumentException("Cannot set maxStringLen to a negative value");
            }
            this.maxStringLen = i10;
            return this;
        }

        public Builder(int i10, long j10, int i11, int i12, int i13) {
            this.maxNestingDepth = i10;
            this.maxDocLen = j10;
            this.maxNumLen = i11;
            this.maxStringLen = i12;
            this.maxNameLen = i13;
        }

        public Builder(StreamReadConstraints streamReadConstraints) {
            this.maxNestingDepth = streamReadConstraints._maxNestingDepth;
            this.maxDocLen = streamReadConstraints._maxDocLen;
            this.maxNumLen = streamReadConstraints._maxNumLen;
            this.maxStringLen = streamReadConstraints._maxStringLen;
            this.maxNameLen = streamReadConstraints._maxNameLen;
        }
    }

    @Deprecated
    public StreamReadConstraints(int i10, long j10, int i11, int i12) {
        this(i10, -1L, i11, i12, 50000);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static StreamReadConstraints defaults() {
        return DEFAULT;
    }

    public static void overrideDefaultStreamReadConstraints(StreamReadConstraints streamReadConstraints) {
        if (streamReadConstraints == null) {
            DEFAULT = new StreamReadConstraints(1000, -1L, 1000, DEFAULT_MAX_STRING_LEN);
        } else {
            DEFAULT = streamReadConstraints;
        }
    }

    public String _constrainRef(String str) {
        return i.n("`StreamReadConstraints.", str, "()`");
    }

    public StreamConstraintsException _constructException(String str, Object... objArr) throws StreamConstraintsException {
        throw new StreamConstraintsException(String.format(str, objArr));
    }

    public long getMaxDocumentLength() {
        return this._maxDocLen;
    }

    public int getMaxNameLength() {
        return this._maxNameLen;
    }

    public int getMaxNestingDepth() {
        return this._maxNestingDepth;
    }

    public int getMaxNumberLength() {
        return this._maxNumLen;
    }

    public int getMaxStringLength() {
        return this._maxStringLen;
    }

    public boolean hasMaxDocumentLength() {
        return this._maxDocLen > 0;
    }

    public Builder rebuild() {
        return new Builder(this);
    }

    public void validateBigIntegerScale(int i10) throws StreamConstraintsException {
        if (Math.abs(i10) > 100000) {
            throw _constructException("BigDecimal scale (%d) magnitude exceeds the maximum allowed (%d)", Integer.valueOf(i10), 100000);
        }
    }

    public void validateDocumentLength(long j10) throws StreamConstraintsException {
        long j11 = this._maxDocLen;
        if (j10 > j11 && j11 > 0) {
            throw _constructException("Document length (%d) exceeds the maximum allowed (%d, from %s)", Long.valueOf(j10), Long.valueOf(this._maxDocLen), _constrainRef("getMaxDocumentLength"));
        }
    }

    public void validateFPLength(int i10) throws StreamConstraintsException {
        if (i10 > this._maxNumLen) {
            throw _constructException("Number value length (%d) exceeds the maximum allowed (%d, from %s)", Integer.valueOf(i10), Integer.valueOf(this._maxNumLen), _constrainRef("getMaxNumberLength"));
        }
    }

    public void validateIntegerLength(int i10) throws StreamConstraintsException {
        if (i10 > this._maxNumLen) {
            throw _constructException("Number value length (%d) exceeds the maximum allowed (%d, from %s)", Integer.valueOf(i10), Integer.valueOf(this._maxNumLen), _constrainRef("getMaxNumberLength"));
        }
    }

    public void validateNameLength(int i10) throws StreamConstraintsException {
        if (i10 > this._maxNameLen) {
            throw _constructException("Name length (%d) exceeds the maximum allowed (%d, from %s)", Integer.valueOf(i10), Integer.valueOf(this._maxNameLen), _constrainRef("getMaxNameLength"));
        }
    }

    public void validateNestingDepth(int i10) throws StreamConstraintsException {
        if (i10 > this._maxNestingDepth) {
            throw _constructException("Document nesting depth (%d) exceeds the maximum allowed (%d, from %s)", Integer.valueOf(i10), Integer.valueOf(this._maxNestingDepth), _constrainRef("getMaxNestingDepth"));
        }
    }

    public void validateStringLength(int i10) throws StreamConstraintsException {
        if (i10 > this._maxStringLen) {
            throw _constructException("String value length (%d) exceeds the maximum allowed (%d, from %s)", Integer.valueOf(i10), Integer.valueOf(this._maxStringLen), _constrainRef("getMaxStringLength"));
        }
    }

    public StreamReadConstraints(int i10, long j10, int i11, int i12, int i13) {
        this._maxNestingDepth = i10;
        this._maxDocLen = j10;
        this._maxNumLen = i11;
        this._maxStringLen = i12;
        this._maxNameLen = i13;
    }
}
