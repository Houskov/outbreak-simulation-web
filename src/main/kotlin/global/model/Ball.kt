package global.model

import kotlin.math.pow


open class Ball(
    var x: Double, // position
    var y: Double,
    var dx: Double, // acc
    var dy: Double,
    var fx: Double,
    var fy: Double,
    val radius: Double,
    val d: Double
) {
    var m: Double // mass
    val dt: Double
    val g:Double = 0.0

    init {
        this.dt = 0.016
        this.m = 1.0
    }

    fun move() {
        y += dy * dt
        val tmp = dx + fx / m * dt
        x += (0.5 * dx + 0.5 * tmp) * dt // Runge Kutta
    }

    fun acc() {
        dy += (g + fy / m) * dt
        dx += fx / m * dt + 0.5 * (fx / m) * dt.pow(2.0) // Runge Kutta
    }
}