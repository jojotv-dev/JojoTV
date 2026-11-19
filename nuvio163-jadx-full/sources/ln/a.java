package ln;

import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;

/* JADX INFO: loaded from: classes5.dex */
public final class a implements AlgorithmParameterSpec {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final HashMap f34160a;

    static {
        a aVar = new a();
        a aVar2 = new a();
        a aVar3 = new a();
        HashMap map = new HashMap();
        f34160a = map;
        map.put("sqisign_lvl1", aVar);
        map.put("sqisign_lvl3", aVar2);
        map.put("sqisign_lvl5", aVar3);
    }
}
