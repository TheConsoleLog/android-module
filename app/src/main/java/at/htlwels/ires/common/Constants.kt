package at.htlwels.ires.common

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

object Constants {

    /** Shared preferences file name */
    const val SHARED_PREF_FILE = "tokens"


    const val STRIPE_API_KEY = "pk_test_51Qx6qK4fJFLnAbUNgEHCXcsFCZfVUbw9IDSi4F8iYGrz7ZfpfTXYEl8NiXbl7chYrPkbWqDZtNeKCoJkBq9Y51db00qPLHFtNL"

    object DateUtils{

        // Define the ISO 8601 formatter for reading from Responses
        val isoFormatter = DateTimeFormatter.ISO_DATE_TIME
        val isoFormat = "yyyy-MM-dd'T'HH:mm:ss"


        fun convertToDateString(
            millis: Long,
            format: String = isoFormat
        ) : String {
            println(millis)

            val formatter = DateTimeFormatter.ofPattern(format).withZone(ZoneId.systemDefault())
            return formatter.format(Instant.ofEpochMilli(millis))
        }

        fun convertToDateString(
            dateTime: LocalDateTime,
            formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm").withZone(ZoneId.systemDefault())
        ) : String = dateTime.format(formatter)


        fun convertToDateTime(
            hour: Int,
            minute: Int,
            dateInMillis: Long
        ): String {

            val calendar = Calendar.getInstance().apply { timeInMillis = dateInMillis }

            // Update the Calendar instance with the time from TimePickerState
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0) // Set seconds to zero

            println(SimpleDateFormat(isoFormat, Locale.getDefault()).format(calendar.time))

            return SimpleDateFormat(isoFormat, Locale.getDefault()).format(calendar.time)
        }
    }
}