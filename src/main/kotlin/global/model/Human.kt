package global.model

import com.example.covidsimulator.global.model.State

class Human: Ball {
    var state: State =
        State.HEALTHY
    var infectedTime = 0.0
    var infectedPeople = 0
    constructor(x: Double, y: Double, dx: Double, dy: Double,
                fx: Double,
                fy: Double, radius: Double, d: Double) : super(x, y, dx, dy,fx,fy, radius, d)

    fun getColor():String{
        if(state == State.INFECTED) {
            return "#FF0000"
        } else if (state == State.HEALTHY) {
            return "#0000FF"
        } else {
            return "#32CD32"
        }
    }
}