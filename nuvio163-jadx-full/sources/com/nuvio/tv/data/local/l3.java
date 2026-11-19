package com.nuvio.tv.data.local;

import com.nuvio.tv.domain.model.CardDepthSurface;

/* JADX INFO: loaded from: classes4.dex */
public abstract /* synthetic */ class l3 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final /* synthetic */ int[] f8902a;

    static {
        int[] iArr = new int[CardDepthSurface.values().length];
        try {
            iArr[CardDepthSurface.POSTERS.ordinal()] = 1;
        } catch (NoSuchFieldError unused) {
        }
        try {
            iArr[CardDepthSurface.CONTINUE_WATCHING.ordinal()] = 2;
        } catch (NoSuchFieldError unused2) {
        }
        try {
            iArr[CardDepthSurface.EPISODE_CARDS.ordinal()] = 3;
        } catch (NoSuchFieldError unused3) {
        }
        try {
            iArr[CardDepthSurface.CAST.ordinal()] = 4;
        } catch (NoSuchFieldError unused4) {
        }
        try {
            iArr[CardDepthSurface.TRAILERS.ordinal()] = 5;
        } catch (NoSuchFieldError unused5) {
        }
        f8902a = iArr;
    }
}
