package n7;

import java.util.HashMap;

/* JADX INFO: loaded from: classes3.dex */
public abstract class d2 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final HashMap f39582a;

    static {
        HashMap map = new HashMap(9);
        f39582a = map;
        map.put("xx-small", new e0(0.694f, 7));
        map.put("x-small", new e0(0.833f, 7));
        map.put("small", new e0(10.0f, 7));
        map.put("medium", new e0(12.0f, 7));
        map.put("large", new e0(14.4f, 7));
        map.put("x-large", new e0(17.3f, 7));
        map.put("xx-large", new e0(20.7f, 7));
        map.put("smaller", new e0(83.33f, 9));
        map.put("larger", new e0(120.0f, 9));
    }

    public static e0 a(String str) {
        return (e0) f39582a.get(str);
    }
}
