package com.eit.enhancedconsultant.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


@Suppress("UNUSED")
object Utils
{
    private const val regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"

    var rowPositions: ArrayList<Int> = ArrayList()

    enum class TimeOfDay{
        MORNING,
        AFTERNOON,
        EVENING,
        NIGHT
    }

    const val hiddenColour: String = "#FFFFFF"
    const val visibleColour: String = "#808080"

    fun formatTelephoneNumber(phoneNumber: String, local: Boolean): String
    {
        if(phoneNumber.length < 10)
        {
            // Incorrect number length so return it
            // to th user
            return phoneNumber
        }// end of if block
        else
        {
            val phoneTransformed: StringBuilder = StringBuilder()

            if(!local)
            {
                phoneTransformed.append("+1 ")
            } // end of if block

            for(i in phoneNumber.indices)
            {
                when (i)
                {
                    0->{
                        phoneTransformed.append("(").append(phoneNumber[i])
                    }
                     
                    2->{
                        phoneTransformed.append(phoneNumber[i]).append(")")
                    }
                        
                    3->{
                        phoneTransformed.append(" ").append(phoneNumber[i])
                    }
                        
                    6->{
                        phoneTransformed.append("-").append(phoneNumber[i])
                    }
                    else->{
                        phoneTransformed.append(phoneNumber[i]) 
                    }
                }// end of when block
            }// end of for loop

            return phoneTransformed.toString()
        }// end of else block
    }// end of method formatTelephoneNumber

    fun validEmailAddress(address: String): Boolean
    {
        //initialize the Pattern object
        val pattern: Pattern = Pattern.compile(regex)
        val matcher: Matcher = pattern.matcher(address)

        return matcher.matches()
    }// end of method validEmailAddress

    /**
     * Get the current time of date
     *
     * @return An enum value of MORNING, NOON, EVENING, and NIGHT
     */
    fun getTimeOfDay(): TimeOfDay
    {
        val now = Calendar.getInstance()
        val checkCalendar = Calendar.getInstance()
        val checkOtherCalendar = Calendar.getInstance()
        val timeZone = SimpleDateFormat("zzz",
            Locale.ENGLISH).format(now.time)
        val isDaylightSavings = timeZone.substring(1) == "DT"

        if (isDaylightSavings)
        {
            checkCalendar.set(Calendar.HOUR_OF_DAY, 17)
            checkCalendar.set(Calendar.MINUTE, 59)
            checkCalendar.set(Calendar.SECOND, 59)

            checkOtherCalendar.set(Calendar.HOUR_OF_DAY, 20)
            checkOtherCalendar.set(Calendar.MINUTE, 59)
            checkOtherCalendar.set(Calendar.SECOND, 59)

            if (now.time.after(checkCalendar.time) &&
                now.time.before(checkOtherCalendar.time))
            {
                return TimeOfDay.EVENING
            } // end of if block

            checkCalendar.set(Calendar.HOUR_OF_DAY, 20)
            checkCalendar.set(Calendar.MINUTE, 59)
            checkCalendar.set(Calendar.SECOND, 59)

            if (now.time.after(checkCalendar.time))
            {
                // anytime after 9:00 should be considered a night time
                return TimeOfDay.NIGHT
            } // end of else if block

            checkCalendar.set(Calendar.HOUR_OF_DAY, 0)
            checkCalendar.set(Calendar.MINUTE, 0)
            checkCalendar.set(Calendar.SECOND, 0)

            checkOtherCalendar.set(Calendar.HOUR_OF_DAY, 11)
            checkOtherCalendar.set(Calendar.MINUTE, 59)
            checkOtherCalendar.set(Calendar.SECOND, 59)

            if (now.time == checkCalendar.time ||
                now.time.after(checkCalendar.time) &&
                now.time.before(checkOtherCalendar.time))
            {
                return TimeOfDay.MORNING
            } // end of else if block
            else
            {
                // if the time is'nt after 6:00 PM we will assume that it is still afternoon
                TimeOfDay.AFTERNOON
            } // end of else block
        } // end of if block

        // Standard time
        checkCalendar.set(Calendar.HOUR_OF_DAY, 16)
        checkCalendar.set(Calendar.MINUTE, 59)
        checkCalendar.set(Calendar.SECOND, 59)

        checkOtherCalendar.set(Calendar.HOUR_OF_DAY, 20)
        checkOtherCalendar.set(Calendar.MINUTE, 59)
        checkOtherCalendar.set(Calendar.SECOND, 59)

        if (now.time.after(checkCalendar.time) &&
            now.time.before(checkOtherCalendar.time))
        {
            return TimeOfDay.EVENING
        } // end of if block

        checkCalendar.set(Calendar.HOUR_OF_DAY, 20)
        checkCalendar.set(Calendar.MINUTE, 59)
        checkCalendar.set(Calendar.SECOND, 59)

        checkOtherCalendar.set(Calendar.HOUR_OF_DAY, 20)
        checkOtherCalendar.set(Calendar.MINUTE, 59)
        checkOtherCalendar.set(Calendar.SECOND, 59)

        if (now.time.after(checkCalendar.time))
        {
            // anytime after 9:00 should be considered a night time
            return TimeOfDay.NIGHT
        } // end of if block

        checkCalendar.set(Calendar.HOUR_OF_DAY, 0)
        checkCalendar.set(Calendar.MINUTE, 0)
        checkCalendar.set(Calendar.SECOND, 0)

        checkOtherCalendar.set(Calendar.HOUR_OF_DAY, 11)
        checkOtherCalendar.set(Calendar.MINUTE, 59)
        checkOtherCalendar.set(Calendar.SECOND, 59)

        if (now.time == checkCalendar.time ||
            now.time.after(checkCalendar.time) &&
            now.time.before(checkOtherCalendar.time))
        {
            return TimeOfDay.MORNING
        } // end of else if block
        else
        {
            // if the time is'nt after 5:00 PM we will assume that it is still afternoon
            return TimeOfDay.AFTERNOON
        } // end of else block
    } // end of method getTimeOfDay

    /**
     * Converts a sequence to alphabetic characters to sentence case.
     *
     * @param text The `String` value to be converted.
     * @return        A `String` representation of text in a sentence format.
     */
    fun toProperCase(text: String): String
    {
        val sep = arrayOf(" ", "-", "/", "'")
        val cycle = text.length
        val sequence = StringBuilder(text.toLowerCase(Locale.ROOT))

        for (i in 0..cycle)
        {
            if (i == 0 && Character.isAlphabetic(sequence[i].toInt()))
            {
                sequence.replace(
                    sequence.indexOf(sequence[i].toString()),
                    sequence.indexOf(sequence[i].toString()) + 1,
                    Character.toUpperCase(sequence[i]).toString()
                )
            } // end of if block
            else if (i < cycle && Character.isAlphabetic(sequence[i].toInt()) &&
                sequence[i - 1] == 'c' && sequence[i - 2] == 'M')
            {
                sequence.replace(
                    sequence.indexOf(sequence[i].toString()),
                    sequence.indexOf(sequence[i].toString()) + 1,
                    sequence[i].toString().toUpperCase(Locale.ROOT)
                )
            } // end of else if block
            else if (i < cycle && Character.isAlphabetic(sequence[i].toInt()) &&
                (sequence[i - 1].toString() == sep[0] || sequence[i - 1].toString() == sep[1]))
            {
                sequence.replace(
                    i, i + 1, sequence[i].toString().toUpperCase(Locale.ROOT)
                )
            } // end of else if block
        } // end of for loop

        return sequence.toString()
    } // end of function toProperCase
}// end of class Utils
