package com.fasterxml.jackson.databind.util;

import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
public class ExceptionUtil {
    private ExceptionUtil() {
    }

    private static boolean isFatal(Throwable th2) {
        return (th2 instanceof VirtualMachineError) || (th2 instanceof ThreadDeath) || (th2 instanceof InterruptedException) || (th2 instanceof ClassCircularityError) || (th2 instanceof ClassFormatError) || (th2 instanceof IncompatibleClassChangeError) || (th2 instanceof BootstrapMethodError) || (th2 instanceof VerifyError);
    }

    public static void rethrowIfFatal(Throwable th2) {
        if (isFatal(th2)) {
            if (th2 instanceof Error) {
                throw ((Error) th2);
            }
            if (!(th2 instanceof RuntimeException)) {
                throw new RuntimeException(th2);
            }
            throw ((RuntimeException) th2);
        }
    }

    public static <T> T throwSneaky(IOException iOException) throws Throwable {
        _sneaky(iOException);
        return null;
    }

    private static <E extends Throwable> void _sneaky(Throwable th2) throws Throwable {
        throw th2;
    }
}
