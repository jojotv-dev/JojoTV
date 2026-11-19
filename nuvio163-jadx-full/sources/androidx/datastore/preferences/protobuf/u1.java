package androidx.datastore.preferences.protobuf;

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteOrder;
import java.security.AccessController;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.Unsafe;

/* JADX INFO: loaded from: classes.dex */
public abstract class u1 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final Unsafe f2572a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final Class f2573b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final t1 f2574c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final boolean f2575d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final boolean f2576e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public static final long f2577f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public static final boolean f2578g;

    static {
        Unsafe unsafeJ = j();
        f2572a = unsafeJ;
        f2573b = c.f2420a;
        boolean zF = f(Long.TYPE);
        boolean zF2 = f(Integer.TYPE);
        t1 s1Var = null;
        if (unsafeJ != null) {
            if (!c.a()) {
                s1Var = new s1(unsafeJ);
            } else if (zF) {
                s1Var = new r1(unsafeJ);
            } else if (zF2) {
                s1Var = new q1(unsafeJ);
            }
        }
        f2574c = s1Var;
        f2575d = s1Var == null ? false : s1Var.s();
        f2576e = s1Var == null ? false : s1Var.r();
        f2577f = c(byte[].class);
        c(boolean[].class);
        d(boolean[].class);
        c(int[].class);
        d(int[].class);
        c(long[].class);
        d(long[].class);
        c(float[].class);
        d(float[].class);
        c(double[].class);
        d(double[].class);
        c(Object[].class);
        d(Object[].class);
        Field fieldE = e();
        if (fieldE != null && s1Var != null) {
            s1Var.j(fieldE);
        }
        f2578g = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;
    }

    public static void a(Throwable th2) {
        Logger.getLogger(u1.class.getName()).log(Level.WARNING, "platform method missing - proto runtime falling back to safer methods: " + th2);
    }

    public static Object b(Class cls) {
        try {
            return f2572a.allocateInstance(cls);
        } catch (InstantiationException e10) {
            throw new IllegalStateException(e10);
        }
    }

    public static int c(Class cls) {
        if (f2576e) {
            return f2574c.a(cls);
        }
        return -1;
    }

    public static void d(Class cls) {
        if (f2576e) {
            f2574c.b(cls);
        }
    }

    public static Field e() {
        Field declaredField;
        Field declaredField2;
        if (c.a()) {
            try {
                declaredField2 = Buffer.class.getDeclaredField("effectiveDirectAddress");
            } catch (Throwable unused) {
                declaredField2 = null;
            }
            if (declaredField2 != null) {
                return declaredField2;
            }
        }
        try {
            declaredField = Buffer.class.getDeclaredField("address");
        } catch (Throwable unused2) {
            declaredField = null;
        }
        if (declaredField == null || declaredField.getType() != Long.TYPE) {
            return null;
        }
        return declaredField;
    }

    public static boolean f(Class cls) {
        if (!c.a()) {
            return false;
        }
        try {
            Class cls2 = f2573b;
            Class cls3 = Boolean.TYPE;
            cls2.getMethod("peekLong", cls, cls3);
            cls2.getMethod("pokeLong", cls, Long.TYPE, cls3);
            Class cls4 = Integer.TYPE;
            cls2.getMethod("pokeInt", cls, cls4, cls3);
            cls2.getMethod("peekInt", cls, cls3);
            cls2.getMethod("pokeByte", cls, Byte.TYPE);
            cls2.getMethod("peekByte", cls);
            cls2.getMethod("pokeByteArray", cls, byte[].class, cls4, cls4);
            cls2.getMethod("peekByteArray", cls, byte[].class, cls4, cls4);
            return true;
        } catch (Throwable unused) {
            return false;
        }
    }

    public static byte g(long j10, byte[] bArr) {
        return f2574c.d(f2577f + j10, bArr);
    }

    public static byte h(long j10, Object obj) {
        return (byte) ((f2574c.g((-4) & j10, obj) >>> ((int) (((~j10) & 3) << 3))) & 255);
    }

    public static byte i(long j10, Object obj) {
        return (byte) ((f2574c.g((-4) & j10, obj) >>> ((int) ((j10 & 3) << 3))) & 255);
    }

    public static Unsafe j() {
        try {
            return (Unsafe) AccessController.doPrivileged(new p1());
        } catch (Throwable unused) {
            return null;
        }
    }

    public static void k(byte[] bArr, long j10, byte b5) {
        f2574c.l(bArr, f2577f + j10, b5);
    }

    public static void l(Object obj, long j10, byte b5) {
        long j11 = (-4) & j10;
        int iG = f2574c.g(j11, obj);
        int i10 = ((~((int) j10)) & 3) << 3;
        n(((255 & b5) << i10) | (iG & (~(255 << i10))), j11, obj);
    }

    public static void m(Object obj, long j10, byte b5) {
        long j11 = (-4) & j10;
        int i10 = (((int) j10) & 3) << 3;
        n(((255 & b5) << i10) | (f2574c.g(j11, obj) & (~(255 << i10))), j11, obj);
    }

    public static void n(int i10, long j10, Object obj) {
        f2574c.o(i10, j10, obj);
    }

    public static void o(long j10, Object obj, long j11) {
        f2574c.p(j10, obj, j11);
    }

    public static void p(long j10, Object obj, Object obj2) {
        f2574c.q(j10, obj, obj2);
    }
}
