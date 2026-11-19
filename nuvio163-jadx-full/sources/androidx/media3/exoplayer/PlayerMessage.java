package androidx.media3.exoplayer;

import android.os.Looper;
import androidx.media3.common.C;
import androidx.media3.common.IllegalSeekPositionException;
import androidx.media3.common.Timeline;
import androidx.media3.common.util.Assertions;
import androidx.media3.common.util.Clock;
import androidx.media3.common.util.UnstableApi;
import java.util.concurrent.TimeoutException;

/* JADX INFO: loaded from: classes.dex */
@UnstableApi
public final class PlayerMessage {
    private final Clock clock;
    private boolean isCanceled;
    private boolean isDelivered;
    private boolean isProcessed;
    private boolean isSent;
    private Looper looper;
    private int mediaItemIndex;
    private Object payload;
    private final Sender sender;
    private final Target target;
    private final Timeline timeline;
    private int type;
    private long positionMs = C.TIME_UNSET;
    private boolean deleteAfterDelivery = true;

    /* JADX INFO: loaded from: classes3.dex */
    public interface Sender {
        void sendMessage(PlayerMessage playerMessage);
    }

    /* JADX INFO: loaded from: classes3.dex */
    public interface Target {
        void handleMessage(int i10, Object obj);
    }

    public PlayerMessage(Sender sender, Target target, Timeline timeline, int i10, Clock clock, Looper looper) {
        this.sender = sender;
        this.target = target;
        this.timeline = timeline;
        this.looper = looper;
        this.clock = clock;
        this.mediaItemIndex = i10;
    }

    public synchronized boolean blockUntilDelivered() {
        try {
            Assertions.checkState(this.isSent);
            Assertions.checkState(this.looper.getThread() != Thread.currentThread());
            while (!this.isProcessed) {
                wait();
            }
        } catch (Throwable th2) {
            throw th2;
        }
        return this.isDelivered;
    }

    public synchronized PlayerMessage cancel() {
        Assertions.checkState(this.isSent);
        this.isCanceled = true;
        markAsProcessed(false);
        return this;
    }

    public boolean getDeleteAfterDelivery() {
        return this.deleteAfterDelivery;
    }

    public Looper getLooper() {
        return this.looper;
    }

    public int getMediaItemIndex() {
        return this.mediaItemIndex;
    }

    public Object getPayload() {
        return this.payload;
    }

    public long getPositionMs() {
        return this.positionMs;
    }

    public Target getTarget() {
        return this.target;
    }

    public Timeline getTimeline() {
        return this.timeline;
    }

    public int getType() {
        return this.type;
    }

    public synchronized boolean isCanceled() {
        return this.isCanceled;
    }

    public synchronized void markAsProcessed(boolean z2) {
        this.isDelivered = z2 | this.isDelivered;
        this.isProcessed = true;
        notifyAll();
    }

    public PlayerMessage send() {
        Assertions.checkState(!this.isSent);
        if (this.positionMs == C.TIME_UNSET) {
            Assertions.checkArgument(this.deleteAfterDelivery);
        }
        this.isSent = true;
        this.sender.sendMessage(this);
        return this;
    }

    public PlayerMessage setDeleteAfterDelivery(boolean z2) {
        Assertions.checkState(!this.isSent);
        this.deleteAfterDelivery = z2;
        return this;
    }

    public PlayerMessage setLooper(Looper looper) {
        Assertions.checkState(!this.isSent);
        this.looper = looper;
        return this;
    }

    public PlayerMessage setPayload(Object obj) {
        Assertions.checkState(!this.isSent);
        this.payload = obj;
        return this;
    }

    public PlayerMessage setPosition(long j10) {
        Assertions.checkState(!this.isSent);
        this.positionMs = j10;
        return this;
    }

    public PlayerMessage setType(int i10) {
        Assertions.checkState(!this.isSent);
        this.type = i10;
        return this;
    }

    public PlayerMessage setPosition(int i10, long j10) {
        Assertions.checkState(!this.isSent);
        Assertions.checkArgument(j10 != C.TIME_UNSET);
        if (i10 >= 0 && (this.timeline.isEmpty() || i10 < this.timeline.getWindowCount())) {
            this.mediaItemIndex = i10;
            this.positionMs = j10;
            return this;
        }
        throw new IllegalSeekPositionException(this.timeline, i10, j10);
    }

    public synchronized boolean blockUntilDelivered(long j10) {
        boolean z2;
        try {
            Assertions.checkState(this.isSent);
            Assertions.checkState(this.looper.getThread() != Thread.currentThread());
            long jElapsedRealtime = this.clock.elapsedRealtime() + j10;
            while (true) {
                z2 = this.isProcessed;
                if (z2 || j10 <= 0) {
                    break;
                }
                this.clock.onThreadBlocked();
                wait(j10);
                j10 = jElapsedRealtime - this.clock.elapsedRealtime();
            }
            if (!z2) {
                throw new TimeoutException("Message delivery timed out.");
            }
        } catch (Throwable th2) {
            throw th2;
        }
        return this.isDelivered;
    }
}
