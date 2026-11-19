package com.uwetrottmann.tmdb2.enumerations;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes4.dex */
public enum CreditType {
    CAST("cast"),
    CREW("crew");

    private static final Map<String, CreditType> lookup = prepareLookup();
    private final String value;

    CreditType(String str) {
        this.value = str;
    }

    public static CreditType get(String str) {
        return lookup.get(str);
    }

    private static Map<String, CreditType> prepareLookup() {
        HashMap map = new HashMap();
        for (CreditType creditType : values()) {
            map.put(creditType.value, creditType);
        }
        return map;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.value;
    }
}
