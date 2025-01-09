package at.htlwels.ires.common

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object Constants {

    /** Shared preferences file name */
    const val SHARED_PREF_FILE = "tokens"



    object DateUtils{

        // Define the ISO 8601 formatter for reading from Responses
        val isoFormatter = DateTimeFormatter.ISO_DATE_TIME


        fun convertToDateString(
            millis: Long,
            format: String = "yyyy-MM-dd'T'HH:mm"
        ) : String {
            println(millis)

            val formatter = DateTimeFormatter.ofPattern(format).withZone(ZoneId.systemDefault())
            return formatter.format(Instant.ofEpochMilli(millis))
        }

        fun convertToDateString(
            dateTime: LocalDateTime,
            formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm").withZone(ZoneId.systemDefault())
        ) : String {
            return dateTime.format(formatter)
        }
    }
}