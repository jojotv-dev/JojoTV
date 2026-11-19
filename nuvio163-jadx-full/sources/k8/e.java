package k8;

import io.ktor.util.date.GMTDateParser;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/* JADX INFO: loaded from: classes4.dex */
public final class e {

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public static final char[] f27983g = {'n', 'u', 'l', 'l'};

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public static final char[] f27984h = {'t', 'r', 'u', 'e'};

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final char[] f27985i = {'f', 'a', 'l', GMTDateParser.SECONDS, 'e'};

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public static final char[] f27986j = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', GMTDateParser.DAY_OF_MONTH, 'e', 'f'};
    public static final char[] k = {'\\', 'u', '0', '0'};

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    public static final char[] f27987l = {'\\', 'u'};

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final StringBuilder f27988a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final StringBuilder f27989b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final BitSet f27990c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public int f27991d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public boolean f27992e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public boolean f27993f;

    public e() {
        StringBuilder sb2 = new StringBuilder();
        this.f27990c = new BitSet();
        this.f27991d = 0;
        this.f27992e = true;
        this.f27988a = sb2;
        this.f27989b = new StringBuilder(10240);
    }

    public final e a(String str, Collection collection) {
        BitSet bitSet = this.f27990c;
        if (str == null) {
            h();
            int i10 = this.f27991d;
            this.f27991d = i10 + 1;
            bitSet.set(i10, this.f27993f);
            this.f27993f = false;
            this.f27992e = true;
            j('[');
        } else {
            i(str);
            int i11 = this.f27991d;
            this.f27991d = i11 + 1;
            bitSet.set(i11, this.f27993f);
            this.f27993f = false;
            this.f27992e = true;
            j('[');
        }
        Iterator it = collection.iterator();
        while (it.hasNext()) {
            m(it.next());
        }
        c();
        return this;
    }

    /* JADX WARN: Code duplicated, block: B:38:0x0090  */
    public final void b(String str) {
        String str2;
        char c5;
        j('\"');
        int i10 = 0;
        char c10 = 0;
        while (i10 < str.length()) {
            char cCharAt = str.charAt(i10);
            if (cCharAt == '\f') {
                str2 = "\\f";
            } else if (cCharAt != '\r') {
                if (cCharAt == '\"') {
                    j('\\');
                    j(cCharAt);
                } else if (cCharAt != '/') {
                    if (cCharAt != '\\') {
                        switch (cCharAt) {
                            case '\b':
                                str2 = "\\b";
                                break;
                            case '\t':
                                str2 = "\\t";
                                break;
                            case '\n':
                                str2 = "\\n";
                                break;
                            default:
                                if (cCharAt < ' ' || ((cCharAt >= 128 && cCharAt < 160) || (cCharAt >= 8192 && cCharAt < 8448))) {
                                    char[] cArr = f27986j;
                                    if (cCharAt < 256) {
                                        l(k);
                                        j(cArr[(cCharAt >> 4) & 15]);
                                        c5 = cArr[cCharAt & 15];
                                    } else {
                                        l(f27987l);
                                        j(cArr[(cCharAt >> '\f') & 15]);
                                        j(cArr[(cCharAt >> '\b') & 15]);
                                        j(cArr[(cCharAt >> 4) & 15]);
                                        c5 = cArr[cCharAt & 15];
                                    }
                                    j(c5);
                                }
                                break;
                        }
                    } else {
                        j('\\');
                    }
                    j(cCharAt);
                } else {
                    if (c10 == '<') {
                        j('\\');
                    }
                    j(cCharAt);
                }
                i10++;
                c10 = cCharAt;
            } else {
                str2 = "\\r";
            }
            k(str2);
            i10++;
            c10 = cCharAt;
        }
        j('\"');
    }

    public final void c() {
        if (this.f27991d == 0) {
            throw new aj.c("Invalid call to end()", 6);
        }
        j(this.f27993f ? '}' : ']');
        this.f27992e = false;
        int i10 = this.f27991d - 1;
        this.f27991d = i10;
        this.f27993f = this.f27990c.get(i10);
    }

    public final void d() {
        StringBuilder sb2 = this.f27989b;
        try {
            this.f27988a.append((CharSequence) sb2.toString());
            sb2.setLength(0);
        } catch (IOException e10) {
            throw new aj.c(e10, 6);
        }
    }

