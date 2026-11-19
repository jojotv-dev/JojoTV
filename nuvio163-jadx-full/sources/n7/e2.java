package n7;

import androidx.media3.extractor.text.ttml.TtmlNode;
import java.util.HashMap;

/* JADX INFO: loaded from: classes3.dex */
public abstract class e2 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final HashMap f39586a;

    static {
        HashMap map = new HashMap(13);
        f39586a = map;
        map.put("normal", 400);
        map.put(TtmlNode.BOLD, 700);
        a0.i.y(1, map, "bolder", -1, "lighter");
        a0.i.y(100, map, "100", 200, "200");
        map.put("300", 300);
        map.put("400", 400);
        a0.i.y(500, map, "500", 600, "600");
        map.put("700", 700);
        map.put("800", 800);
        map.put("900", 900);
    }

    public static Integer a(String str) {
        return (Integer) f39586a.get(str);
    }
}
