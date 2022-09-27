package hu.raven.puppet.logic.step.common.calculatecost

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.utility.extention.getEdgeBetween
import hu.raven.puppet.utility.inject
import kotlin.math.max

class CalculateCostOfCVRPSolutionWithCapacityAndMaxTripLengrh<S : ISpecimenRepresentation> : CalculateCost<S>() {
    val doubleLogger: DoubleLogger by inject()
    override fun invoke(specimen: ISpecimenRepresentation) {
        statistics.fitnessCallCount++
        taskHolder.run {
            var sumCost = 0L
            var startCount = 0
            var endCount = 0
            specimen.forEachSliceIndexed { sliceIndex, slice ->
                //val salesman = task.salesmen[sliceIndex]
                var takenCapacity = 0L
                sumCost = max(
                    sumCost,
                    slice.mapIndexed { index, value ->
                        when (index) {
                            0 -> {
                                startCount++
                                val fromCenterEdge = task.costGraph.edgesFromCenter[value]
                                takenCapacity += task.costGraph.objectives[value].volume_Stere
                                if (index != slice.size - 1) {
                                    fromCenterEdge.length_Meter
                                } else {
                                    endCount++
                                    val toCenterEdge = task.costGraph.edgesToCenter[value]
                                    fromCenterEdge.length_Meter + toCenterEdge.length_Meter
                                }
                            }

                            slice.size - 1 -> {
                                endCount++
                                //if (takenCapacity + task.costGraph.objectives[value].volume_Stere < salesman.volumeCapacity_Stere) {
                                val betweenEdge = task.costGraph.getEdgeBetween(slice[index - 1], value)
                                val toCenterEdge = task.costGraph.edgesToCenter[value]
                                takenCapacity += task.costGraph.objectives[value].volume_Stere
                                (betweenEdge.length_Meter + toCenterEdge.length_Meter)
                                /*} else {
                                    val fromPreviousToCenterEdge = task.costGraph.edgesFromCenter[slice[index - 1]]
                                    val fromCenterEdge = task.costGraph.edgesFromCenter[value]
                                    val toCenterEdge = task.costGraph.edgesFromCenter[value]
                                    takenCapacity += task.costGraph.objectives[value].volume_Stere
                                    (
                                            fromPreviousToCenterEdge.length_Meter +
                                                    fromCenterEdge.length_Meter +
                                                    toCenterEdge.length_Meter
                                            ).toDouble() / 1000.0
                                }

                                 */
                            }

                            else -> {
                                //if (takenCapacity + task.costGraph.objectives[value].volume_Stere < salesman.volumeCapacity_Stere) {
                                val betweenEdge = task.costGraph.getEdgeBetween(slice[index - 1], value)
                                betweenEdge.length_Meter
                                /* } else{
                                     val fromPreviousToCenterEdge = task.costGraph.edgesFromCenter[slice[index - 1]]
                                     val fromCenterEdge = task.costGraph.edgesFromCenter[value]
                                     (fromPreviousToCenterEdge.length_Meter + fromCenterEdge.length_Meter).toDouble() / 1000.0
                                 }

                                 */
                            }
                        }

                    }.sum()
                )
            }
            if (endCount != startCount) {
                doubleLogger("startCount: $startCount endCount: $endCount")
            }
            specimen.cost = sumCost.toDouble()
            specimen.costCalculated = true
            if (sumCost == 0L) {
                println("Impossible!")
            }
        }
    }
}