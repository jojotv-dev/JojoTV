package androidx.media3.extractor;

import androidx.media3.common.util.UnstableApi;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import r7.a1;
import r7.c1;
import r7.i2;

/* JADX INFO: loaded from: classes.dex */
@UnstableApi
public interface Extractor {
    public static final int RESULT_CONTINUE = 0;
    public static final int RESULT_END_OF_INPUT = -1;
    public static final int RESULT_SEEK = 1;

    /* JADX INFO: loaded from: classes3.dex */
    @Target({ElementType.TYPE_USE})
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface ReadResult {
    }

    default List<SniffFailure> getSniffFailureDetails() {
        a1 a1Var = c1.f51216c;
        return i2.f51280m;
    }

    default Extractor getUnderlyingImplementation() {
        return this;
    }

    void init(ExtractorOutput extractorOutput);

    int read(ExtractorInput extractorInput, PositionHolder positionHolder);

    void release();

    void seek(long j10, long j11);

    boolean sniff(ExtractorInput extractorInput);
}
