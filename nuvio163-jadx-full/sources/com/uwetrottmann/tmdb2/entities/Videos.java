package com.uwetrottmann.tmdb2.entities;

import com.uwetrottmann.tmdb2.enumerations.VideoType;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class Videos {

    /* JADX INFO: renamed from: id, reason: collision with root package name */
    public Integer f11057id;
    public List<Video> results;

    public static class Video {

        /* JADX INFO: renamed from: id, reason: collision with root package name */
        public String f11058id;
        public String iso_3166_1;
        public String iso_639_1;
        public String key;
        public String name;
        public String site;
        public Integer size;
        public VideoType type;
    }
}
