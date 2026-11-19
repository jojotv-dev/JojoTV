package androidx.media3.extractor.jpeg;

import androidx.media3.extractor.metadata.mp4.MotionPhotoMetadata;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
final class MotionPhotoDescription {
    public final List<ContainerItem> items;
    public final long photoPresentationTimestampUs;

    public static final class ContainerItem {
        public final long length;
        public final String mime;
        public final long padding;
        public final String semantic;

        public ContainerItem(String str, String str2, long j10, long j11) {
            this.mime = str;
            this.semantic = str2;
            this.length = j10;
            this.padding = j11;
        }
    }

    public MotionPhotoDescription(long j10, List<ContainerItem> list) {
        this.photoPresentationTimestampUs = j10;
        this.items = list;
    }

    public MotionPhotoMetadata getMotionPhotoMetadata(long j10) {
        long j11;
        if (this.items.size() < 2) {
            return null;
        }
        long j12 = j10;
        long j13 = -1;
        long j14 = -1;
        long j15 = -1;
        long j16 = -1;
        boolean z2 = false;
        for (int size = this.items.size() - 1; size >= 0; size--) {
            ContainerItem containerItem = this.items.get(size);
            boolean zEquals = "video/mp4".equals(containerItem.mime) | z2;
            if (size == 0) {
                j12 -= containerItem.padding;
                j11 = 0;
            } else {
                j11 = j12 - containerItem.length;
            }
            long j17 = j11;
            long j18 = j12;
            j12 = j17;
            if (!zEquals || j12 == j18) {
                z2 = zEquals;
            } else {
                j16 = j18 - j12;
                j15 = j12;
                z2 = false;
            }
            if (size == 0) {
                j13 = j12;
                j14 = j18;
            }
        }
        if (j15 == -1 || j16 == -1 || j13 == -1 || j14 == -1) {
            return null;
        }
        return new MotionPhotoMetadata(j13, j14, this.photoPresentationTimestampUs, j15, j16);
    }
}
