package hu.raven.puppet.logic.evolutionary.bacterial.mutation

import hu.raven.puppet.logic.common.logging.DoubleLogger
import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.statistics.Statistics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject
import kotlin.math.exp
import kotlin.random.Random.Default.nextFloat
import kotlin.random.Random.Default.nextInt

class BacterialMutationWithSimulatedAnnealingThatTouchesAllGenesAndContinuesSegment : BacterialMutation {

    private val statistics: Statistics by inject(Statistics::class.java)
    val logger: DoubleLogger by inject(DoubleLogger::class.java)

    override suspend fun <S : ISpecimenRepresentation> invoke(
        algorithm: BacterialAlgorithm<S>
    ) = withContext(Dispatchers.Default) {
        algorithm.run {
            logger(simulatedAnnealingHeat(iteration, iterationLimit).toString())

            val randomStartPosition =
                nextInt(cloneSegmentLength)

            population.forEachIndexed { index, specimen ->
                launch {
                    val oldCost = specimen.cost

                    mutateSpecimen(
                        specimen,
                        randomStartPosition,
                        index != 0
                    )

                    if (specimen.cost == oldCost) {
                        return@launch
                    }

                    statistics.mutationImprovementCountOnAll++

                    if (index == 0) {
                        statistics.mutationImprovementCountOnBest++
                    }
                }
            }
        }
    }

    private fun <S : ISpecimenRepresentation> BacterialAlgorithm<S>.mutateSpecimen(
        specimen: S,
        randomStartPosition: Int,
        doSimulatedAnnealing: Boolean
    ) {
        repeat(cloneCycleCount) { cycleCount ->

            val segmentPosition = randomStartPosition + cycleCount * cloneSegmentLength
            val selectedPositions = IntArray(cloneSegmentLength) { segmentPosition + it }
            val selectedElements = IntArray(cloneSegmentLength) {
                specimen[selectedPositions[it]]
            }

            val clones = generateClones(
                specimen,
                selectedPositions,
                selectedElements
            )

            calcCostOfEachAndSort(clones)

            loadDataToSpecimen(
                specimen,
                clones,
                doSimulatedAnnealing
            )
        }
    }

    private fun <S : ISpecimenRepresentation> BacterialAlgorithm<S>.generateClones(
        specimen: S,
        selectedPositions: IntArray,
        selectedElements: IntArray
    ): MutableList<S> {
        val clones = MutableList(cloneCount + 1) { subSolutionFactory.copy(specimen) }
        clones
            .slice(1 until clones.size)
            .forEach { clone ->
                mutationOperator(
                    clone,
                    selectedPositions,
                    selectedElements
                )
            }
        return clones
    }

    private fun <S : ISpecimenRepresentation> BacterialAlgorithm<S>.loadDataToSpecimen(
        specimen: S,
        clones: MutableList<S>,
        doSimulatedAnnealing: Boolean
    ) {
        if (!doSimulatedAnnealing || nextFloat() > simulatedAnnealingHeat(iteration, iterationLimit)) {
            specimen.setData(clones.first().getData())
            specimen.cost = clones.first().cost
            return
        }

        specimen.setData(clones[1].getData())
        specimen.cost = clones[1].cost
    }

    private fun simulatedAnnealingHeat(iteration: Int, divider: Int): Float {
        return 1 / (1 + exp(iteration.toFloat() / divider))
    }
}