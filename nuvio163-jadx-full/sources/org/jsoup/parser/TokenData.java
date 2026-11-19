package org.jsoup.parser;

import org.jsoup.internal.StringUtil;

/* JADX INFO: loaded from: classes5.dex */
class TokenData {
    private StringBuilder builder;
    private String value;

    private void flipToBuilder() {
        StringBuilder sbBorrowBuilder = StringUtil.borrowBuilder();
        this.builder = sbBorrowBuilder;
        sbBorrowBuilder.append(this.value);
        this.value = null;
    }

    public void append(String str) {
        StringBuilder sb2 = this.builder;
        if (sb2 != null) {
            sb2.append(str);
        } else if (this.value == null) {
            this.value = str;
        } else {
            flipToBuilder();
            this.builder.append(str);
        }
    }

    public void appendCodePoint(int i10) {
        StringBuilder sb2 = this.builder;
        if (sb2 != null) {
            sb2.appendCodePoint(i10);
        } else if (this.value == null) {
            this.value = String.valueOf(Character.toChars(i10));
        } else {
            flipToBuilder();
            this.builder.appendCodePoint(i10);
        }
    }

    public boolean hasData() {
        return (this.builder == null && this.value == null) ? false : true;
    }

    public void reset() {
        StringBuilder sb2 = this.builder;
        if (sb2 != null) {
            StringUtil.releaseBuilderVoid(sb2);
            this.builder = null;
        }
        this.value = null;
    }

    public void set(String str) {
        reset();
        this.value = str;
    }

    public String toString() {
        StringBuilder sb2 = this.builder;
        if (sb2 != null) {
            return sb2.toString();
        }
        String str = this.value;
        return str != null ? str : "";
    }

    public String value() {
        StringBuilder sb2 = this.builder;
        if (sb2 == null) {
            String str = this.value;
            return str != null ? str : "";
        }
        this.value = sb2.toString();
        StringUtil.releaseBuilder(this.builder);
        this.builder = null;
        return this.value;
    }

    public void append(char c5) {
        StringBuilder sb2 = this.builder;
        if (sb2 != null) {
            sb2.append(c5);
        } else if (this.value != null) {
            flipToBuilder();
            this.builder.append(c5);
        } else {
            this.value = String.valueOf(c5);
        }
    }
}
