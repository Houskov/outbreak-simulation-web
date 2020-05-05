package global.view

import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import world

class GraphPainter(
    var graphCanvas: HTMLCanvasElement
) {

    init {
        clear()
    }

    val graphContext: CanvasRenderingContext2D
        get() {
            return graphCanvas.getContext("2d") as CanvasRenderingContext2D
        }

    var actualX = 8.0
    var drawingIncrementX = 8.0
    var previousSize = 0

    val graphWidth: Int
        get() {
            return graphCanvas.width
        }

    val graphHeight: Int
        get() {
            return graphCanvas.height
        }

    fun drawGraphFunction(fun1: ArrayList<Int>, fun2: ArrayList<Int>, fun3: ArrayList<Int>) {
        if (previousSize < fun1.size) {
            if (fun1.size > 1) {
                if (actualX < graphCanvas.width) {
                    graphContext.beginPath();
                    drawLine(
                        actualX - drawingIncrementX,
                        graphHeight - ((graphHeight.toDouble() / world.humans.size.toDouble()) * fun1[fun1.size - 2]),
                        actualX,
                        graphHeight - ((graphHeight.toDouble() / world.humans.size.toDouble()) * fun1[fun1.size - 1]),
                        "#ff0000"
                    )
                    drawLine(
                        actualX - drawingIncrementX,
                        graphHeight - ((graphHeight.toDouble() / world.humans.size.toDouble()) * fun2[fun2.size - 2]),
                        actualX,
                        graphHeight - ((graphHeight.toDouble() / world.humans.size.toDouble()) * fun2[fun2.size - 1]),
                        "#32CD32"
                    )
                    drawLine(
                        actualX - drawingIncrementX,
                        graphHeight - ((graphHeight.toDouble() / world.humans.size.toDouble()) * fun3[fun3.size - 2]),
                        actualX,
                        graphHeight - ((graphHeight.toDouble() / world.humans.size.toDouble()) * fun3[fun3.size - 1]),
                        "#0000FF"
                    )

                    actualX += drawingIncrementX
                } else {
                    if (drawingIncrementX > 1.0) {
                        drawingIncrementX--
                    } else {
                        drawingIncrementX /= 2.0
                    }
                    redrawFunction(fun1, fun2, fun3)
                }
            }
            previousSize = fun1.size
        }
    }


    fun redrawFunction(function1: ArrayList<Int>, function2: ArrayList<Int>, function3: ArrayList<Int>) {
        clear()
        var lastDrawnX = 0.0
        actualX = 0.0
        var lastDrawIndex = 0
        for (i in 1 until function1.size - 1) {
            if (actualX.toInt() > lastDrawnX.toInt()) {
                graphContext.beginPath();
                drawLine(
                    lastDrawnX,
                    graphHeight - ((graphHeight.toDouble() / world.humans.size.toDouble()) * function1[lastDrawIndex]),
                    actualX,
                    graphHeight - ((graphHeight.toDouble() / world.humans.size.toDouble()) * function1[i + 1]),
                    "#ff0000"
                )
                drawLine(
                    lastDrawnX,
                    graphHeight - ((graphHeight.toDouble() / world.humans.size.toDouble()) * function2[lastDrawIndex]),
                    actualX,
                    graphHeight - ((graphHeight.toDouble() / world.humans.size.toDouble()) * function2[i + 1]),
                    "#32CD32"
                )
                drawLine(
                    lastDrawnX,
                    graphHeight - ((graphHeight.toDouble() / world.humans.size.toDouble()) * function3[lastDrawIndex]),
                    actualX,
                    graphHeight - ((graphHeight.toDouble() / world.humans.size.toDouble()) * function3[i + 1]),
                    "#0000FF"
                )

                lastDrawIndex = i + 1
                lastDrawnX = actualX
            }
            actualX += drawingIncrementX
        }

    }

    fun clear() {
        graphContext.fillStyle = "#FFFFFF"
        graphContext.fillRect(0.0, 0.0, graphWidth.toDouble(), graphHeight.toDouble())
        graphContext.strokeStyle = "#000000"
        graphContext.lineWidth = 1.0
        graphContext.strokeRect(0.0, 0.0, graphWidth.toDouble(), graphHeight.toDouble())
    }

    fun drawLine(startx: Double, starty: Double, endx: Double, endy: Double, color: String) {
        graphContext.beginPath();
        graphContext.moveTo(
            startx, starty
        )
        graphContext.lineTo(
            endx, endy
        )
        graphContext.strokeStyle = color
        graphContext.stroke()
    }
}