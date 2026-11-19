package com.fasterxml.jackson.core.filter;

import com.fasterxml.jackson.core.JsonPointer;

/* JADX INFO: loaded from: classes3.dex */
public class JsonPointerBasedFilter extends TokenFilter {
    protected final boolean _includeAllElements;
    protected final JsonPointer _pathToMatch;

    public JsonPointerBasedFilter(String str) {
        this(JsonPointer.compile(str), false);
    }

    @Override // com.fasterxml.jackson.core.filter.TokenFilter
    public boolean _includeScalar() {
        return this._pathToMatch.matches();
    }

    public JsonPointerBasedFilter construct(JsonPointer jsonPointer, boolean z2) {
        return new JsonPointerBasedFilter(jsonPointer, z2);
    }

    @Override // com.fasterxml.jackson.core.filter.TokenFilter
    public TokenFilter filterStartArray() {
        return this;
    }

    @Override // com.fasterxml.jackson.core.filter.TokenFilter
    public TokenFilter filterStartObject() {
        return this;
    }

    @Override // com.fasterxml.jackson.core.filter.TokenFilter
    public TokenFilter includeElement(int i10) {
        JsonPointer jsonPointerMatchElement = (!this._includeAllElements || this._pathToMatch.mayMatchElement()) ? this._pathToMatch.matchElement(i10) : this._pathToMatch.tail();
        if (jsonPointerMatchElement == null) {
            return null;
        }
        return jsonPointerMatchElement.matches() ? TokenFilter.INCLUDE_ALL : construct(jsonPointerMatchElement, this._includeAllElements);
    }

    @Override // com.fasterxml.jackson.core.filter.TokenFilter
    public TokenFilter includeProperty(String str) {
        JsonPointer jsonPointerMatchProperty = this._pathToMatch.matchProperty(str);
        if (jsonPointerMatchProperty == null) {
            return null;
        }
        return jsonPointerMatchProperty.matches() ? TokenFilter.INCLUDE_ALL : construct(jsonPointerMatchProperty, this._includeAllElements);
    }

    @Override // com.fasterxml.jackson.core.filter.TokenFilter
    public String toString() {
        return "[JsonPointerFilter at: " + this._pathToMatch + "]";
    }

    public JsonPointerBasedFilter(JsonPointer jsonPointer) {
        this(jsonPointer, false);
    }

    public JsonPointerBasedFilter(JsonPointer jsonPointer, boolean z2) {
        this._pathToMatch = jsonPointer;
        this._includeAllElements = z2;
    }
}
