package com.nuvio.tv.youtube;

/* JADX INFO: loaded from: classes4.dex */
public final class YouTubeAccountViewModel_HiltModules {

    public static abstract class BindsModule {
        private BindsModule() {
        }

        public abstract androidx.lifecycle.z0 binds(YouTubeAccountViewModel youTubeAccountViewModel);
    }

    public static final class KeyModule {
        private KeyModule() {
        }

        public static boolean provide() {
            return true;
        }
    }

    private YouTubeAccountViewModel_HiltModules() {
    }
}
