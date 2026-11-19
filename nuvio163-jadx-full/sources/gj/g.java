package gj;

import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes5.dex */
public final class g extends ClassLoader {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final String f20251a = b.class.getName();

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final Class[] f20252b;

    static {
        Class cls = Integer.TYPE;
        f20252b = new Class[]{String.class, byte[].class, cls, cls};
    }

    public g(ClassLoader classLoader) {
        super(classLoader);
    }

    public final Class a(String str, byte[] bArr) {
        try {
            Method declaredMethod = ClassLoader.class.getDeclaredMethod("defineClass", f20252b);
            declaredMethod.setAccessible(true);
            return (Class) declaredMethod.invoke(getParent(), str, bArr, 0, Integer.valueOf(bArr.length));
        } catch (Exception unused) {
            return defineClass(str, bArr, 0, bArr.length);
        }
    }

    @Override // java.lang.ClassLoader
    public final synchronized Class loadClass(String str, boolean z2) {
        if (str.equals(f20251a)) {
            return b.class;
        }
        return super.loadClass(str, z2);
    }
}
