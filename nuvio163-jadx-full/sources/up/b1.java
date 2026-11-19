package up;

import java.util.HashMap;

/* JADX INFO: loaded from: classes5.dex */
public class b1 {

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public static final Integer[] f60066g = new Integer[64];

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final String f60069c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final int f60070d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public String f60071e;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final HashMap f60067a = new HashMap();

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final HashMap f60068b = new HashMap();

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public int f60072f = Integer.MAX_VALUE;

    static {
        int i10 = 0;
        while (true) {
            Integer[] numArr = f60066g;
            if (i10 >= numArr.length) {
                return;
            }
            numArr[i10] = new Integer(i10);
            i10++;
        }
    }

    public b1(String str, int i10) {
        this.f60069c = str;
        this.f60070d = i10;
    }

    public static Integer f(int i10) {
        return (i10 < 0 || i10 >= 64) ? new Integer(i10) : f60066g[i10];
    }

    public final void a(int i10, String str) {
        b(i10);
        Integer numF = f(i10);
        String strD = d(str);
        this.f60067a.put(strD, numF);
        this.f60068b.put(numF, strD);
    }

    public void b(int i10) {
        if (i10 < 0 || i10 > this.f60072f) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(this.f60069c);
            stringBuffer.append(" ");
            stringBuffer.append(i10);
            stringBuffer.append("is out of range");
            throw new IllegalArgumentException(stringBuffer.toString());
        }
    }

    public final String c(int i10) {
        b(i10);
        String str = (String) this.f60068b.get(f(i10));
        if (str != null) {
            return str;
        }
        String string = Integer.toString(i10);
        if (this.f60071e == null) {
            return string;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.f60071e);
        stringBuffer.append(string);
        return stringBuffer.toString();
    }

    public final String d(String str) {
        int i10 = this.f60070d;
        if (i10 == 2) {
            return str.toUpperCase();
        }
        return i10 == 3 ? str.toLowerCase() : str;
    }

    public final void e(String str) {
        this.f60071e = d(str);
    }
}
