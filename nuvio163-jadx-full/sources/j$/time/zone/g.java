package j$.time.zone;

import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public final class g implements PrivilegedAction {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ List f26746a;

    public g(List list) {
        this.f26746a = list;
    }

    @Override // java.security.PrivilegedAction
    public final Object run() {
        String property = System.getProperty("java.time.zone.DefaultZoneRulesProvider");
        if (property != null) {
            try {
                h hVar = (h) h.class.cast(Class.forName(property, true, h.class.getClassLoader()).newInstance());
                h.b(hVar);
                ((ArrayList) this.f26746a).add(hVar);
                return null;
            } catch (Exception e10) {
                throw new Error(e10);
            }
        }
        h.b(new h());
        return null;
    }
}
