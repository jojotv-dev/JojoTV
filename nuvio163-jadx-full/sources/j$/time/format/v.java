package j$.time.format;

import j$.time.ZoneId;
import j$.time.temporal.ChronoField;
import j$.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/* JADX INFO: loaded from: classes2.dex */
public final class v {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final DateTimeFormatter f26624a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public boolean f26625b = true;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public boolean f26626c = true;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final ArrayList f26627d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public ArrayList f26628e;

    public v(DateTimeFormatter dateTimeFormatter) {
        ArrayList arrayList = new ArrayList();
        this.f26627d = arrayList;
        this.f26628e = null;
        this.f26624a = dateTimeFormatter;
        arrayList.add(new c0());
    }

    public final j$.time.chrono.j d() {
        j$.time.chrono.j jVar = c().f26557c;
        if (jVar != null) {
            return jVar;
        }
        j$.time.chrono.j jVar2 = this.f26624a.f26535e;
        return jVar2 == null ? j$.time.chrono.q.f26505c : jVar2;
    }

    public final boolean a(char c5, char c10) {
        if (this.f26625b) {
            return c5 == c10;
        }
        return b(c5, c10);
    }

    public final boolean h(CharSequence charSequence, int i10, CharSequence charSequence2, int i11, int i12) {
        if (i10 + i12 <= charSequence.length() && i11 + i12 <= charSequence2.length()) {
            if (this.f26625b) {
                for (int i13 = 0; i13 < i12; i13++) {
                    if (charSequence.charAt(i10 + i13) == charSequence2.charAt(i11 + i13)) {
                    }
                }
                return true;
            }
            for (int i14 = 0; i14 < i12; i14++) {
                char cCharAt = charSequence.charAt(i10 + i14);
                char cCharAt2 = charSequence2.charAt(i11 + i14);
                if (cCharAt == cCharAt2 || Character.toUpperCase(cCharAt) == Character.toUpperCase(cCharAt2) || Character.toLowerCase(cCharAt) == Character.toLowerCase(cCharAt2)) {
                }
            }
            return true;
        }
        return false;
    }

    public static boolean b(char c5, char c10) {
        return c5 == c10 || Character.toUpperCase(c5) == Character.toUpperCase(c10) || Character.toLowerCase(c5) == Character.toLowerCase(c10);
    }

    public final c0 c() {
        ArrayList arrayList = this.f26627d;
        return (c0) arrayList.get(arrayList.size() - 1);
    }

    public final Long e(ChronoField chronoField) {
        return (Long) ((HashMap) c().f26555a).get(chronoField);
    }

    public final int g(TemporalField temporalField, long j10, int i10, int i11) {
        Objects.requireNonNull(temporalField, "field");
        Long l7 = (Long) ((HashMap) c().f26555a).put(temporalField, Long.valueOf(j10));
        return (l7 == null || l7.longValue() == j10) ? i11 : ~i10;
    }

    public final void f(ZoneId zoneId) {
        Objects.requireNonNull(zoneId, "zone");
        c().f26556b = zoneId;
    }

    public final String toString() {
        return c().toString();
    }
}
