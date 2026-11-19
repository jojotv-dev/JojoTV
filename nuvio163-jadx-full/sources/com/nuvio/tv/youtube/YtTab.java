package com.nuvio.tv.youtube;

import androidx.media3.container.NalUnitUtil;
import io.ktor.http.LinkHeader;
import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;

/* JADX WARN: Enum visitor error
jadx.core.utils.exceptions.JadxRuntimeException: Init of enum field 'LIVE' uses external variables
	at jadx.core.dex.visitors.EnumVisitor.createEnumFieldByConstructor(EnumVisitor.java:485)
	at jadx.core.dex.visitors.EnumVisitor.processEnumFieldByField(EnumVisitor.java:399)
	at jadx.core.dex.visitors.EnumVisitor.processEnumFieldByWrappedInsn(EnumVisitor.java:364)
	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromFilledArray(EnumVisitor.java:349)
	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromInsn(EnumVisitor.java:284)
	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromInvoke(EnumVisitor.java:315)
	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromInsn(EnumVisitor.java:288)
	at jadx.core.dex.visitors.EnumVisitor.convertToEnum(EnumVisitor.java:160)
	at jadx.core.dex.visitors.EnumVisitor.visit(EnumVisitor.java:102)
 */
/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0014\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B%\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0005¢\u0006\u0004\b\u0007\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\fj\u0002\b\u000ej\u0002\b\u000fj\u0002\b\u0010j\u0002\b\u0011j\u0002\b\u0012j\u0002\b\u0013j\u0002\b\u0014j\u0002\b\u0015j\u0002\b\u0016j\u0002\b\u0017j\u0002\b\u0018¨\u0006\u0019"}, d2 = {"Lcom/nuvio/tv/youtube/YtTab;", "", LinkHeader.Parameters.Title, "", "needsSignIn", "", "grid", "<init>", "(Ljava/lang/String;ILjava/lang/String;ZZ)V", "getTitle", "()Ljava/lang/String;", "getNeedsSignIn", "()Z", "getGrid", "HOME", "SUBSCRIPTIONS", "HISTORY", "PLAYLISTS", "LIVE", "MUSIC", "GAMING", "NEWS", "SPORTS", "SHORTS", "CHANNELS", "app_fullRelease"}, k = 1, mv = {2, 3, 0}, xi = NalUnitUtil.H265_NAL_UNIT_TYPE_UNSPECIFIED)
public final class YtTab {
    private static final /* synthetic */ EnumEntries $ENTRIES;
    private static final /* synthetic */ YtTab[] $VALUES;
    public static final YtTab GAMING;
    public static final YtTab LIVE;
    public static final YtTab MUSIC;
    public static final YtTab NEWS;
    public static final YtTab SPORTS;
    private final boolean grid;
    private final boolean needsSignIn;
    private final String title;
    public static final YtTab HOME = new YtTab("HOME", 0, "Accueil", false, false, 6, null);
    public static final YtTab SUBSCRIPTIONS = new YtTab("SUBSCRIPTIONS", 1, "Abonnements", true, true);
    public static final YtTab HISTORY = new YtTab("HISTORY", 2, "Historique", true, true);
    public static final YtTab PLAYLISTS = new YtTab("PLAYLISTS", 3, "Playlists", true, true);
    public static final YtTab SHORTS = new YtTab("SHORTS", 9, "Shorts", false, true, 2, null);
    public static final YtTab CHANNELS = new YtTab("CHANNELS", 10, "Chaînes", true, true);

    private static final /* synthetic */ YtTab[] $values() {
        return new YtTab[]{HOME, SUBSCRIPTIONS, HISTORY, PLAYLISTS, LIVE, MUSIC, GAMING, NEWS, SPORTS, SHORTS, CHANNELS};
    }

    static {
        boolean z2 = false;
        LIVE = new YtTab("LIVE", 4, "En direct", false, z2, 6, null);
        boolean z10 = false;
        MUSIC = new YtTab("MUSIC", 5, "Musique", z2, z10, 6, null);
        boolean z11 = false;
        GAMING = new YtTab("GAMING", 6, "Jeux vidéo", z10, z11, 6, null);
        boolean z12 = false;
        NEWS = new YtTab("NEWS", 7, "Actualités", z11, z12, 6, null);
        SPORTS = new YtTab("SPORTS", 8, "Sports", z12, false, 6, null);
        YtTab[] ytTabArr$values = $values();
        $VALUES = ytTabArr$values;
        $ENTRIES = EnumEntriesKt.enumEntries(ytTabArr$values);
    }

    private YtTab(String str, int i10, String str2, boolean z2, boolean z10) {
        super(str, i10);
        this.title = str2;
        this.needsSignIn = z2;
        this.grid = z10;
    }

    public static EnumEntries<YtTab> getEntries() {
        return $ENTRIES;
    }

    public static YtTab valueOf(String str) {
        return (YtTab) Enum.valueOf(YtTab.class, str);
    }

    public static YtTab[] values() {
        return (YtTab[]) $VALUES.clone();
    }

    public final boolean getGrid() {
        return this.grid;
    }

    public final boolean getNeedsSignIn() {
        return this.needsSignIn;
    }

    public final String getTitle() {
        return this.title;
    }

    public /* synthetic */ YtTab(String str, int i10, String str2, boolean z2, boolean z10, int i11, DefaultConstructorMarker defaultConstructorMarker) {
        this(str, i10, str2, (i11 & 2) != 0 ? false : z2, (i11 & 4) != 0 ? false : z10);
    }
}
