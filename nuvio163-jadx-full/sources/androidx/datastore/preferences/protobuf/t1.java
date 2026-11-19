package androidx.datastore.preferences.protobuf;

import java.lang.reflect.Field;
import sun.misc.Unsafe;

/* JADX INFO: loaded from: classes.dex */
public abstract class t1 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Unsafe f2567a;

    public t1(Unsafe unsafe) {
        this.f2567a = unsafe;
    }

    public final int a(Class cls) {
        return this.f2567a.arrayBaseOffset(cls);
    }

    public final int b(Class cls) {
        return this.f2567a.arrayIndexScale(cls);
    }

    public abstract boolean c(long j10, Object obj);

    public abstract byte d(long j10, Object obj);

    public abstract double e(long j10, Object obj);

    public abstract float f(long j10, Object obj);

    public final int g(long j10, Object obj) {
        return this.f2567a.getInt(obj, j10);
    }

    public final long h(long j10, Object obj) {
        return this.f2567a.getLong(obj, j10);
    }

    public final Object i(long j10, Object obj) {
        return this.f2567a.getObject(obj, j10);
    }

    public final long j(Field field) {
        return this.f2567a.objectFieldOffset(field);
    }

    public abstract void k(long j10, Object obj, boolean z2);

    public abstract void l(Object obj, long j10, byte b5);

    public abstract void m(Object obj, long j10, double d10);

    public abstract void n(Object obj, long j10, float f10);

    public final void o(int i10, long j10, Object obj) {
        this.f2567a.putInt(obj, j10, i10);
    }

    public final void p(long j10, Object obj, long j11) {
        this.f2567a.putLong(obj, j10, j11);
    }

    public final void q(long j10, Object obj, Object obj2) {
        this.f2567a.putObject(obj, j10, obj2);
    }

    public boolean r() {
        Unsafe unsafe = this.f2567a;
        if (unsafe == null) {
            return false;
        }
        try {
            Class<?> cls = unsafe.getClass();
            cls.getMethod("objectFieldOffset", Field.class);
            cls.getMethod("arrayBaseOffset", Class.class);
            cls.getMethod("arrayIndexScale", Class.class);
            Class cls2 = Long.TYPE;
            cls.getMethod("getInt", Object.class, cls2);
            cls.getMethod("putInt", Object.class, cls2, Integer.TYPE);
            cls.getMethod("getLong", Object.class, cls2);
            cls.getMethod("putLong", Object.class, cls2, cls2);
            cls.getMethod("getObject", Object.class, cls2);
            cls.getMethod("putObject", Object.class, cls2, Object.class);
            return true;
        } catch (Throwable th2) {
            u1.a(th2);
            return false;
        }
    }

    public abstract boolean s();
}
