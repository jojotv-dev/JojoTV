package androidx.media3.common;

import androidx.media3.common.util.UnstableApi;

/* JADX INFO: loaded from: classes.dex */
@UnstableApi
public final class NuvioEngineConfig {
    public static final int NUVIO_EXTRACTOR_SCRATCH_SIZE = 65536;
    public static final int STOCK_EXTRACTOR_SCRATCH_SIZE = 4096;
    private static volatile NuvioEngineConfig instance = stockMode();
    private final AllocationMode allocationMode;
    private final DataPipelineMode dataPipelineMode;
    private final int extractorScratchSize;

    public enum AllocationMode {
        STOCK_HEAP,
        NATIVE_OFF_HEAP
    }

    public static final class Builder {
        private AllocationMode allocationMode = AllocationMode.STOCK_HEAP;
        private DataPipelineMode dataPipelineMode = DataPipelineMode.STOCK;
        private int extractorScratchSize = 4096;

        public NuvioEngineConfig build() {
            return new NuvioEngineConfig(this);
        }

        public Builder setAllocationMode(AllocationMode allocationMode) {
            this.allocationMode = allocationMode;
            return this;
        }

        public Builder setDataPipelineMode(DataPipelineMode dataPipelineMode) {
            this.dataPipelineMode = dataPipelineMode;
            return this;
        }

        public Builder setExtractorScratchSize(int i10) {
            this.extractorScratchSize = i10;
            return this;
        }
    }

    public enum DataPipelineMode {
        STOCK,
        ZERO_COPY
    }

    public static NuvioEngineConfig get() {
        return instance;
    }

    public static NuvioEngineConfig nuvioMode() {
        return new Builder().setAllocationMode(AllocationMode.NATIVE_OFF_HEAP).setDataPipelineMode(DataPipelineMode.ZERO_COPY).setExtractorScratchSize(65536).build();
    }

    public static void set(NuvioEngineConfig nuvioEngineConfig) {
        instance = nuvioEngineConfig;
    }

    public static NuvioEngineConfig stockMode() {
        return new Builder().setAllocationMode(AllocationMode.STOCK_HEAP).setDataPipelineMode(DataPipelineMode.STOCK).setExtractorScratchSize(4096).build();
    }

    public AllocationMode getAllocationMode() {
        return this.allocationMode;
    }

    public DataPipelineMode getDataPipelineMode() {
        return this.dataPipelineMode;
    }

    public int getExtractorScratchSize() {
        return this.extractorScratchSize;
    }

    public boolean isNativeAllocationEnabled() {
        return this.allocationMode == AllocationMode.NATIVE_OFF_HEAP;
    }

    public boolean isZeroCopyEnabled() {
        return this.dataPipelineMode == DataPipelineMode.ZERO_COPY;
    }

    private NuvioEngineConfig(Builder builder) {
        this.allocationMode = builder.allocationMode;
        this.dataPipelineMode = builder.dataPipelineMode;
        this.extractorScratchSize = builder.extractorScratchSize;
    }
}
