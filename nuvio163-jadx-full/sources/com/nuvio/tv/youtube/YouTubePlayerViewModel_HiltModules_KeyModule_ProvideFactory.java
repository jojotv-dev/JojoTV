package com.nuvio.tv.youtube;

/* JADX INFO: loaded from: classes4.dex */
public final class YouTubePlayerViewModel_HiltModules_KeyModule_ProvideFactory implements tg.c {

    public static final class InstanceHolder {
        static final YouTubePlayerViewModel_HiltModules_KeyModule_ProvideFactory INSTANCE = new YouTubePlayerViewModel_HiltModules_KeyModule_ProvideFactory();

        private InstanceHolder() {
        }
    }

    public static YouTubePlayerViewModel_HiltModules_KeyModule_ProvideFactory create() {
        return InstanceHolder.INSTANCE;
    }

    public static boolean provide() {
        return YouTubePlayerViewModel_HiltModules.KeyModule.provide();
    }

    @Override // tg.c
    public Boolean get() {
        return Boolean.valueOf(provide());
    }
}
