package com.nuvio.tv.youtube;

/* JADX INFO: loaded from: classes4.dex */
public final class YouTubePlayerViewModel_HiltModules {

    public static abstract class BindsModule {
        private BindsModule() {
        }

        public abstract androidx.lifecycle.z0 binds(YouTubePlayerViewModel youTubePlayerViewModel);
    }

    public static final class KeyModule {
        private KeyModule() {
        }

        public static boolean provide() {
            return true;
        }
    }

    private YouTubePlayerViewModel_HiltModules() {
    }
}
