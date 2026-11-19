package com.nuvio.tv.youtube;

/* JADX INFO: loaded from: classes4.dex */
public final class YouTubeAccountViewModel_HiltModules_KeyModule_ProvideFactory implements tg.c {

    public static final class InstanceHolder {
        static final YouTubeAccountViewModel_HiltModules_KeyModule_ProvideFactory INSTANCE = new YouTubeAccountViewModel_HiltModules_KeyModule_ProvideFactory();

        private InstanceHolder() {
        }
    }

    public static YouTubeAccountViewModel_HiltModules_KeyModule_ProvideFactory create() {
        return InstanceHolder.INSTANCE;
    }

    public static boolean provide() {
        return YouTubeAccountViewModel_HiltModules.KeyModule.provide();
    }

    @Override // tg.c
    public Boolean get() {
        return Boolean.valueOf(provide());
    }
}
