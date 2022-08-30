package hu.raven.puppet.logic.evolutionary.bacterial.mutation

import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.statistics.Statistics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent

class BacterialMutationWithElitistSelectionThatTouchesAllGenesAndSpreadSegment : BacterialMutation {

    private val statistics: Statistics by KoinJavaComponent.inject(Statistics::class.java)


    override suspend fun <S : ISpecimenRepresentation> invoke(
        algorithm: BacterialAlgorithm<S>
    ) = withContext(Dispatchers.Default) {
        algorithm.run {
            val randomPermutation = IntArray(geneCount) { it }.apply { shuffle() }

            population.forEachIndexed { index, specimen ->
                launch {
                    val oldCost = specimen.cost

                    mutateSpecimen(specimen, randomPermutation)

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
        randomPermutation: IntArray
    ) {
        repeat(cloneCycleCount) { cycleCount ->
            val segmentStart = cycleCount * cloneSegmentLength
            val segmentEnd = (cycleCount + 1) * cloneSegmentLength
            val selectedPositions = randomPermutation
                .slice(segmentStart until segmentEnd)
                .sortedBy { it }
                .toIntArray()
            val selectedElements = IntArray(cloneSegmentLength) {
                specimen[selectedPositions[it]]
            }

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

            calcCostOfEachAndSort(clones)
            specimen.setData(clones.first().getData())
            specimen.cost = clones.first().cost
        }
    }
}