import global.model.Human
import com.example.covidsimulator.global.model.State
import global.physic.InfectionWorld
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.document
import kotlin.browser.window
import kotlin.random.*
import kotlin.math.*

val canvas = initializeCanvas()
fun initializeCanvas(): HTMLCanvasElement {
    val div = document.getElementById("balls-container")
    val canvas = document.createElement("canvas") as HTMLCanvasElement
    val context = canvas.getContext("2d") as CanvasRenderingContext2D
    context.canvas.width = window.innerWidth/2
    context.canvas.height = window.innerHeight
    div!!.appendChild(canvas)
    return canvas
}

val context: CanvasRenderingContext2D
    get() {
        return canvas.getContext("2d") as CanvasRenderingContext2D
    }


val width: Int
    get() {
        return canvas.width
    }

val height: Int
    get() {
        return canvas.height
    }

fun renderBackground() {
    context.save()
    context.fillStyle = "#5C7EED"
    context.fillRect(0.0, 0.0, width.toDouble(), height.toDouble())
    context.restore()
}

var world: InfectionWorld = InfectionWorld(0.016)
fun main(args: Array<String>) {
    generateData()
    world.setWorldSize(width, height)
    window.setInterval({
        world.update()
        drawData()
        updateTextAndSettings()
    }, (world.dt * 1000).toInt())

}

fun updateTextAndSettings(){
    val infected = document.getElementById("infected-text")
    infected!!.textContent = "Infected people: " + world.infected.toString()
    val immune = document.getElementById("immune-text")
    immune!!.textContent = "Immune people: " + world.immune.toString()
    val total = document.getElementById("total-text")
    total!!.textContent = "Total people: " + world.humans.size.toString()
}

fun drawData(){
    clear()
    repaint()
}

fun repaint() {
    for (i in 0 until world.humans.size) {
        paintCircle(world.humans[i])
    }
}

fun paintCircle(human: Human){
    context.fillStyle = human.getColor()
    context.beginPath();
    context.arc(
        human.x,
        human.y,
        human.radius,
        0.0,
        2*PI,
        false)
    context.stroke();
    context.fill();
}

fun clear() {
    context.fillStyle = "#FFFFFF"
    context.fillRect(0.0, 0.0, width.toDouble(), height.toDouble())
    context.strokeStyle = "#000000"
    context.lineWidth = 4.0
    context.strokeRect(0.0, 0.0, width.toDouble(), height.toDouble())
}

fun generateData() {
    for (i in 1 until 10)
        for (j in 1 until 10) {
            val dx = Random.nextDouble(-1.0, 1.0) * 10
            val dy = Random.nextDouble(-1.0, 1.0) * 10
            val human = Human(
                i * 70.0,
                150 + (j * 70.0),
                dx,
                dy,
                dx,
                dy,
                18.0,
                1.0
            )
            world.humans.add(human)
        }
    world.humans.shuffle()
    world.humans[world.humans.size / 2].state = State.INFECTED
    world.humans[world.humans.size / 2].infectedTime = 0.0
}
