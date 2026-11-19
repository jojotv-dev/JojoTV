package sp;

import java.io.PrintStream;

/* JADX INFO: loaded from: classes3.dex */
public abstract class d {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final int f56918a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final int f56919b;

    static {
        int i10;
        String[] strArr = {"System.out", "stdout", "sysout"};
        String property = System.getProperty("slf4j.internal.report.stream");
        int i11 = 2;
        if (property != null && !property.isEmpty()) {
            int i12 = 0;
            while (true) {
                if (i12 >= 3) {
                    i10 = 1;
                    break;
                } else {
                    if (strArr[i12].equalsIgnoreCase(property)) {
                        i10 = 2;
                        break;
                    }
                    i12++;
                }
            }
        } else {
            i10 = 1;
            break;
        }
        f56918a = i10;
        String property2 = System.getProperty("slf4j.internal.verbosity");
        if (property2 != null && !property2.isEmpty()) {
            if (property2.equalsIgnoreCase("DEBUG")) {
                i11 = 1;
            } else if (property2.equalsIgnoreCase("ERROR")) {
                i11 = 4;
            } else if (property2.equalsIgnoreCase("WARN")) {
                i11 = 3;
            }
        }
        f56919b = i11;
    }

    public static final void a(String str) {
        c().println("SLF4J(E): " + str);
    }

    public static final void b(String str, Throwable th2) {
        c().println("SLF4J(E): " + str);
        c().println("SLF4J(E): Reported exception:");
        th2.printStackTrace(c());
    }

    public static PrintStream c() {
        return u4.i.b(f56918a) != 1 ? System.err : System.out;
    }

    public static void d(String str) {
        if (u4.i.b(2) >= u4.i.b(f56919b)) {
            c().println("SLF4J(I): " + str);
        }
    }

    public static final void e(String str) {
        if (u4.i.b(3) >= u4.i.b(f56919b)) {
            c().println("SLF4J(W): " + str);
        }
    }
}
