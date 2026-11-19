package androidx.media3.common.util;

import a0.i;
import android.text.TextUtils;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.UnknownHostException;

/* JADX INFO: loaded from: classes.dex */
@UnstableApi
public final class Log {
    public static final int LOG_LEVEL_ALL = 0;
    public static final int LOG_LEVEL_ERROR = 3;
    public static final int LOG_LEVEL_INFO = 1;
    public static final int LOG_LEVEL_OFF = Integer.MAX_VALUE;
    public static final int LOG_LEVEL_WARNING = 2;
    private static int logLevel = 0;
    private static boolean logStackTraces = true;
    private static final Object lock = new Object();
    private static Logger logger = Logger.DEFAULT;

    /* JADX INFO: loaded from: classes3.dex */
    @Target({ElementType.TYPE_USE})
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface LogLevel {
    }

    public interface Logger {
        public static final Logger DEFAULT = new Logger() { // from class: androidx.media3.common.util.Log.Logger.1
            @Override // androidx.media3.common.util.Log.Logger
            public void d(String str, String str2, Throwable th2) {
                android.util.Log.d(str, Log.appendThrowableString(str2, th2));
            }

            @Override // androidx.media3.common.util.Log.Logger
            public void e(String str, String str2, Throwable th2) {
                android.util.Log.e(str, Log.appendThrowableString(str2, th2));
            }

            @Override // androidx.media3.common.util.Log.Logger
            public void i(String str, String str2, Throwable th2) {
                android.util.Log.i(str, Log.appendThrowableString(str2, th2));
            }

            @Override // androidx.media3.common.util.Log.Logger
            public void w(String str, String str2, Throwable th2) {
                android.util.Log.w(str, Log.appendThrowableString(str2, th2));
            }
        };

        void d(String str, String str2, Throwable th2);

        void e(String str, String str2, Throwable th2);

        void i(String str, String str2, Throwable th2);

        void w(String str, String str2, Throwable th2);
    }

    private Log() {
    }

    public static String appendThrowableString(String str, Throwable th2) {
        String throwableString = getThrowableString(th2);
        if (TextUtils.isEmpty(throwableString)) {
            return str;
        }
        StringBuilder sbS = i.s(str, "\n  ");
        sbS.append(throwableString.replace("\n", "\n  "));
        sbS.append('\n');
        return sbS.toString();
    }

    public static void d(String str, String str2) {
        synchronized (lock) {
            try {
                if (logLevel == 0) {
                    logger.d(str, str2, null);
                }
            } catch (Throwable th2) {
                throw th2;
            }
        }
    }

    public static void e(String str, String str2) {
        synchronized (lock) {
            try {
                if (logLevel <= 3) {
                    logger.e(str, str2, null);
                }
            } catch (Throwable th2) {
                throw th2;
            }
        }
    }

    public static int getLogLevel() {
        int i10;
        synchronized (lock) {
            i10 = logLevel;
        }
        return i10;
    }

    public static String getThrowableString(Throwable th2) {
        if (th2 == null) {
            return null;
        }
        synchronized (lock) {
            try {
                if (isCausedByUnknownHostException(th2)) {
                    return "UnknownHostException (no network)";
                }
                if (logStackTraces) {
                    return android.util.Log.getStackTraceString(th2).trim().replace("\t", "    ");
                }
                return th2.getMessage();
            } catch (Throwable th3) {
                throw th3;
            }
        }
    }

    public static void i(String str, String str2) {
        synchronized (lock) {
            try {
                if (logLevel <= 1) {
                    logger.i(str, str2, null);
                }
            } catch (Throwable th2) {
                throw th2;
            }
        }
    }

    private static boolean isCausedByUnknownHostException(Throwable th2) {
        while (th2 != null) {
            if (th2 instanceof UnknownHostException) {
                return true;
            }
            th2 = th2.getCause();
        }
        return false;
    }

    public static void setLogLevel(int i10) {
        synchronized (lock) {
            logLevel = i10;
        }
    }

    public static void setLogStackTraces(boolean z2) {
        synchronized (lock) {
            logStackTraces = z2;
        }
    }

    public static void setLogger(Logger logger2) {
        synchronized (lock) {
            logger = logger2;
        }
    }

    public static void w(String str, String str2) {
        synchronized (lock) {
            try {
                if (logLevel <= 2) {
                    logger.w(str, str2, null);
                }
            } catch (Throwable th2) {
                throw th2;
            }
        }
    }

    public static void d(String str, String str2, Throwable th2) {
        synchronized (lock) {
            try {
                if (logLevel == 0) {
                    logger.d(str, str2, th2);
                }
            } catch (Throwable th3) {
                throw th3;
            }
        }
    }

    public static void e(String str, String str2, Throwable th2) {
        synchronized (lock) {
            try {
                if (logLevel <= 3) {
                    logger.e(str, str2, th2);
                }
            } catch (Throwable th3) {
                throw th3;
            }
        }
    }

    public static void i(String str, String str2, Throwable th2) {
        synchronized (lock) {
            try {
                if (logLevel <= 1) {
                    logger.i(str, str2, th2);
                }
            } catch (Throwable th3) {
                throw th3;
            }
        }
    }

    public static void w(String str, String str2, Throwable th2) {
        synchronized (lock) {
            try {
                if (logLevel <= 2) {
                    logger.w(str, str2, th2);
                }
            } catch (Throwable th3) {
                throw th3;
            }
        }
    }
}
