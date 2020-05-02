package global.physic

import global.model.Ball
import global.model.Human
import com.example.covidsimulator.global.model.State
import kotlin.math.pow
import kotlin.random.Random

class InfectionWorld(
    var dt: Double
) {
    var totalTime = 0.0
    var imunityLength: Double = 730.0
    var infectionProbilityPerCollision: Double = 0.15
    var infected: Int = 0
    var immune: Int = 0
    var imporInfectedAfterDays = 0
    var staticPopulationPercent = 0
    var lastReinfectionTime = 0

    private val elasticity = 1
    private val INFECTION_LENGTH_DAYS = 14.0
    private val MAX_R = 6
    private var collisionCounter = 0

    // transformation between physical and graphical screen coordinates
    private var worldWidth: Int = 0
    private var worldHeight: Int = 0

    var humans: ArrayList<Human> = ArrayList()


    fun update() {
        updateBalls()
        updateCollision()
        updateInfection()
    }

    fun setWorldSize(width: Int, height: Int) {
        worldHeight = height
        worldWidth = width
    }

    private fun updateBalls() {
        val staticCount = (humans.size * staticPopulationPercent) / 100
        for (i in 0 until humans.size) {
            val b = humans[i]
            if (i >= staticCount) {
                b.acc()
                b.move()
                // top
                if (b.y - b.radius <= 0) {
                    b.y = b.radius
                    b.dy = -b.dy * elasticity
                }
                // bottom
                if (b.y + b.radius >= worldHeight) {
                    b.y = worldHeight - b.radius
                    b.dy = -b.dy * elasticity
                }
                // left
                if (b.x - b.radius <= 0) {
                    b.x = b.radius
                    b.dx = -b.dx * elasticity
                }
                // right
                if (b.x + b.radius >= worldWidth) {
                    b.x = worldWidth - b.radius
                    b.dx = -b.dx * elasticity
                }
                b.m = 1.0
            } else {
                b.dx = 0.0
                b.dy = 0.0
                b.fx = 0.0
                b.fy = 0.0
                b.m = 10000000.0
            }
        }
    }

    private fun updateCollision() {
        val length = humans.size
        var a: Human
        var b: Human

        for (i in 0 until length) {
            a = humans[i]

            for (j in i + 1 until length) {
                b = humans[j]

                if (distance(a, b) <= a.radius + b.radius) {
                    collisionCounter++
                    // collision probably happened dt before distance(A, B) == A.radius + B.radius
                    // so we adjust the distance between center of the two humans -dt time
                    adjustPosition(a, b, -1)
                    // calc new velocities
                    calcCollision(a, b)
                    // because we adjusted time backward dt we need to move time forward dt
                    adjustPosition(a, b, 1)
                    handleInfecting(a, b)
                }
            }
        }
    }

    private fun handleInfecting(a: Human, b: Human) {
        if ((a.state == State.INFECTED) || (b.state == State.INFECTED)) {
            if (Random.nextDouble() < infectionProbilityPerCollision) {
                if (a.state == State.INFECTED && b.state == State.HEALTHY && a.infectedPeople < MAX_R) {
                    b.state = State.INFECTED
                    b.infectedTime = totalTime
                    a.infectedPeople++
                } else if (b.state == State.INFECTED && a.state == State.HEALTHY && b.infectedPeople < MAX_R) {
                    a.state = State.INFECTED
                    a.infectedTime = totalTime
                    b.infectedPeople++
                }
            }
        }
    }

    private fun adjustPosition(A: Ball, B: Ball, dir: Int) {
        val disX = B.x - A.x
        val disY = B.y - A.y
        val dis = distance(A, B)

        // the component of velocity in the direction of (dx,dy)
        val vA = A.dx * disX / dis + A.dy * disY / dis
        val vB = B.dx * disX / dis + B.dy * disY / dis

        // the time when the two ball really collide with each other.
        val dt = (A.radius + B.radius - dis) / (vA - vB)

        // move humans dt time
        updatePosition(A, B, dt, dir)
    }

    private fun updatePosition(A: Ball, B: Ball, dt: Double, dir: Int) {
        if (dt >= 0) {
            A.x += A.dx * dt * dir
            B.x += B.dx * dt * dir
            A.y += A.dy * dt * dir
            B.y += B.dy * dt * dir
        }
    }

    private fun calcCollision(A: Ball, B: Ball) {
        val dis = distance(A, B)
        // unit vectors in direction of collision
        val uvX = (B.x - A.x) / dis
        val uvY = (B.y - A.y) / dis

        // projection of the velocities in these axes
        val vAx = A.dx * uvX + A.dy * uvY
        val vAy = -A.dx * uvY + A.dy * uvX
        val vBx = B.dx * uvX + B.dy * uvY
        val vBy = -B.dx * uvY + B.dy * uvX

        // new velocities in these axes after collision (for elastic collision)
        val vA = vAx + (1.0 + elasticity) * (vBx - vAx) / (1.0 + A.m / B.m)
        val vB = vBx + (1.0 + elasticity) * (vAx - vBx) / (1.0 + B.m / A.m)

        // update velocity
        A.dx = vA * uvX - vAy * uvY
        A.dy = vA * uvY + vAy * uvX
        B.dx = vB * uvX - vBy * uvY
        B.dy = vB * uvY + vBy * uvX
    }

    private fun distance(A: Ball, B: Ball): Double {
        return kotlin.math.sqrt((B.x - A.x).pow(2.0) + (B.y - A.y).pow(2.0))
    }


    private fun updateInfection() {
        totalTime += dt
        immune = 0
        infected = 0
        for (i in 0 until humans.size) {
            if (humans[i].state == State.HEALTHY){
                if ((totalTime-lastReinfectionTime>imporInfectedAfterDays) && (imporInfectedAfterDays>0)) {
                    humans[i].state = State.INFECTED
                    humans[i].infectedTime = totalTime
                    lastReinfectionTime = totalTime.toInt()
                }
            }
            if (humans[i].state == State.INFECTED) {
                if (totalTime - humans[i].infectedTime >= INFECTION_LENGTH_DAYS) {
                    humans[i].state = State.IMMUNE
                    humans[i].infectedPeople = 0
                } else {
                    infected++
                }
            }
            if (humans[i].state == State.IMMUNE) {
                if (totalTime - humans[i].infectedTime > imunityLength) {
                    humans[i].infectedTime = 0.0
                    humans[i].state = State.HEALTHY
                } else {
                    immune++
                }
            }
        }
    }
}