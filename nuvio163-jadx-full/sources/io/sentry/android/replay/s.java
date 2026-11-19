package io.sentry.android.replay;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX INFO: loaded from: classes.dex */
public enum s {
    INITIAL,
    STARTED,
    RESUMED,
    PAUSED,
    STOPPED,
    CLOSED;

    private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(values());

    public static EnumEntries<s> getEntries() {
        return $ENTRIES;
    }
}
