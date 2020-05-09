import global.model.Human
import com.example.covidsimulator.global.model.State
import global.physic.InfectionWorld
import global.view.GraphPainter
import org.w3c.dom.*
import kotlin.browser.document
import kotlin.browser.window
import kotlin.random.*
import kotlin.math.*
import jquery.*


val day = document.getElementById("day-text")
val immune = document.getElementById("immune-text")
val infected = document.getElementById("infected-text")
val healthy = document.getElementById("healthy-text")
val total = document.getElementById("total-text")
val maxr = document.getElementById("maxr") as HTMLInputElement
val maxrOutput = document.getElementById("maxrOutput") as HTMLOutputElement
val percentStaticPopulation = document.getElementById("percentStaticPopulation") as HTMLInputElement
val percentStaticPopulationOutput = document.getElementById("percentStaticPopulationOutput") as HTMLOutputElement
val reimportInfectionDays = document.getElementById("reimportInfectionDays") as HTMLInputElement
val reimportInfectionDaysOutput = document.getElementById("reimportInfectionDaysOutput") as HTMLOutputElement
val imunityLength = document.getElementById("imunityLength") as HTMLInputElement
val imunityLengthOutput = document.getElementById("imunityLengthOutput") as HTMLOutputElement
val transmissionProbability = document.getElementById("transmissionProbability") as HTMLInputElement
val transmissionProbabilityOutput = document.getElementById("transmissionProbabilityOutput") as HTMLOutputElement
val ballsCount = document.getElementById("ballsCount") as HTMLInputElement
val ballsCountOutput = document.getElementById("ballsCountOutput") as HTMLOutputElement
val diseaseLength = document.getElementById("diseaseLength") as HTMLInputElement
val diseaseLengthOutput = document.getElementById("diseaseLengthOutput") as HTMLOutputElement
val diseasesSelect = document.getElementById("diseases") as HTMLSelectElement

var lastBallsCount = 500
var world: InfectionWorld = InfectionWorld(0.016)

val canvas = initializeCanvas()
val graphCanvas = initializeGraphCanvas()
fun initializeCanvas(): HTMLCanvasElement {
    val div = document.getElementById("balls-container")
    val canvas = document.createElement("canvas") as HTMLCanvasElement
    val context = canvas.getContext("2d") as CanvasRenderingContext2D
    context.canvas.width = 500
    context.canvas.height = 500
    div!!.appendChild(canvas)
    return canvas
}

var graphPainter: GraphPainter = GraphPainter(graphCanvas)


fun initializeGraphCanvas(): HTMLCanvasElement {
    val div = document.getElementById("graph-canvas")
    val canvas = document.createElement("canvas") as HTMLCanvasElement
    val context = canvas.getContext("2d") as CanvasRenderingContext2D
    context.canvas.width = 300
    context.canvas.height = 100
    div!!.appendChild(canvas)
    return canvas
}

val context: CanvasRenderingContext2D
    get() {
        return canvas.getContext("2d") as CanvasRenderingContext2D
    }
