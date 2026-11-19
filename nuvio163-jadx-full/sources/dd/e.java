package dd;

import java.util.Collection;
import kotlin.coroutines.Continuation;

/* JADX INFO: loaded from: classes.dex */
public interface e {
    Object deleteItems(int i10, Collection collection, Continuation continuation);

    Object getDeltaCursor(int i10, Continuation continuation);

    Object pullDelta(int i10, long j10, int i11, Continuation continuation);

    Object pullSnapshot(int i10, int i11, Continuation continuation);

    Object pushItems(int i10, Collection collection, Continuation continuation);
}
