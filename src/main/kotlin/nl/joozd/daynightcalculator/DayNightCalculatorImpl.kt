package nl.joozd

import nl.joozd.navigation.GreatCircleRoute
import org.shredzone.commons.suncalc.SunPosition
import java.time.Duration
import java.time.Instant

internal class DayNightCalculatorImpl: DayNightCalculator {
    /**
     * @return true if it is day (including civil twilight), false if it is night.
     */
    override fun itIsDayAt(time: Instant, latitude: Double, longitude: Double, allowableTwilight: Double): Boolean =
        itIsDayAt(time, Location(latitude, longitude), allowableTwilight)

    private fun itIsDayAt(time: Instant, location: Location, allowableTwilight: Double): Boolean {
        // get position of sun at position
        val sunPosition = SunPosition.compute()
            .at(location.latitude, location.longitude)
            .on(time)
            .execute()
        // check if sun is more than [allowableTwilight] degrees above the horizon.
        // Twilight values should be negative, so (for example) -6.0 will give civil twilight;
        // sun less than 6 degrees below the horizon.
        return sunPosition.altitude >= allowableTwilight
    }

    /**
     * Gives the number of minutes this trip was done during the night
     */
    override fun minutesOfNightOnTrip(
        startLat: Double,
        startLon: Double,
        endLat: Double,
        endLong: Double,
        departureTime: Instant,
        arrivalTime: Instant,
        allowableTwilight: Double
    ): Int {
        val positionsOnRoute = routeToPositionsPerMinute(departureTime, arrivalTime, startLat, startLon, endLat, endLong)
        var positionsVisited = 0L
        return positionsOnRoute.count { pos ->
            !itIsDayAt(
                time = departureTime + Duration.ofMinutes(positionsVisited++),
                location = pos,
                allowableTwilight = Twilight.CIVIL_TWILIGHT)
        }
    }

    /**
     * List of times at which a sunrise is expected to be. Usually 0 or 1, but can be higher in theory.
     */
    override fun sunrisesSunsets(startLat: Double, startLon: Double, endLat: Double, endLong: Double, departureTime: Instant, arrivalTime: Instant): SunrisesSunsets {
        val sunrises = ArrayList<Instant>()
        val sunsets = ArrayList<Instant>()
        val positions = routeToPositionsPerMinute(departureTime, arrivalTime, startLat, startLon, endLat, endLong)
        var itIsDay = itIsDayAt(departureTime, positions.first(), allowableTwilight = Twilight.NO_TWILIGHT)

        var positionsVisited = 0L

        // Iterate over all positions and add positions where sun changes from below to above horizon or VV to the respective list
        positions.forEach { position ->

            val time = departureTime.plusSeconds(60 * positionsVisited  )
            val isDayAtPosition = itIsDayAt(
                time,
                position,
                allowableTwilight = Twilight.NO_TWILIGHT)
            if (isDayAtPosition == itIsDay) return@forEach // no sunrise/sunset event, try next position
            if(isDayAtPosition)
                sunrises.add(time)
            else
                sunsets.add(time)
            itIsDay = isDayAtPosition

            positionsVisited++
        }
        return SunrisesSunsets(sunrises, sunsets)
    }

    private fun routeToPositionsPerMinute(
        departureTime: Instant,
        arrivalTime: Instant,
        startLat: Double,
        startLon: Double,
        endLat: Double,
        endLong: Double
    ): Sequence<Location> {
        val duration = Duration.between(departureTime, arrivalTime).toMinutes().toInt()
        val startLocation = Location(startLat, startLon)
        val endLocation = Location(endLat, endLong)
        val route = GreatCircleRoute(startLocation, endLocation)

        return route.getPointsAlongRoute(duration)
    }
}