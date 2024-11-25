package org.nehuatl.tachiwin.core

import android.content.Context
import org.nehuatl.tachiwin.agnostic.AgnosticLogger

class Logger(context: Context): AgnosticLogger(context) {

    fun search() = log(Events.SEARCH)
    fun switch() = log(Events.SWITCH)
    fun details() = log(Events.DETAILS)
    fun jump() = log(Events.JUMP)
    fun about() = log(Events.ABOUT)

    enum class Events {
        SEARCH,
        SWITCH,
        DETAILS,
        JUMP,
        ABOUT,
    }

}