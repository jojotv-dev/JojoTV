package com.fasterxml.jackson.core.io;

import com.fasterxml.jackson.core.ErrorReportConfiguration;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/* JADX INFO: loaded from: classes3.dex */
public class ContentReference implements Serializable {

    @Deprecated
    public static final int DEFAULT_MAX_CONTENT_SNIPPET = 500;
    private static final long serialVersionUID = 1;
    protected final boolean _isContentTextual;
    protected final int _length;
    protected final int _maxRawContentLength;
    protected final int _offset;
    protected final transient Object _rawContent;
    protected static final ContentReference UNKNOWN_CONTENT = new ContentReference(false, null);
    protected static final ContentReference REDACTED_CONTENT = new ContentReference(false, null);

    @Deprecated
    public ContentReference(boolean z2, Object obj) {
        this(z2, obj, -1, -1, ErrorReportConfiguration.defaults());
    }

    @Deprecated
    public static ContentReference construct(boolean z2, Object obj) {
        return new ContentReference(z2, obj, ErrorReportConfiguration.defaults());
    }

    public static ContentReference rawReference(boolean z2, Object obj) {
        return obj instanceof ContentReference ? (ContentReference) obj : new ContentReference(z2, obj);
    }

    private void readObject(ObjectInputStream objectInputStream) {
    }

    public static ContentReference redacted() {
        return REDACTED_CONTENT;
    }

    public static ContentReference unknown() {
        return UNKNOWN_CONTENT;
    }

    private void writeObject(ObjectOutputStream objectOutputStream) {
    }

    public int _append(StringBuilder sb2, String str) {
        sb2.append('\"');
        int length = str.length();
        for (int i10 = 0; i10 < length; i10++) {
            char cCharAt = str.charAt(i10);
            if (!Character.isISOControl(cCharAt) || !_appendEscaped(sb2, cCharAt)) {
                sb2.append(cCharAt);
            }
        }
        sb2.append('\"');
        return str.length();
    }

    public boolean _appendEscaped(StringBuilder sb2, int i10) {
        if (i10 == 13 || i10 == 10) {
            return false;
        }
        sb2.append('\\');
        sb2.append('u');
        sb2.append(CharTypes.hexToChar((i10 >> 12) & 15));
        sb2.append(CharTypes.hexToChar((i10 >> 8) & 15));
        sb2.append(CharTypes.hexToChar((i10 >> 4) & 15));
        sb2.append(CharTypes.hexToChar(i10 & 15));
        return true;
    }

    public String _truncate(CharSequence charSequence, int[] iArr, int i10) {
        _truncateOffsets(iArr, charSequence.length());
        int i11 = iArr[0];
        return charSequence.subSequence(i11, Math.min(iArr[1], i10) + i11).toString();
    }

    public void _truncateOffsets(int[] iArr, int i10) {
        int i11 = iArr[0];
        if (i11 < 0) {
            i11 = 0;
        } else if (i11 >= i10) {
            i11 = i10;
        }
        iArr[0] = i11;
        int i12 = iArr[1];
        int i13 = i10 - i11;
        if (i12 < 0 || i12 > i13) {
            iArr[1] = i13;
        }
    }

