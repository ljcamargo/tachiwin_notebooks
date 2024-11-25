package org.nehuatl.tachiwin.agnostic

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

class AgnosticAdView(
    private val context: Context,
    private val adSize: AgnosticAdSize,
    private val adUnit: String,
) {

    private val builder = AdRequest.Builder().build()

    fun getAd() = AdView(context).also {
        it.setAdSize(adSize.toGoogleAdSize())
        it.adUnitId = adUnit
        it.loadAd(builder)
    }

    private fun AgnosticAdSize.toGoogleAdSize(): AdSize = when (this) {
        AgnosticAdSize.BANNER -> AdSize.BANNER
        AgnosticAdSize.FULL_BANNER -> AdSize.FULL_BANNER
        AgnosticAdSize.LARGE_BANNER -> AdSize.LARGE_BANNER
        AgnosticAdSize.LEADERBOARD -> AdSize.LEADERBOARD
        AgnosticAdSize.MEDIUM_RECTANGLE -> AdSize.MEDIUM_RECTANGLE
        AgnosticAdSize.WIDE_SKYSCRAPER -> AdSize.WIDE_SKYSCRAPER
        AgnosticAdSize.FLUID -> AdSize.FLUID
        AgnosticAdSize.INVALID -> AdSize.INVALID
        AgnosticAdSize.SEARCH -> AdSize.SEARCH
    }

    enum class AgnosticAdSize {
        BANNER,
        FULL_BANNER,
        LARGE_BANNER,
        LEADERBOARD,
        MEDIUM_RECTANGLE,
        WIDE_SKYSCRAPER,
        FLUID,
        INVALID,
        SEARCH;
    }

}