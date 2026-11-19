package j$.time.zone;

import java.security.AccessController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/* JADX INFO: loaded from: classes2.dex */
public final class h {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final CopyOnWriteArrayList f26747b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final ConcurrentMap f26748c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static volatile Set f26749d;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Set f26750a;

    static {
        CopyOnWriteArrayList copyOnWriteArrayList = new CopyOnWriteArrayList();
        f26747b = copyOnWriteArrayList;
        f26748c = new ConcurrentHashMap(512, 0.75f, 2);
        ArrayList arrayList = new ArrayList();
        AccessController.doPrivileged(new g(arrayList));
        copyOnWriteArrayList.addAll(arrayList);
    }

    public static ZoneRules a(String str) {
        Objects.requireNonNull(str, "zoneId");
        ConcurrentHashMap concurrentHashMap = (ConcurrentHashMap) f26748c;
        h hVar = (h) concurrentHashMap.get(str);
        if (hVar == null) {
            if (concurrentHashMap.isEmpty()) {
                throw new f("No time-zone data files registered");
            }
            throw new f("Unknown time-zone ID: ".concat(str));
        }
        if (hVar.f26750a.contains(str)) {
            return new ZoneRules(TimeZone.getTimeZone(str));
        }
        throw new f("Not a built-in time zone: ".concat(str));
    }

    public static void b(h hVar) {
        Objects.requireNonNull(hVar, "provider");
        synchronized (h.class) {
            try {
                for (String str : hVar.f26750a) {
                    Objects.requireNonNull(str, "zoneId");
                    if (((h) ((ConcurrentHashMap) f26748c).putIfAbsent(str, hVar)) != null) {
                        throw new f("Unable to register zone as one already registered with that ID: " + str + ", currently loading from provider: " + hVar);
                    }
                }
                f26749d = Collections.unmodifiableSet(new HashSet(((ConcurrentHashMap) f26748c).keySet()));
            } catch (Throwable th2) {
                throw th2;
            }
        }
        f26747b.add(hVar);
    }

    public h() {
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        for (String str : TimeZone.getAvailableIDs()) {
            linkedHashSet.add(str);
        }
        this.f26750a = Collections.unmodifiableSet(linkedHashSet);
    }
}
