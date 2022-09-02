package hu.raven.puppet.logic.evolutionary.bacterial.mutationoperator

import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.utility.extention.shuffled
import org.koin.java.KoinJavaComponent.inject

class MutationOperatorWithContinuesSegment<S : ISpecimenRepresentation>(
    override val algorithm: BacterialAlgorithm<S>,

    ) : BacterialMutationOperator<S> {

    val statistics: BacterialAlgorithmStatistics by inject(BacterialAlgorithmStatistics::class.java)
    override fun invoke(
        clone: S,
        selectedPositions: IntArray,
        selectedElements: IntArray
    ) {
        synchronized(statistics) {
            statistics.mutationImprovement = statistics.mutationImprovement.run {
                copy(operatorCallCount = operatorCallCount + 1)
            }
        }

        selectedPositions
            .shuffled()
            .forEachIndexed { readIndex, writeIndex ->
                clone[writeIndex] = selectedElements[readIndex]
            }
    }
}