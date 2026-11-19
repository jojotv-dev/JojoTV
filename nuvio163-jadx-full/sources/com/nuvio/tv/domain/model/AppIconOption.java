package com.nuvio.tv.domain.model;

import androidx.media3.container.NalUnitUtil;
import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000b\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0011\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\fj\u0002\b\r¨\u0006\u000e"}, d2 = {"Lcom/nuvio/tv/domain/model/AppIconOption;", "", "key", "", "<init>", "(Ljava/lang/String;ILjava/lang/String;)V", "getKey", "()Ljava/lang/String;", "ORIGINAL", "ARCTIC_BLUE", "EMERALD", "ROSE_GOLD", "COPPER", "GRAPHITE", "app_fullRelease"}, k = 1, mv = {2, 3, 0}, xi = NalUnitUtil.H265_NAL_UNIT_TYPE_UNSPECIFIED)
public enum AppIconOption {
    ORIGINAL("original"),
    ARCTIC_BLUE("arctic_blue"),
    EMERALD("emerald"),
    ROSE_GOLD("rose_gold"),
    COPPER("copper"),
    GRAPHITE("graphite");

    private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(values());
    private final String key;

    AppIconOption(String str) {
        this.key = str;
    }

    public static EnumEntries<AppIconOption> getEntries() {
        return $ENTRIES;
    }

    public final String getKey() {
        return this.key;
    }
}
