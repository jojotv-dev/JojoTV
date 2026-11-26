package com.nuvio.tv.ui.screens.home

object HeroBackdropState {
    @Volatile
    var currentHeroBackdropUrl: String? = null
        private set

    /** Last backdrop URL that was actually displayed — survives navigation for seamless back transitions. */
    @Volatile
    var lastDisplayedUrl: String? = null

    @Volatile
    var lastDisplayedIdentityKey: String? = null
        private set

    fun update(url: String?, identityKey: String? = null) {
        currentHeroBackdropUrl = url
        if (!url.isNullOrBlank()) {
            lastDisplayedUrl = url
            lastDisplayedIdentityKey = identityKey
        }
    }

    fun consumeAndClear(): String? {
        val url = currentHeroBackdropUrl
        currentHeroBackdropUrl = null
        return url
    }
}
