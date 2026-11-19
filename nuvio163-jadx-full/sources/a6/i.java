package a6;

import android.net.Uri;
import android.provider.BaseColumns;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public abstract class i implements BaseColumns {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final Uri f598a = Uri.parse("content://android.media.tv/channel");

    static {
        HashMap map = new HashMap();
        map.put("VIDEO_FORMAT_480I", "VIDEO_RESOLUTION_SD");
        map.put("VIDEO_FORMAT_480P", "VIDEO_RESOLUTION_ED");
        map.put("VIDEO_FORMAT_576I", "VIDEO_RESOLUTION_SD");
        map.put("VIDEO_FORMAT_576P", "VIDEO_RESOLUTION_ED");
        map.put("VIDEO_FORMAT_720P", "VIDEO_RESOLUTION_HD");
        map.put("VIDEO_FORMAT_1080I", "VIDEO_RESOLUTION_HD");
        map.put("VIDEO_FORMAT_1080P", "VIDEO_RESOLUTION_FHD");
        map.put("VIDEO_FORMAT_2160P", "VIDEO_RESOLUTION_UHD");
        map.put("VIDEO_FORMAT_4320P", "VIDEO_RESOLUTION_UHD");
    }
}
