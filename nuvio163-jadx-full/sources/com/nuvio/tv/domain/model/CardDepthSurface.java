package com.nuvio.tv.domain.model;

import androidx.media3.container.NalUnitUtil;
import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\b\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\b¨\u0006\t"}, d2 = {"Lcom/nuvio/tv/domain/model/CardDepthSurface;", "", "<init>", "(Ljava/lang/String;I)V", "POSTERS", "CONTINUE_WATCHING", "EPISODE_CARDS", "CAST", "TRAILERS", "app_fullRelease"}, k = 1, mv = {2, 3, 0}, xi = NalUnitUtil.H265_NAL_UNIT_TYPE_UNSPECIFIED)
public enum CardDepthSurface {
    POSTERS,
    CONTINUE_WATCHING,
    EPISODE_CARDS,
    CAST,
    TRAILERS;

    private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(values());

    public static EnumEntries<CardDepthSurface> getEntries() {
        return $ENTRIES;
    }
}