    public final e e(String str, Map map) {
        BitSet bitSet = this.f27990c;
        if (str == null) {
            f();
        } else {
            i(str);
            int i10 = this.f27991d;
            this.f27991d = i10 + 1;
            bitSet.set(i10, this.f27993f);
            this.f27993f = true;
            this.f27992e = true;
            j('{');
        }
        for (Map.Entry entry : map.entrySet()) {
            Object value = entry.getValue();
            if (!(entry.getKey() instanceof String)) {
                StringBuilder sb2 = new StringBuilder("Invalid key type for map: ");
                sb2.append(entry.getKey() == null ? "null" : entry.getKey().getClass());
                throw new aj.c(sb2.toString(), 6);
            }
            String str2 = (String) entry.getKey();
            if (value == null) {
                i(str2);
                l(f27983g);
            } else if (value instanceof String) {
                i(str2);
                b((String) value);
            } else if (value instanceof Number) {
                i(str2);
                k(((Number) value).toString());
            } else if (value instanceof Boolean) {
                boolean zBooleanValue = ((Boolean) value).booleanValue();
                i(str2);
                l(zBooleanValue ? f27984h : f27985i);
            } else if (value instanceof Collection) {
                a(str2, (Collection) value);
            } else if (value instanceof Map) {
                e(str2, (Map) value);
            } else {
                if (!value.getClass().isArray()) {
                    throw new aj.c("Unable to handle type: " + value.getClass(), 6);
                }
                int length = Array.getLength(value);
                i(str2);
                int i11 = this.f27991d;
                this.f27991d = i11 + 1;
                bitSet.set(i11, this.f27993f);
                this.f27993f = false;
                this.f27992e = true;
                j('[');
                for (int i12 = 0; i12 < length; i12++) {
                    m(Array.get(value, i12));
                }
                c();
            }
        }
        c();
        return this;
    }

    public final void f() {
        h();
        int i10 = this.f27991d;
        this.f27991d = i10 + 1;
        this.f27990c.set(i10, this.f27993f);
        this.f27993f = true;
        this.f27992e = true;
        j('{');
    }

    public final void g() {
        if (this.f27992e) {
            this.f27992e = false;
        } else {
            if (this.f27991d == 0) {
                throw new aj.c("Invalid call to emit a value in a finished JSON writer", 6);
            }
            j(',');
        }
    }

    public final void h() {
        if (this.f27993f) {
            throw new aj.c("Invalid call to emit a keyless value while writing an object", 6);
        }
        g();
    }

    public final void i(String str) {
        if (!this.f27993f) {
            throw new aj.c("Invalid call to emit a key value while not writing an object", 6);
        }
        g();
        b(str);
        j(':');
    }

    public final void j(char c5) {
        StringBuilder sb2 = this.f27989b;
        sb2.append(c5);
        if (sb2.length() > 10240) {
            d();
        }
    }

    public final void k(String str) {
        StringBuilder sb2 = this.f27989b;
        sb2.append(str);
        if (sb2.length() > 10240) {
            d();
        }
    }

    public final void l(char[] cArr) {
        StringBuilder sb2 = this.f27989b;
        sb2.append(cArr);
        if (sb2.length() > 10240) {
            d();
        }
    }

    public final e m(Object obj) {
        if (obj == null) {
            h();
            l(f27983g);
            return this;
        }
        if (obj instanceof String) {
            h();
            b((String) obj);
            return this;
        }
        if (obj instanceof Number) {
            h();
            k(((Number) obj).toString());
            return this;
        }
        if (obj instanceof Boolean) {
            boolean zBooleanValue = ((Boolean) obj).booleanValue();
            h();
            l(zBooleanValue ? f27984h : f27985i);
            return this;
        }
        if (obj instanceof Collection) {
            a(null, (Collection) obj);
            return this;
        }
        if (obj instanceof Map) {
            e(null, (Map) obj);
            return this;
        }
        if (!obj.getClass().isArray()) {
            throw new aj.c("Unable to handle type: " + obj.getClass(), 6);
        }
        int length = Array.getLength(obj);
        h();
        int i10 = this.f27991d;
        this.f27991d = i10 + 1;
        this.f27990c.set(i10, this.f27993f);
        this.f27993f = false;
        this.f27992e = true;
        j('[');
        for (int i11 = 0; i11 < length; i11++) {
            m(Array.get(obj, i11));
        }
        c();
        return this;
    }
}
