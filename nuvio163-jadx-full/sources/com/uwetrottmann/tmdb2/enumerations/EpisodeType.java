package com.uwetrottmann.tmdb2.enumerations;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes4.dex */
public enum EpisodeType {
    STANDARD("standard"),
    MID_SEASON_FINALE("mid_season"),
    FINALE("finale");

    private static final Map<String, EpisodeType> STRING_MAPPING = new HashMap();
    private final String value;

    static {
        for (EpisodeType episodeType : values()) {
            STRING_MAPPING.put(episodeType.toString().toUpperCase(), episodeType);
        }
    }

    EpisodeType(String str) {
        this.value = str;
    }

    public static EpisodeType fromValue(String str) {
        return STRING_MAPPING.get(str.toUpperCase());
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.value;
    }
}
