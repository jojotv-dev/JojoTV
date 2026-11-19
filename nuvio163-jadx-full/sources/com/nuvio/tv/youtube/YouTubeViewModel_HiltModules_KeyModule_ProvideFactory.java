package com.nuvio.tv.youtube;

/* JADX INFO: loaded from: classes4.dex */
public final class YouTubeViewModel_HiltModules_KeyModule_ProvideFactory implements tg.c {

    public static final class InstanceHolder {
        static final YouTubeViewModel_HiltModules_KeyModule_ProvideFactory INSTANCE = new YouTubeViewModel_HiltModules_KeyModule_ProvideFactory();

        private InstanceHolder() {
        }
    }

    public static YouTubeViewModel_HiltModules_KeyModule_ProvideFactory create() {
        return InstanceHolder.INSTANCE;
    }

    public static boolean provide() {
        return YouTubeViewModel_HiltModules.KeyModule.provide();
    }

    @Override // tg.c
    public Boolean get() {
        return Boolean.valueOf(provide());
    }
}
