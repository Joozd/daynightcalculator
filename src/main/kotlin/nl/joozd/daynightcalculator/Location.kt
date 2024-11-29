package nl.joozd

data class Location(val latitude: Double, val longitude: Double){
    override fun toString() = "(${String.format("%.3f", latitude)}, ${String.format("%.3f", longitude)})"
}