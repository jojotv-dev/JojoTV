package up;

import com.eclipsesource.v8.Platform;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes5.dex */
public final class m2 {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final m2 f60212c = new m2(0);

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final m2 f60213d = new m2(1);

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final m2 f60214e = new m2(2);

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public int f60215a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public Serializable f60216b;

    public m2(int i10, w1 w1Var) {
        if (i10 < 0 || i10 > 6) {
            throw new IllegalArgumentException("invalid type");
        }
        this.f60215a = i10;
        this.f60216b = w1Var;
    }

    public static m2 b(int i10) {
        switch (i10) {
            case 0:
                return f60212c;
            case 1:
                return f60213d;
            case 2:
                return f60214e;
            case 3:
            case 4:
            case 5:
            case 6:
                m2 m2Var = new m2();
                m2Var.f60215a = i10;
                m2Var.f60216b = null;
                return m2Var;
            default:
                throw new IllegalArgumentException("invalid type");
        }
    }

    public final void a(w1 w1Var) {
        if (this.f60216b == null) {
            this.f60216b = new ArrayList();
        }
        ((List) this.f60216b).add(w1Var);
    }

    public final String toString() {
        switch (this.f60215a) {
            case 0:
                return Platform.UNKNOWN;
            case 1:
                return "NXDOMAIN";
            case 2:
                return "NXRRSET";
            case 3:
                StringBuffer stringBuffer = new StringBuffer("delegation: ");
                stringBuffer.append(this.f60216b);
                return stringBuffer.toString();
            case 4:
                StringBuffer stringBuffer2 = new StringBuffer("CNAME: ");
                stringBuffer2.append(this.f60216b);
                return stringBuffer2.toString();
            case 5:
                StringBuffer stringBuffer3 = new StringBuffer("DNAME: ");
                stringBuffer3.append(this.f60216b);
                return stringBuffer3.toString();
            case 6:
                return "successful";
            default:
                throw new IllegalStateException();
        }
    }

    public m2(int i10) {
        if (i10 >= 0 && i10 <= 6) {
            this.f60215a = i10;
            this.f60216b = null;
            return;
        }
        throw new IllegalArgumentException("invalid type");
    }
}
