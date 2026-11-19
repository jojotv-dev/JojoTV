package f6;

import android.os.Build;
import java.util.HashSet;

/* JADX INFO: loaded from: classes3.dex */
public abstract class c implements f {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final HashSet f16199c = new HashSet();

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f16200a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f16201b;

    public c(String str, String str2) {
        this.f16200a = str;
        this.f16201b = str2;
        f16199c.add(this);
    }

    public abstract boolean a();

    public boolean b() {
        HashSet hashSet = a.f16197a;
        String str = this.f16201b;
        if (hashSet.contains(str)) {
            return true;
        }
        String str2 = Build.TYPE;
        return ("eng".equals(str2) || "userdebug".equals(str2)) && hashSet.contains(str.concat(":dev"));
    }
}
