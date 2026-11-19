package androidx.media3.effect;

import androidx.media3.common.VideoFrameProcessingException;
import androidx.media3.common.util.Assertions;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/* JADX INFO: loaded from: classes3.dex */
final class VideoFrameProcessingTaskExecutor {
    private static final long EXECUTOR_SERVICE_TIMEOUT_MS = 500;
    private final ErrorListener errorListener;
    private boolean shouldCancelTasks;
    private final boolean shouldShutdownExecutorService;
    private final ExecutorService singleThreadExecutorService;
    private final Future<Thread> threadFuture;
    private final Object lock = new Object();
    private final Queue<Task> highPriorityTasks = new ArrayDeque();

    public interface ErrorListener {
        void onError(VideoFrameProcessingException videoFrameProcessingException);
    }

    public interface Task {
        void run();
    }

    public VideoFrameProcessingTaskExecutor(ExecutorService executorService, boolean z2, ErrorListener errorListener) {
        this.singleThreadExecutorService = executorService;
        this.threadFuture = executorService.submit(new j0());
        this.shouldShutdownExecutorService = z2;
        this.errorListener = errorListener;
    }

    private void handleException(Exception exc) {
        synchronized (this.lock) {
            try {
                if (this.shouldCancelTasks) {
                    return;
                }
                this.shouldCancelTasks = true;
                this.errorListener.onError(VideoFrameProcessingException.from(exc));
            } catch (Throwable th2) {
                throw th2;
            }
        }
    }

    private boolean isRunningOnVideoFrameProcessingThread() throws InterruptedException {
        try {
            return Thread.currentThread() == this.threadFuture.get(500L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e10) {
            throw e10;
        } catch (Exception e11) {
            handleException(e11);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$flush$2(CountDownLatch countDownLatch) {
        synchronized (this.lock) {
            this.shouldCancelTasks = false;
        }
        countDownLatch.countDown();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$invoke$0(Task task) {
        try {
            task.run();
        } catch (Exception e10) {
            handleException(e10);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$submitWithHighPriority$1() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$wrapTaskAndSubmitToExecutorService$3(boolean z2, Task task) {
        Task taskPoll;
        try {
            synchronized (this.lock) {
                try {
                    if (this.shouldCancelTasks && z2) {
                        return;
                    }
                    while (true) {
                        synchronized (this.lock) {
                            taskPoll = this.highPriorityTasks.poll();
                        }
                        if (taskPoll == null) {
                            task.run();
                            return;
                        }
                        taskPoll.run();
                    }
                } catch (Throwable th2) {
                    throw th2;
                }
            }
        } catch (Exception e10) {
            handleException(e10);
        }
    }

    private Future<?> wrapTaskAndSubmitToExecutorService(final Task task, final boolean z2) {
        return this.singleThreadExecutorService.submit(new Runnable() { // from class: androidx.media3.effect.h0
            @Override // java.lang.Runnable
            public final void run() {
                this.f2870b.lambda$wrapTaskAndSubmitToExecutorService$3(z2, task);
            }
        });
    }

    public void flush() {
        synchronized (this.lock) {
            this.shouldCancelTasks = true;
            this.highPriorityTasks.clear();
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        wrapTaskAndSubmitToExecutorService(new e(this, countDownLatch, 6), false);
        countDownLatch.await();
    }

    public void invoke(Task task) {
        if (isRunningOnVideoFrameProcessingThread()) {
            try {
                task.run();
                return;
            } catch (Exception e10) {
                handleException(e10);
                return;
            }
        }
        try {
            this.singleThreadExecutorService.submit(new w(this, task, 5)).get(500L, TimeUnit.MILLISECONDS);
        } catch (RuntimeException | ExecutionException | TimeoutException e11) {
            handleException(e11);
        }
    }

    public void release(Task task) {
        Assertions.checkState(!isRunningOnVideoFrameProcessingThread());
        synchronized (this.lock) {
            this.shouldCancelTasks = true;
            this.highPriorityTasks.clear();
        }
        wrapTaskAndSubmitToExecutorService(task, false);
        if (this.shouldShutdownExecutorService) {
            this.singleThreadExecutorService.shutdown();
            if (this.singleThreadExecutorService.awaitTermination(500L, TimeUnit.MILLISECONDS)) {
                return;
            }
            this.errorListener.onError(new VideoFrameProcessingException("Release timed out. OpenGL resources may not be cleaned up properly."));
        }
    }

    public void submit(Task task, boolean z2) {
        synchronized (this.lock) {
            if (this.shouldCancelTasks && z2) {
                return;
            }
            try {
                wrapTaskAndSubmitToExecutorService(task, z2);
                e = null;
            } catch (RejectedExecutionException e10) {
                e = e10;
            }
            if (e != null) {
                handleException(e);
            }
        }
    }

    public void submitWithHighPriority(Task task) {
        synchronized (this.lock) {
            try {
                if (this.shouldCancelTasks) {
                    return;
                }
                this.highPriorityTasks.add(task);
                submit(new i0());
            } catch (Throwable th2) {
                throw th2;
            }
        }
    }

    public void verifyVideoFrameProcessingThread() {
        try {
            Assertions.checkState(isRunningOnVideoFrameProcessingThread());
        } catch (InterruptedException e10) {
            Thread.currentThread().interrupt();
            handleException(e10);
        }
    }

    public void submit(Task task) {
        submit(task, true);
    }
}
