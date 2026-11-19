package com.uwetrottmann.tmdb2.enumerations;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes4.dex */
public enum VideoType {
    TRAILER("Trailer"),
    TEASER("Teaser"),
    CLIP("Clip"),
    FEATURETTE("Featurette"),
    OPENING_CREDITS("Opening Credits");

    private static final Map<String, VideoType> lookup = prepareLookup();
    private final String value;

    VideoType(String str) {
        this.value = str;
    }

    public static VideoType get(String str) {
        return lookup.get(str);
    }

    private static Map<String, VideoType> prepareLookup() {
        HashMap map = new HashMap();
        for (VideoType videoType : values()) {
            map.put(videoType.value, videoType);
        }
        return map;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.value;
    }
}
