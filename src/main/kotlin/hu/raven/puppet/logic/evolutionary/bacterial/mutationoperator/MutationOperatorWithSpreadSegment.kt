package hu.raven.puppet.logic.evolutionary.bacterial.mutationoperator

import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation

class MutationOperatorWithSpreadSegment : BacterialMutationOperator {
    override fun <S : ISpecimenRepresentation> invoke(
        algorithm: BacterialAlgorithm<S>,
        clone: S,
        selectedPositions: IntArray,
        selectedElements: IntArray
    ) {
        val shuffler = (0 until algorithm.cloneSegmentLength).shuffled()
        selectedPositions.forEachIndexed { index, position ->
            clone[position] = selectedElements[shuffler[index]]
        }
    }
}