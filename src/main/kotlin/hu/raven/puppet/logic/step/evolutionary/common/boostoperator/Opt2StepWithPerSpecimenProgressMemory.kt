package hu.raven.puppet.logic.step.evolutionary.common.boostoperator

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.model.logging.StepEfficiencyData
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class Opt2StepWithPerSpecimenProgressMemory<S : ISpecimenRepresentation> : BoostOperator<S>() {

    var lastPositionPerSpecimen = arrayOf<Pair<Int, Int>>()

    @OptIn(ExperimentalTime::class)
    override fun invoke(specimen: S): StepEfficiencyData {
        var spentBudget = 0L
        val oldCost = specimen.cost
        val spentTime = measureTime {
            if (lastPositionPerSpecimen.isEmpty()) {
                lastPositionPerSpecimen = Array(sizeOfPopulation) { Pair(0, 1) }
            }

            val bestCost = specimen.cost
            var improved = false

            var lastPosition = lastPositionPerSpecimen[specimen.id]

            outer@ for (firstIndex in lastPosition.first until algorithmState.population.first().permutationSize - 1) {
                val secondIndexStart =
                    if (firstIndex == lastPosition.first) lastPosition.second
                    else firstIndex + 1
                for (secondIndex in secondIndexStart until algorithmState.population.first().permutationSize) {
                    specimen.swapGenes(firstIndex, secondIndex)
                    calculateCostOf(specimen)
                    spentBudget++

                    if (specimen.cost >= bestCost) {
                        specimen.swapGenes(firstIndex, secondIndex)
                        specimen.cost = bestCost
                        continue
                    }

                    improved = true
                    lastPosition = Pair(firstIndex, secondIndex)
                    break@outer
                }
            }

            if (!improved) {
                lastPosition = Pair(0, 1)
            }
            lastPositionPerSpecimen[specimen.id] = lastPosition
        }

        return StepEfficiencyData(
            spentTime = spentTime,
            spentBudget = spentBudget,
            improvementCountPerRun = if (specimen.cost < oldCost) 1 else 0,
            improvementPercentagePerBudget =
            if (specimen.cost < oldCost)
                (1 - (specimen.cost / oldCost)) / spentBudget
            else
                0.0
        )
    }
}