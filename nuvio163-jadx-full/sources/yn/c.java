package yn;

import android.os.AsyncTask;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes5.dex */
public final class c extends ThreadPoolExecutor implements AutoCloseable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final int f65415b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final int f65416c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final b f65417e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final ArrayBlockingQueue f65418i;

    static {
        int iAvailableProcessors = Runtime.getRuntime().availableProcessors();
        f65415b = Math.max(2, Math.min(iAvailableProcessors - 1, 4));
        f65416c = (iAvailableProcessors * 2) + 1;
        f65417e = new b();
        f65418i = new ArrayBlockingQueue(128);
    }

    @Override // java.lang.AutoCloseable
    public final /* synthetic */ void close() {
        boolean zIsTerminated;
        if (this == ForkJoinPool.commonPool() || (zIsTerminated = isTerminated())) {
            return;
        }
        shutdown();
        boolean z2 = false;
        while (!zIsTerminated) {
            try {
                zIsTerminated = awaitTermination(1L, TimeUnit.DAYS);
            } catch (InterruptedException unused) {
                if (!z2) {
                    shutdownNow();
                    z2 = true;
                }
            }
        }
        if (z2) {
            Thread.currentThread().interrupt();
        }
    }

    /* JADX WARN: Code duplicated, block: B:17:0x0050  */
    /* JADX WARN: Code duplicated, block: B:18:0x005b  */
    @Override // java.util.concurrent.ThreadPoolExecutor, java.util.concurrent.Executor
    public final void execute(Runnable runnable) {
        String name;
        int iIntValue;
        try {
            super.execute(runnable);
        } catch (RejectedExecutionException e10) {
            HashMap map = new HashMap();
            for (Runnable runnable2 : (Runnable[]) getQueue().toArray(new Runnable[0])) {
                Class<?> cls = runnable2.getClass();
                if (cls == a.class) {
                    throw new ClassCastException();
                }
                try {
                    if (cls.getEnclosingClass() == AsyncTask.class) {
                        Field declaredField = cls.getDeclaredField("this$0");
                        declaredField.setAccessible(true);
                        cls = declaredField.get(runnable2).getClass();
                    }
                } catch (IllegalAccessException | NoSuchFieldException unused) {
                }
                name = cls.getName();
                if (map.containsKey(name)) {
                    iIntValue = ((Integer) map.get(name)).intValue();
                } else {
                    iIntValue = 0;
                }
                map.put(name, Integer.valueOf(iIntValue + 1));
                name = cls.getName();
                if (map.containsKey(name)) {
                    iIntValue = ((Integer) map.get(name)).intValue();
                } else {
                    iIntValue = 0;
                }
                map.put(name, Integer.valueOf(iIntValue + 1));
            }
            StringBuilder sb2 = new StringBuilder("Prominent classes in AsyncTask: ");
            StringBuilder sb3 = new StringBuilder();
            for (Map.Entry entry : map.entrySet()) {
                if (((Integer) entry.getValue()).intValue() > 32) {
                    sb3.append((String) entry.getKey());
                    sb3.append(' ');
                }
            }
            sb2.append(sb3.length() == 0 ? "NO CLASSES FOUND" : sb3.toString());
            throw new RejectedExecutionException(sb2.toString(), e10);
        }
    }
}
