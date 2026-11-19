package com.fasterxml.jackson.core;

import java.io.Serializable;

/* JADX INFO: loaded from: classes3.dex */
public class ErrorReportConfiguration implements Serializable {
    private static ErrorReportConfiguration DEFAULT = new ErrorReportConfiguration(256, 500);
    public static final int DEFAULT_MAX_ERROR_TOKEN_LENGTH = 256;
    public static final int DEFAULT_MAX_RAW_CONTENT_LENGTH = 500;
    private static final long serialVersionUID = 1;
    protected final int _maxErrorTokenLength;
    protected final int _maxRawContentLength;

    public static final class Builder {
        private int maxErrorTokenLength;
        private int maxRawContentLength;

        public Builder() {
            this(256, 500);
        }

        public ErrorReportConfiguration build() {
            return new ErrorReportConfiguration(this.maxErrorTokenLength, this.maxRawContentLength);
        }

        public Builder maxErrorTokenLength(int i10) {
            ErrorReportConfiguration.validateMaxErrorTokenLength(i10);
            this.maxErrorTokenLength = i10;
            return this;
        }

        public Builder maxRawContentLength(int i10) {
            ErrorReportConfiguration.validateMaxRawContentLength(i10);
            this.maxRawContentLength = i10;
            return this;
        }

        public Builder(int i10, int i11) {
            this.maxErrorTokenLength = i10;
            this.maxRawContentLength = i11;
        }

        public Builder(ErrorReportConfiguration errorReportConfiguration) {
            this.maxErrorTokenLength = errorReportConfiguration._maxErrorTokenLength;
            this.maxRawContentLength = errorReportConfiguration._maxRawContentLength;
        }
    }

    public ErrorReportConfiguration(int i10, int i11) {
        this._maxErrorTokenLength = i10;
        this._maxRawContentLength = i11;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static ErrorReportConfiguration defaults() {
        return DEFAULT;
    }

    public static void overrideDefaultErrorReportConfiguration(ErrorReportConfiguration errorReportConfiguration) {
        if (errorReportConfiguration == null) {
            DEFAULT = new ErrorReportConfiguration(256, 500);
        } else {
            DEFAULT = errorReportConfiguration;
        }
    }

    public static void validateMaxErrorTokenLength(int i10) {
        if (i10 < 0) {
            throw new IllegalArgumentException(String.format("Value of maxErrorTokenLength (%d) cannot be negative", Integer.valueOf(i10)));
        }
    }

    public static void validateMaxRawContentLength(int i10) {
        if (i10 < 0) {
            throw new IllegalArgumentException(String.format("Value of maxRawContentLength (%d) cannot be negative", Integer.valueOf(i10)));
        }
    }

    public int getMaxErrorTokenLength() {
        return this._maxErrorTokenLength;
    }

    public int getMaxRawContentLength() {
        return this._maxRawContentLength;
    }

    public Builder rebuild() {
        return new Builder(this);
    }
}
