package at.htlwels.ires.common

import java.time.format.DateTimeFormatter

object Constants {

    /** Shared preferences file name */
    const val SHARED_PREF_FILE = "tokens"

    // Define the ISO 8601 formatter for parsing
    val isoFormatter = DateTimeFormatter.ISO_DATE_TIME
}