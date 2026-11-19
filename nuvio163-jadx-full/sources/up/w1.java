package up;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes5.dex */
public class w1 implements Serializable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public List f60306b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public short f60307c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public short f60308e;

    public static String e(Iterator it) {
        StringBuffer stringBuffer = new StringBuffer();
        while (it.hasNext()) {
            z1 z1Var = (z1) it.next();
            stringBuffer.append("[");
            stringBuffer.append(z1Var.k());
            stringBuffer.append("]");
            if (it.hasNext()) {
                stringBuffer.append(" ");
            }
        }
        return stringBuffer.toString();
    }

    public final synchronized z1 c() {
        if (this.f60306b.size() == 0) {
            throw new IllegalStateException("rrset is empty");
        }
        return (z1) this.f60306b.get(0);
    }

    public final synchronized Iterator d(boolean z2, boolean z10) {
        int i10;
        try {
            int size = this.f60306b.size();
            int i11 = z2 ? size - this.f60307c : this.f60307c;
            if (i11 == 0) {
                return Collections.EMPTY_LIST.iterator();
            }
            if (!z2) {
                i10 = size - this.f60307c;
            } else if (z10) {
                if (this.f60308e >= i11) {
                    this.f60308e = (short) 0;
                }
                i10 = this.f60308e;
                this.f60308e = (short) (i10 + 1);
            } else {
                i10 = 0;
            }
            ArrayList arrayList = new ArrayList(i11);
            if (z2) {
                arrayList.addAll(this.f60306b.subList(i10, i11));
                if (i10 != 0) {
                    arrayList.addAll(this.f60306b.subList(0, i10));
                }
            } else {
                arrayList.addAll(this.f60306b.subList(i10, size));
            }
            return arrayList.iterator();
        } catch (Throwable th2) {
            throw th2;
        }
    }

    public final void f(z1 z1Var) {
        List list = this.f60306b;
        if (z1Var instanceof v1) {
            list.add(z1Var);
            this.f60307c = (short) (this.f60307c + 1);
        } else if (this.f60307c == 0) {
            list.add(z1Var);
        } else {
            list.add(list.size() - this.f60307c, z1Var);
        }
    }

    public final int getType() {
        return c().g();
    }

    public String toString() {
        long j10;
        if (this.f60306b.size() == 0) {
            return "{empty}";
        }
        StringBuffer stringBuffer = new StringBuffer("{ ");
        StringBuffer stringBuffer2 = new StringBuffer();
        stringBuffer2.append(c().f60323b);
        stringBuffer2.append(" ");
        stringBuffer.append(stringBuffer2.toString());
        StringBuffer stringBuffer3 = new StringBuffer();
        synchronized (this) {
            j10 = c().f60326i;
        }
        stringBuffer3.append(j10);
        stringBuffer3.append(" ");
        stringBuffer.append(stringBuffer3.toString());
        StringBuffer stringBuffer4 = new StringBuffer();
        stringBuffer4.append(t.f60287a.c(c().f60325e));
        stringBuffer4.append(" ");
        stringBuffer.append(stringBuffer4.toString());
        StringBuffer stringBuffer5 = new StringBuffer();
        stringBuffer5.append(y2.f60317a.c(getType()));
        stringBuffer5.append(" ");
        stringBuffer.append(stringBuffer5.toString());
        stringBuffer.append(e(d(true, false)));
        if (this.f60307c > 0) {
            stringBuffer.append(" sigs: ");
            stringBuffer.append(e(d(false, false)));
        }
        stringBuffer.append(" }");
        return stringBuffer.toString();
    }
}
