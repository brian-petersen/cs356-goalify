import java.text.SimpleDateFormat
import java.util.*

fun formatTime(hourOfDay: Int, minute: Int): String {
    val cal = Calendar.getInstance()

    cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
    cal.set(Calendar.MINUTE, minute)

    return SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(cal.time)
}
