package nl.joozd.daynightcalculator

import java.time.Instant

interface DayNightCalculator {
    /**
     * @return true if it is day (including civil twilight), false if it is night.
     */
    fun itIsDayAt(time: Instant, location: Location, allowableTwilight: Double = Twilight.CIVIL_TWILIGHT): Boolean

    /**
     * Gives the number of minutes this trip was done during the night
     */
    fun minutesOfNightOnTrip(
        start: Location,
        end: Location,
        departureTime: Instant,
        arrivalTime: Instant,
        allowableTwilight: Double = Twilight.CIVIL_TWILIGHT
    ): Int

    /**
     * Lists of times at which a sunrise or sunset is expected to be. Usually 0 or 1, but can be higher in theory.
     */
    fun sunrisesSunsets(
        start: Location,
        end: Location,
        departureTime: Instant,
        arrivalTime: Instant
    ): SunrisesSunsets



    companion object{
        // This allows you to construct a DayNightCalculator just like a class (DayNightCalculator())
        operator fun invoke(): DayNightCalculator = DayNightCalculatorImpl()
    }
}