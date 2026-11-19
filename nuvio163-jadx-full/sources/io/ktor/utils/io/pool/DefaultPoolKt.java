package io.ktor.utils.io.pool;

import androidx.media3.container.NalUnitUtil;
import kotlin.Metadata;

/* JADX INFO: loaded from: classes5.dex */
@Metadata(d1 = {"\u0000\b\n\u0002\u0010\b\n\u0002\b\u0006\"\u0014\u0010\u0001\u001a\u00020\u00008\u0002X\u0082T¢\u0006\u0006\n\u0004\b\u0001\u0010\u0002\"\u0014\u0010\u0003\u001a\u00020\u00008\u0002X\u0082T¢\u0006\u0006\n\u0004\b\u0003\u0010\u0002\"\u0014\u0010\u0004\u001a\u00020\u00008\u0002X\u0082T¢\u0006\u0006\n\u0004\b\u0004\u0010\u0002\"\u0014\u0010\u0005\u001a\u00020\u00008\u0002X\u0082T¢\u0006\u0006\n\u0004\b\u0005\u0010\u0002¨\u0006\u0006"}, d2 = {"", "MULTIPLIER", "I", "PROBE_COUNT", "MAGIC", "MAX_CAPACITY", "ktor-io"}, k = 2, mv = {2, 1, 0}, xi = NalUnitUtil.H265_NAL_UNIT_TYPE_UNSPECIFIED)
public final class DefaultPoolKt {
    private static final int MAGIC = -1640531527;
    private static final int MAX_CAPACITY = 536870911;
    private static final int MULTIPLIER = 4;
    private static final int PROBE_COUNT = 8;
}
