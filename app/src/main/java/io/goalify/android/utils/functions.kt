import java.text.SimpleDateFormat
import java.util.*

fun formatTime(hourOfDay: Int, minute: Int): String {
    val cal = Calendar.getInstance()

    cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
    cal.set(Calendar.MINUTE, minute)

    return SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(cal.time)
}

fun getSummaryDays(): List<Calendar> {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_MONTH, -2)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)

    val days = mutableListOf<Calendar>()
    days.add(cal.clone() as Calendar)
    for (i in 1..4) {
        cal.add(Calendar.DATE, 1)
        days.add(cal.clone() as Calendar)
    }

    return days
}

fun getSummaryDaysAsLong(): List<Long> = getSummaryDays().map { it.timeInMillis }

fun normalizeDate(value: Date): Long {
    val cal = Calendar.getInstance()

    cal.time = value
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)

    return cal.timeInMillis
}
