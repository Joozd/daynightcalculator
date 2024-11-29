package nl.joozd.navigation

import nl.joozd.Location
import nl.joozd.util.toDegrees
import nl.joozd.util.toRadians
import kotlin.math.*

/**
 * Calculate a great circle route from start to end
 */
internal class GreatCircleRoute(
    start: Location,
    end: Location,
) {
    // Convert latitudes and longitudes from degrees to radians
    private val lat1 = start.latitude.toRadians()
    private val lon1 = start.longitude.toRadians()
    private val lat2 = end.latitude.toRadians()
    private val lon2 = end.longitude.toRadians()

    // calculates cosines and sines so we only have to do that once.
    private val cosLat1 = cos(lat1)
    private val cosLat2 = cos(lat2)
    private val sinLat1 = sin(lat1)
    private val sinLat2 = sin(lat2)

    /**
     * The initial track to steer from start (as a true track)
     */
    val initialTrack = atan2(sin(lon2 - lon1) * cosLat2, cosLat1 * sinLat2 - sinLat1 * cosLat2 * cos(lon2 - lon1))

    /**
     * The distance from start to end along the great circle (in radians)
     */
    val distance = 2 * asin(sqrt(haversine(lat1, lat2) + cosLat1 * cosLat2 * haversine(lon1, lon2)))

    /**
     * Distance in NM
     */
    val distanceNM = distance.toDegrees() * 60

    /**
     * Splits the route into [numberOfPoints] points and gives the location per point
     */
    /**
     * Get a List of [amountOfPoints] points along a route from [origin] to [destination]
     */
    fun getPointsAlongRoute(amountOfPoints: Int) = sequence{
        // Calculate the initial bearing

        // calculate and add each intermediate point
        repeat(amountOfPoints) { i ->
            val fraction = i.toDouble() / amountOfPoints // fraction of the total distance from start to end
            val aDist = fraction * distance // angular distance, in radians
            val lat = asin(sinLat1 * cos(aDist) + cosLat1 * sin(aDist) * cos(initialTrack))
            val lon = lon1 + atan2(sin(initialTrack) * sin(aDist) * cosLat1, cos(aDist) - sinLat1 * sin(lat))

            // Add the intermediate point to the waypoints list
            yield(Location(lat.toDegrees(), lon.toDegrees()))
        }
    }

    /**
     * Calculate the haversine of the latitude difference
     */
    private fun haversine(pos1: Double, pos2: Double): Double =
        sin((pos2 - pos1) / 2).pow(2.0)
}