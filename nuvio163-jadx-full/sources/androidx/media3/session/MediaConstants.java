package androidx.media3.session;

import androidx.media3.common.util.UnstableApi;

/* JADX INFO: loaded from: classes3.dex */
public final class MediaConstants {
    static final String ARGUMENT_CAPTIONING_ENABLED = "androidx.media3.session.ARGUMENT_CAPTIONING_ENABLED";
    public static final int ERROR_CODE_AUTHENTICATION_EXPIRED_COMPAT = 3;

    @UnstableApi
    public static final String EXTRAS_KEY_APPLICATION_PREFERENCES_USING_CAR_APP_LIBRARY_INTENT = "androidx.media.BrowserRoot.Extras.APPLICATION_PREFERENCES_USING_CAR_APP_LIBRARY_INTENT";

    @UnstableApi
    public static final String EXTRAS_KEY_COMMAND_BUTTON_ICON_COMPAT = "androidx.media3.session.EXTRAS_KEY_COMMAND_BUTTON_ICON_COMPAT";

    @UnstableApi
    public static final String EXTRAS_KEY_COMMAND_BUTTON_ICON_URI_COMPAT = "androidx.media3.session.EXTRAS_KEY_COMMAND_BUTTON_ICON_URI_COMPAT";

    @UnstableApi
    public static final String EXTRAS_KEY_COMPLETION_PERCENTAGE = "androidx.media.MediaItem.Extras.COMPLETION_PERCENTAGE";

    @UnstableApi
    public static final String EXTRAS_KEY_COMPLETION_STATUS = "android.media.extra.PLAYBACK_STATUS";

    @UnstableApi
    public static final String EXTRAS_KEY_CONTENT_STYLE_BROWSABLE = "android.media.browse.CONTENT_STYLE_BROWSABLE_HINT";

    @UnstableApi
    public static final String EXTRAS_KEY_CONTENT_STYLE_GROUP_TITLE = "android.media.browse.CONTENT_STYLE_GROUP_TITLE_HINT";

    @UnstableApi
    public static final String EXTRAS_KEY_CONTENT_STYLE_PLAYABLE = "android.media.browse.CONTENT_STYLE_PLAYABLE_HINT";

    @UnstableApi
    public static final String EXTRAS_KEY_CONTENT_STYLE_SINGLE_ITEM = "android.media.browse.CONTENT_STYLE_SINGLE_ITEM_HINT";

    @UnstableApi
    public static final String EXTRAS_KEY_DOWNLOAD_STATUS = "android.media.extra.DOWNLOAD_STATUS";
    public static final String EXTRAS_KEY_ERROR_RESOLUTION_ACTION_INTENT_COMPAT = "android.media.extras.ERROR_RESOLUTION_ACTION_INTENT";
    public static final String EXTRAS_KEY_ERROR_RESOLUTION_ACTION_LABEL_COMPAT = "android.media.extras.ERROR_RESOLUTION_ACTION_LABEL";

    @UnstableApi
    public static final String EXTRAS_KEY_ERROR_RESOLUTION_USING_CAR_APP_LIBRARY_INTENT_COMPAT = "androidx.media.PlaybackStateCompat.Extras.ERROR_RESOLUTION_USING_CAR_APP_LIBRARY_INTENT";

    @UnstableApi
    public static final String EXTRAS_KEY_IS_ADVERTISEMENT = "android.media.metadata.ADVERTISEMENT";

    @UnstableApi
    public static final String EXTRAS_KEY_IS_EXPLICIT = "android.media.IS_EXPLICIT";

    @UnstableApi
    public static final String EXTRAS_KEY_MEDIA_ART_SIZE_PIXELS = "android.media.extras.MEDIA_ART_SIZE_HINT_PIXELS";
    static final String EXTRAS_KEY_MEDIA_DESCRIPTION_COMPAT_TITLE = "androidx.media3.mediadescriptioncompat.title";

    @UnstableApi
    public static final String EXTRAS_KEY_MEDIA_ID_COMPAT = "androidx.media.PlaybackStateCompat.Extras.KEY_MEDIA_ID";

    @UnstableApi
    public static final String EXTRAS_KEY_MEDIA_TYPE_COMPAT = "androidx.media3.session.EXTRAS_KEY_MEDIA_TYPE_COMPAT";

    @UnstableApi
    public static final String EXTRAS_KEY_PLAYBACK_SPEED_COMPAT = "EXO_SPEED";

    @UnstableApi
    public static final String EXTRAS_KEY_ROOT_CHILDREN_LIMIT = "androidx.media.MediaBrowserCompat.Extras.KEY_ROOT_CHILDREN_LIMIT";
    public static final String EXTRAS_KEY_SLOT_RESERVATION_SEEK_TO_NEXT = "android.media.playback.ALWAYS_RESERVE_SPACE_FOR.ACTION_SKIP_TO_NEXT";
    public static final String EXTRAS_KEY_SLOT_RESERVATION_SEEK_TO_PREV = "android.media.playback.ALWAYS_RESERVE_SPACE_FOR.ACTION_SKIP_TO_PREVIOUS";

    @UnstableApi
    public static final long EXTRAS_VALUE_ATTRIBUTE_PRESENT = 1;

    @UnstableApi
    public static final int EXTRAS_VALUE_COMPLETION_STATUS_FULLY_PLAYED = 2;

    @UnstableApi
    public static final int EXTRAS_VALUE_COMPLETION_STATUS_NOT_PLAYED = 0;

    @UnstableApi
    public static final int EXTRAS_VALUE_COMPLETION_STATUS_PARTIALLY_PLAYED = 1;

    @UnstableApi
    public static final int EXTRAS_VALUE_CONTENT_STYLE_CATEGORY_GRID_ITEM = 4;

    @UnstableApi
    public static final int EXTRAS_VALUE_CONTENT_STYLE_CATEGORY_LIST_ITEM = 3;

    @UnstableApi
    public static final int EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM = 2;

    @UnstableApi
    public static final int EXTRAS_VALUE_CONTENT_STYLE_LIST_ITEM = 1;

    @UnstableApi
    public static final long EXTRAS_VALUE_STATUS_DOWNLOADED = 2;

    @UnstableApi
    public static final long EXTRAS_VALUE_STATUS_DOWNLOADING = 1;

    @UnstableApi
    public static final long EXTRAS_VALUE_STATUS_NOT_DOWNLOADED = 0;

    @UnstableApi
    public static final String EXTRA_KEY_MEDIA_ID = "androidx.media.utils.extras.KEY_CUSTOM_BROWSER_ACTION_MEDIA_ITEM_ID";

    @UnstableApi
    public static final String EXTRA_KEY_ROOT_CHILDREN_BROWSABLE_ONLY = "androidx.media3.session.LibraryParams.Extras.KEY_ROOT_CHILDREN_BROWSABLE_ONLY";
    static final String SESSION_COMMAND_MEDIA3_PLAY_REQUEST = "androidx.media3.session.SESSION_COMMAND_MEDIA3_PLAY_REQUEST";
    static final String SESSION_COMMAND_ON_CAPTIONING_ENABLED_CHANGED = "androidx.media3.session.SESSION_COMMAND_ON_CAPTIONING_ENABLED_CHANGED";
    static final String SESSION_COMMAND_REQUEST_SESSION3_TOKEN = "androidx.media3.session.SESSION_COMMAND_REQUEST_SESSION3_TOKEN";

    private MediaConstants() {
    }
}
