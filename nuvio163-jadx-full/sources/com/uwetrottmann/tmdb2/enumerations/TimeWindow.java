package com.uwetrottmann.tmdb2.enumerations;

/* JADX INFO: loaded from: classes4.dex */
public enum TimeWindow {
    DAY("day"),
    WEEK("week");

    private final String value;

    TimeWindow(String str) {
        this.value = str;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.value;
    }
}
