package org.bouncycastle.jcajce.provider.symmetric.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import qj.c1;

/* JADX INFO: loaded from: classes5.dex */
public abstract class q {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final Class f44090a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final Constructor f44091b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final Method f44092c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final Method f44093d;

    static {
        Constructor constructor;
        Method method;
        Class clsE = gj.f.E(q.class, "javax.crypto.spec.GCMParameterSpec");
        f44090a = clsE;
        Method method2 = null;
        if (clsE == null) {
            f44091b = null;
            f44092c = null;
            f44093d = null;
            return;
        }
        try {
            constructor = (Constructor) AccessController.doPrivileged(new n());
        } catch (PrivilegedActionException unused) {
            constructor = null;
        }
        f44091b = constructor;
        try {
            method = (Method) AccessController.doPrivileged(new p("getTLen", 2));
        } catch (PrivilegedActionException unused2) {
            method = null;
        }
        f44092c = method;
        try {
            method2 = (Method) AccessController.doPrivileged(new p("getIV", 2));
        } catch (PrivilegedActionException unused3) {
        }
        f44093d = method2;
    }

    public static AlgorithmParameterSpec a(c1 c1Var) throws InvalidParameterSpecException {
        try {
            pk.b bVarI = pk.b.i(c1Var);
            return (AlgorithmParameterSpec) f44091b.newInstance(Integer.valueOf(bVarI.f45188c * 8), rn.d.b(bVarI.f45187b));
        } catch (Exception e10) {
            throw new InvalidParameterSpecException("Construction failed: " + e10.getMessage());
        }
    }

    public static boolean b(AlgorithmParameterSpec algorithmParameterSpec) {
        Class cls = f44090a;
        return cls != null && cls.isInstance(algorithmParameterSpec);
    }
}
