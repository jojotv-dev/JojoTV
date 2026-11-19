package com.liskovsoft.sharedutils.rx;

import eh.k;

/* JADX INFO: loaded from: classes4.dex */
public interface SchedulerProvider {
    k computation();

    k io();

    k ui();
}
