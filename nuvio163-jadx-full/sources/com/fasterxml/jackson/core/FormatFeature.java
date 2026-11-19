package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.util.JacksonFeature;

/* JADX INFO: loaded from: classes3.dex */
public interface FormatFeature extends JacksonFeature {
    @Override // com.fasterxml.jackson.core.util.JacksonFeature
    boolean enabledByDefault();

    @Override // com.fasterxml.jackson.core.util.JacksonFeature
    boolean enabledIn(int i10);

    @Override // com.fasterxml.jackson.core.util.JacksonFeature
    int getMask();
}
