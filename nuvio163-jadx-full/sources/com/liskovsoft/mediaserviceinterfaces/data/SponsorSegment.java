package com.liskovsoft.mediaserviceinterfaces.data;

/* JADX INFO: loaded from: classes4.dex */
public interface SponsorSegment {
    public static final String ACTION_MUTE = "mute";
    public static final String ACTION_SKIP = "skip";
    public static final String CATEGORY_FILLER = "filler";
    public static final String CATEGORY_INTERACTION = "interaction";
    public static final String CATEGORY_INTRO = "intro";
    public static final String CATEGORY_MUSIC_OFF_TOPIC = "music_offtopic";
    public static final String CATEGORY_OUTRO = "outro";
    public static final String CATEGORY_POI_HIGHLIGHT = "poi_highlight";
    public static final String CATEGORY_PREVIEW_RECAP = "preview";
    public static final String CATEGORY_SELF_PROMO = "selfpromo";
    public static final String CATEGORY_SPONSOR = "sponsor";

    String getAction();

    String getCategory();

    long getEndMs();

    long getStartMs();
}
