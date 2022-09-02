package hu.raven.puppet.logic.evolutionary.bacterial.mutation

import hu.raven.puppet.logic.common.steps.calculatecost.CalculateCost
import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.evolutionary.bacterial.mutationoperator.BacterialMutationOperator
import hu.raven.puppet.logic.evolutionary.bacterial.selectsegment.SelectSegment
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject
import kotlin.random.Random

class BacterialMutationByElitistSelectionOnRandomSequence<S : ISpecimenRepresentation>(
    val mutationPercentage: Float,
    override val algorithm: BacterialAlgorithm<S>
) : BacterialMutation<S> {

    val statistics: BacterialAlgorithmStatistics by inject(BacterialAlgorithmStatistics::class.java)
    val calculateCostOf: CalculateCost<S> by inject(CalculateCost::class.java)
    val mutationOperator: BacterialMutationOperator<S> by inject(BacterialMutationOperator::class.java)
    val selectSegment: SelectSegment<S> by inject(BacterialMutationOperator::class.java)

    override suspend fun invoke(): Unit = withContext(Dispatchers.Default) {
        algorithm.run {
            population.mapIndexed { index, specimen ->
                launch {
                    if (index != 0 && Random.nextFloat() > mutationPercentage) {
                        return@launch
                    }
                    val oldCost = specimen.cost

                    mutateSpecimen(specimen)

                    if (specimen.cost == oldCost) {
                        return@launch
                    }

                    synchronized(statistics) {
                        statistics.mutationImprovement = statistics.mutationImprovement.run {
                            copy(improvementCountTotal = improvementCountTotal + 1)
                        }

                        if (index == 0) {
                            statistics.mutationOnBestImprovement = statistics.mutationOnBestImprovement.run {
                                copy(improvementCountTotal = improvementCountTotal + 1)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun BacterialAlgorithm<S>.mutateSpecimen(
        specimen: S
    ) {
        repeat(cloneCycleCount) {
            val selectedPositions = selectSegment(specimen)
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