package global.view

import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import world

class GraphPainter(
    var graphCanvas: HTMLCanvasElement
) {

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

    fun drawGraphFunction(fun1: ArrayList<Int>) {
        if (previousSize < fun1.size) {
            if (fun1.size > 1) {
                if (actualX < graphCanvas.width) {
                    graphContext.beginPath();
                    graphContext.moveTo(
                        actualX - drawingIncrementX,
                        graphHeight - ((graphHeight.toDouble() / world.humans.size.toDouble()) * fun1[fun1.size - 2])
                    )
                    graphContext.lineTo(
                        actualX,
                        graphHeight - ((graphHeight.toDouble() / world.humans.size.toDouble()) * fun1[fun1.size - 1])
                    )
                    graphContext.stroke()
                    actualX += drawingIncrementX
                } else {
                    if (drawingIncrementX > 1.0) {
                        drawingIncrementX--
                    } else {
                        drawingIncrementX /= 2.0
                    }
                    redrawFunction(fun1)
                }
            }
            previousSize = fun1.size
        }
    }

    var lastDrawnX = 0.0
    var lastDrawIndex = 0
    fun redrawFunction(function: ArrayList<Int>) {
        clear()
        lastDrawnX = 0.0
        actualX = 0.0
        lastDrawIndex = 0
        for (i in 1 until function.size - 1) {
            if (actualX.toInt() > lastDrawnX.toInt()) {
                graphContext.beginPath();
                graphContext.moveTo(
                    lastDrawnX,
                    graphHeight - ((graphHeight.toDouble() / world.humans.size.toDouble()) * function[lastDrawIndex])
                )
                graphContext.lineTo(
                    actualX,
                    graphHeight - ((graphHeight.toDouble() / world.humans.size.toDouble()) * function[i + 1])
                )
                lastDrawIndex = i + 1
                lastDrawnX = actualX
                graphContext.stroke()
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
}