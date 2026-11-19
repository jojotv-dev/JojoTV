package androidx.media3.common.util;

/* JADX INFO: loaded from: classes.dex */
@UnstableApi
public final class Size {
    public static final Size UNKNOWN = new Size(-1, -1);
    public static final Size ZERO = new Size(0, 0);
    private final int height;
    private final int width;

    public Size(int i10, int i11) {
        Assertions.checkArgument((i10 == -1 || i10 >= 0) && (i11 == -1 || i11 >= 0));
        this.width = i10;
        this.height = i11;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof Size) {
            Size size = (Size) obj;
            if (this.width == size.width && this.height == size.height) {
                return true;
            }
        }
        return false;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public int hashCode() {
        int i10 = this.height;
        int i11 = this.width;
        return i10 ^ ((i11 >>> 16) | (i11 << 16));
    }

    public String toString() {
        return this.width + "x" + this.height;
    }
}
