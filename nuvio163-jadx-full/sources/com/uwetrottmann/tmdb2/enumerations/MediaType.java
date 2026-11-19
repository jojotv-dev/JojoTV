package com.uwetrottmann.tmdb2.enumerations;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes4.dex */
public enum MediaType {
    MOVIE("movie"),
    TV("tv"),
    PERSON("person");

    private static final Map<String, MediaType> lookup = prepareLookup();
    private final String value;

    MediaType(String str) {
        this.value = str;
    }

    public static MediaType get(String str) {
        return lookup.get(str);
    }

    private static Map<String, MediaType> prepareLookup() {
        HashMap map = new HashMap();
        for (MediaType mediaType : values()) {
            map.put(mediaType.value, mediaType);
        }
        return map;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.value;
    }
}