val graphContext: CanvasRenderingContext2D
    get() {
        return graphCanvas.getContext("2d") as CanvasRenderingContext2D
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
var skipped:Int = 0
fun main(args: Array<String>) {
    setParametersForUltraInfectious()
    selectDiseaseHandler()
    restart(ballsCount.value.toInt() * 10)
    window.setInterval({
        updateSettings()
        world.update()
        if(skipped>=8) {
            drawData()
            skipped = 0
        } else {
            skipped++
        }
        updateStats()

    }, 4)

}

fun updateSettings() {
    if (lastBallsCount != ballsCount.value.toInt() * 10) {
        val count = ballsCount.value.toInt() * 10
        restart(count)
        lastBallsCount = count
        ballsCountOutput.textContent = count.toString()
    }
    maxrOutput.textContent = maxr.value
    world.maxR = maxr.value.toInt()
    percentStaticPopulationOutput.textContent = percentStaticPopulation.value + "%"
    world.staticPopulationPercent = percentStaticPopulation.value.toInt()
    reimportInfectionDaysOutput.textContent = reimportInfectionDays.value + " days"
    world.imporInfectedAfterDays = reimportInfectionDays.value.toInt()
    imunityLengthOutput.textContent = imunityLength.value + " weeks"
    world.imunityLength = imunityLength.value.toDouble() * 7
    transmissionProbabilityOutput.textContent = (transmissionProbability.value.toDouble() / 100).toString()
    world.infectionProbilityPerCollision = transmissionProbability.value.toDouble() / 100
    diseaseLengthOutput.textContent = diseaseLength.value + " days"
    world.infectionLength = diseaseLength.value.toInt()
}

fun updateStats() {
    day!!.textContent = "Day total: " + world.totalTime.toInt().toString()
    infected!!.textContent = "Infected people: " + world.infected.toString()
    immune!!.textContent = "Immune people: " + world.immune.toString()
    healthy!!.textContent = "Healthy people: " + (world.humans.size - world.infected - world.immune).toString()
    total!!.textContent = "Total people: " + world.humans.size.toString()
    updateGraph()
}

private fun updateGraph() {
    graphPainter.drawGraphFunction(world.infectedHistory, world.immuneHistory, world.healthyHistory)
}

fun drawData() {
    clear()
    repaint()
}

fun repaint() {
    for (i in 0 until world.humans.size) {
        paintCircle(world.humans[i])
    }
}

fun paintCircle(human: Human) {
    context.fillStyle = human.getColor()
    context.beginPath();
    context.arc(
        human.x / (world.worldWidth.toDouble() / width.toDouble()),
        human.y / (world.worldHeight.toDouble() / height.toDouble()),
        human.radius / (world.worldWidth.toDouble() / width.toDouble()),
        0.0,
        2 * PI,
        false
    )
    //context.stroke();
    context.fill();
}

fun clear() {
    context.fillStyle = "#FFFFFF"
    context.fillRect(0.0, 0.0, width.toDouble(), height.toDouble())
    context.strokeStyle = "#000000"
    context.lineWidth = 1.0
    context.strokeRect(0.0, 0.0, width.toDouble(), height.toDouble())
}

fun generateData(ballCount: Int) {
    val ballCountLine = ceil(sqrt(ballCount.toDouble())).toInt()
    for (i in 1 until ballCountLine + 1)
        for (j in 1 until ballCountLine + 1) {
            if (world.humans.size >= ballCount) {
                break
            }
            val dx = Random.nextDouble(-1.0, 1.0) * 5
            val dy = Random.nextDouble(-1.0, 1.0) * 5
            val human = Human(
                (i * (world.worldWidth / (ballCountLine + 1))).toDouble(),
                (j * (world.worldHeight / (ballCountLine + 1))).toDouble(),
                dx * Random.nextDouble(1.0, 9.0),
                dy * Random.nextDouble(1.0, 9.0),
                dx,
                dy,
                16.0,
                1.0
            )
            world.humans.add(human)
        }
    world.humans.shuffle()
    world.humans[world.humans.size / 2].state = State.INFECTED
    world.humans[world.humans.size / 2].infectedTime = 0.0
}

fun restart(ballCount: Int) {
    world = InfectionWorld(0.016)
    graphPainter = GraphPainter(graphCanvas)
    val size = sqrt(4000.0 * ballCount.toDouble()).toInt()
    world.setWorldSize(size, size)
    generateData(ballCount)
}

fun selectDiseaseHandler() {
    diseasesSelect.addEventListener("change", {
        if (diseasesSelect.value == "ultrainfectious") {
            maxr.value = 5.toString()
            percentStaticPopulation.value = 10.toString()
            reimportInfectionDays.value = 0.toString()
            imunityLength.value = 2.toString()
            transmissionProbability.value = 80.toString()
            diseaseLength.value = 10.toString()
        } else if (diseasesSelect.value == "covid19") {
            maxr.value = 5.toString()
            percentStaticPopulation.value = 10.toString()
            reimportInfectionDays.value = 0.toString()
            imunityLength.value = 104.toString()
            transmissionProbability.value = 15.toString()
            diseaseLength.value = 14.toString()
        }
        restart(lastBallsCount)
    })
}

fun setParametersForUltraInfectious(){
    maxr.value = 5.toString()
    percentStaticPopulation.value = 10.toString()
    reimportInfectionDays.value = 0.toString()
    imunityLength.value = 2.toString()
    transmissionProbability.value = 80.toString()
    diseaseLength.value = 10.toString()
}
