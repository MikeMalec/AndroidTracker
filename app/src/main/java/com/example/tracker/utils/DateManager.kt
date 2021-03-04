package com.example.tracker.utils

import java.text.SimpleDateFormat
import java.util.*


class DateManager {
    companion object {
        private val dateWithDayNameHoursPattern = "E dd MMMM h:mm a"
        private val dateWithDayNameHoursYearPattern = "E dd MMMM yyyy h:mm a"

        private fun getCalendar() = Calendar.getInstance()

        fun getDateWithDayNameAndHours(date: Long): String {
            val calendar = getCalendar()
            calendar.timeInMillis = date
            return when (getCalendar().get(Calendar.YEAR)) {
                calendar.get(Calendar.YEAR) -> SimpleDateFormat(dateWithDayNameHoursPattern).format(
                    calendar.timeInMillis
                )
                else -> SimpleDateFormat(dateWithDayNameHoursYearPattern).format(calendar.timeInMillis)
            }
        }
    }
}