    public StringBuilder appendSourceDescription(StringBuilder sb2) {
        String str_truncate;
        Object rawContent = getRawContent();
        if (rawContent == null) {
            if (this == REDACTED_CONTENT) {
                sb2.append("REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled)");
                return sb2;
            }
            sb2.append("UNKNOWN");
            return sb2;
        }
        Class<?> cls = rawContent instanceof Class ? (Class) rawContent : rawContent.getClass();
        String name = cls.getName();
        if (name.startsWith("java.")) {
            name = cls.getSimpleName();
        } else if (rawContent instanceof byte[]) {
            name = "byte[]";
        } else if (rawContent instanceof char[]) {
            name = "char[]";
        }
        sb2.append('(');
        sb2.append(name);
        sb2.append(')');
        if (hasTextualContent()) {
            int iMaxRawContentLength = maxRawContentLength();
            int[] iArr = {contentOffset(), contentLength()};
            String str = " chars";
            if (rawContent instanceof CharSequence) {
                str_truncate = _truncate((CharSequence) rawContent, iArr, iMaxRawContentLength);
            } else if (rawContent instanceof char[]) {
                str_truncate = _truncate((char[]) rawContent, iArr, iMaxRawContentLength);
            } else if (rawContent instanceof byte[]) {
                str_truncate = _truncate((byte[]) rawContent, iArr, iMaxRawContentLength);
                str = " bytes";
            } else {
                str_truncate = null;
            }
            if (str_truncate != null) {
                _append(sb2, str_truncate);
                if (iArr[1] > iMaxRawContentLength) {
                    sb2.append("[truncated ");
                    sb2.append(iArr[1] - iMaxRawContentLength);
                    sb2.append(str);
                    sb2.append(']');
                    return sb2;
                }
            }
        } else if (rawContent instanceof byte[]) {
            int iContentLength = contentLength();
            if (iContentLength < 0) {
                iContentLength = ((byte[]) rawContent).length;
            }
            sb2.append('[');
            sb2.append(iContentLength);
            sb2.append(" bytes]");
        }
        return sb2;
    }

    public String buildSourceDescription() {
        return appendSourceDescription(new StringBuilder(200)).toString();
    }

    public int contentLength() {
        return this._length;
    }

    public int contentOffset() {
        return this._offset;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || !(obj instanceof ContentReference)) {
            return false;
        }
        ContentReference contentReference = (ContentReference) obj;
        if (this._offset != contentReference._offset || this._length != contentReference._length) {
            return false;
        }
        Object obj2 = contentReference._rawContent;
        Object obj3 = this._rawContent;
        if (obj3 == null) {
            return obj2 == null;
        }
        if (obj2 == null) {
            return false;
        }
        if ((obj3 instanceof File) || (obj3 instanceof URL) || (obj3 instanceof URI)) {
            return obj3.equals(obj2);
        }
        return obj3 == obj2;
    }

    public Object getRawContent() {
        return this._rawContent;
    }

    public boolean hasTextualContent() {
        return this._isContentTextual;
    }

    public int hashCode() {
        return Objects.hashCode(this._rawContent);
    }

    public int maxRawContentLength() {
        return this._maxRawContentLength;
    }

    public Object readResolve() {
        return UNKNOWN_CONTENT;
    }

    @Deprecated
    public ContentReference(boolean z2, Object obj, int i10, int i11) {
        this(z2, obj, i10, i11, ErrorReportConfiguration.defaults());
    }

    @Deprecated
    public static ContentReference construct(boolean z2, Object obj, int i10, int i11) {
        return new ContentReference(z2, obj, i10, i11, ErrorReportConfiguration.defaults());
    }

    public ContentReference(boolean z2, Object obj, ErrorReportConfiguration errorReportConfiguration) {
        this(z2, obj, -1, -1, errorReportConfiguration);
    }

    public static ContentReference construct(boolean z2, Object obj, int i10, int i11, ErrorReportConfiguration errorReportConfiguration) {
        return new ContentReference(z2, obj, i10, i11, errorReportConfiguration);
    }

    public ContentReference(boolean z2, Object obj, int i10, int i11, ErrorReportConfiguration errorReportConfiguration) {
        this._isContentTextual = z2;
        this._rawContent = obj;
        this._offset = i10;
        this._length = i11;
        this._maxRawContentLength = errorReportConfiguration.getMaxRawContentLength();
    }

    public static ContentReference construct(boolean z2, Object obj, ErrorReportConfiguration errorReportConfiguration) {
        return new ContentReference(z2, obj, errorReportConfiguration);
    }

    public static ContentReference rawReference(Object obj) {
        return rawReference(false, obj);
    }

    public String _truncate(char[] cArr, int[] iArr, int i10) {
        _truncateOffsets(iArr, cArr.length);
        return new String(cArr, iArr[0], Math.min(iArr[1], i10));
    }

    public String _truncate(byte[] bArr, int[] iArr, int i10) {
        _truncateOffsets(iArr, bArr.length);
        return new String(bArr, iArr[0], Math.min(iArr[1], i10), StandardCharsets.UTF_8);
    }
}
