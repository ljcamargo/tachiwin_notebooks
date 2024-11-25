package org.nehuatl.tachiwin.agnostic

import android.app.Activity

interface AdManagerInterface {
    fun initialize()
    fun loadInterstitial(callback: (Boolean) -> Unit)
    fun showInterstitial(activity: Activity, callback: (Boolean) -> Unit)
    fun loadShowInterstitial(activity: Activity, callback: (Boolean) -> Unit)
}