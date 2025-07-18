package com.nwq.baseutils

import android.icu.util.Calendar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DataUtils {


    const val DEFAULT_DATE_FORMAT = "yyyy-MM-dd"
    const val DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss"

    /**
     * 将当前日期时间格式化为字符串
     *
     * @param format 日期时间格式
     * @return 格式化的日期时间字符串
     */
    fun getCurrentDateTime(format: String = DEFAULT_DATE_FORMAT): String {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return sdf.format(Date())
    }

    /**
     * 获取今天的23时59分59秒的时间戳
     * @return 时间戳
     */
    fun getEndOfDayTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }


    /**
     * 将日期时间字符串转换为毫秒值
     *
     * @param dateTimeStr 日期时间字符串
     * @param format 日期时间格式
     * @return 毫秒值
     */
    fun dateTimeStrToMillis(dateTimeStr: String, format: String = DEFAULT_DATETIME_FORMAT): Long {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return try {
            sdf.parse(dateTimeStr)?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    private fun parseTimeToMillis(timeStr: String): Long {
        val format = SimpleDateFormat("yyyy/M/dd/HH:mm", Locale.getDefault())
        return format.parse(timeStr)?.time ?: System.currentTimeMillis()
    }

    /**
     * 将毫秒值转换为日期时间字符串
     *
     * @param millis 毫秒值
     * @param format 日期时间格式
     * @return 日期时间字符串
     */
    fun millisToDateTimeStr(millis: Long, format: String = DEFAULT_DATETIME_FORMAT): String {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return sdf.format(Date(millis))
    }

    /**
     * 将日期格式化为字符串
     *
     * @param date 日期对象
     * @param format 日期格式
     * @return 日期字符串
     */
    fun dateToStr(date: Date, format: String = DEFAULT_DATE_FORMAT): String {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return sdf.format(date)
    }

    /**
     * 将日期字符串解析为日期对象
     *
     * @param dateStr 日期字符串
     * @param format 日期格式
     * @return 日期对象
     */
    fun strToDate(dateStr: String, format: String = DEFAULT_DATE_FORMAT): Date? {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return try {
            sdf.parse(dateStr)
        } catch (e: Exception) {
            null
        }
    }
}