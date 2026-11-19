package androidx.media3.exoplayer.video;

import androidx.media3.common.util.UnstableApi;
import androidx.media3.container.ObuParser;
import java.nio.ByteBuffer;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public final class Av1SampleDependencyParser {
    private static final int MAX_OBU_COUNT_FOR_PARTIAL_SKIP = 8;
    private ObuParser.SequenceHeader sequenceHeader;

    private boolean canSkipObu(ObuParser.Obu obu, boolean z2) {
        ObuParser.SequenceHeader sequenceHeader;
        ObuParser.FrameHeader frameHeader;
        int i10 = obu.type;
        if (i10 == 2 || i10 == 15) {
            return true;
        }
        if (i10 != 3 || z2) {
            return ((i10 != 6 && i10 != 3) || (sequenceHeader = this.sequenceHeader) == null || (frameHeader = ObuParser.FrameHeader.parse(sequenceHeader, obu)) == null || frameHeader.isDependedOn()) ? false : true;
        }
        return false;
    }

    private void updateSequenceHeaders(List<ObuParser.Obu> list) {
        for (int i10 = 0; i10 < list.size(); i10++) {
            if (list.get(i10).type == 1) {
                this.sequenceHeader = ObuParser.SequenceHeader.parse(list.get(i10));
            }
        }
    }

    public void queueInputBuffer(ByteBuffer byteBuffer) {
        updateSequenceHeaders(ObuParser.split(byteBuffer));
    }

    public void reset() {
        this.sequenceHeader = null;
    }

    public int sampleLimitAfterSkippingNonReferenceFrame(ByteBuffer byteBuffer, boolean z2) {
        List<ObuParser.Obu> listSplit = ObuParser.split(byteBuffer);
        updateSequenceHeaders(listSplit);
        int size = listSplit.size() - 1;
        int i10 = 0;
        while (size >= 0 && canSkipObu(listSplit.get(size), z2)) {
            if (listSplit.get(size).type == 6 || listSplit.get(size).type == 3) {
                i10++;
            }
            size--;
        }
        if (i10 > 1 || size + 1 >= 8) {
            return byteBuffer.limit();
        }
        return size >= 0 ? listSplit.get(size).payload.limit() : byteBuffer.position();
    }
}
