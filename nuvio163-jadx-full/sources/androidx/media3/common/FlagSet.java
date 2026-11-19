package androidx.media3.common;

import android.util.SparseBooleanArray;
import androidx.media3.common.util.Assertions;
import androidx.media3.common.util.UnstableApi;

/* JADX INFO: loaded from: classes.dex */
@UnstableApi
public final class FlagSet {
    private final SparseBooleanArray flags;

    public boolean contains(int i10) {
        return this.flags.get(i10);
    }

    public boolean containsAny(int... iArr) {
        for (int i10 : iArr) {
            if (contains(i10)) {
                return true;
            }
        }
        return false;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof FlagSet) {
            return this.flags.equals(((FlagSet) obj).flags);
        }
        return false;
    }

    public int get(int i10) {
        Assertions.checkIndex(i10, 0, size());
        return this.flags.keyAt(i10);
    }

    public int hashCode() {
        return this.flags.hashCode();
    }

    public int size() {
        return this.flags.size();
    }

    public static final class Builder {
        private boolean buildCalled;
        private final SparseBooleanArray flags = new SparseBooleanArray();

        public Builder add(int i10) {
            Assertions.checkState(!this.buildCalled);
            this.flags.append(i10, true);
            return this;
        }

        public Builder addAll(int... iArr) {
            for (int i10 : iArr) {
                add(i10);
            }
            return this;
        }

        public Builder addIf(int i10, boolean z2) {
            return z2 ? add(i10) : this;
        }

        public FlagSet build() {
            Assertions.checkState(!this.buildCalled);
            this.buildCalled = true;
            return new FlagSet(this.flags);
        }

        public Builder remove(int i10) {
            Assertions.checkState(!this.buildCalled);
            this.flags.delete(i10);
            return this;
        }

        public Builder removeAll(int... iArr) {
            for (int i10 : iArr) {
                remove(i10);
            }
            return this;
        }

        public Builder removeIf(int i10, boolean z2) {
            return z2 ? remove(i10) : this;
        }

        public Builder addAll(FlagSet flagSet) {
            for (int i10 = 0; i10 < flagSet.size(); i10++) {
                add(flagSet.get(i10));
            }
            return this;
        }
    }

    private FlagSet(SparseBooleanArray sparseBooleanArray) {
        this.flags = sparseBooleanArray;
    }
}
