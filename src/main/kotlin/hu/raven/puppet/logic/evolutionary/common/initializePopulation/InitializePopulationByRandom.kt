package hu.raven.puppet.logic.evolutionary.common.initializePopulation

import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.specimen.DOnePartRepresentation
import hu.raven.puppet.logic.specimen.DTwoPartRepresentation
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import kotlin.random.Random
import kotlin.random.nextInt

class InitializePopulationByRandom : InitializePopulation {
    override fun <S : ISpecimenRepresentation> invoke(algorithm: SEvolutionaryAlgorithm<S>) {
        algorithm.population = if (algorithm.costGraph.objectives.size != 1)
            ArrayList(List(algorithm.sizeOfPopulation) { specimenIndex ->
                algorithm.subSolutionFactory.produce(
                    specimenIndex,
                    Array(algorithm.salesmen.size) { index ->
                        if (index == 0)
                            IntArray(algorithm.costGraph.objectives.size) { it }
                        else
                            intArrayOf()
                    }
                )
            })
        else arrayListOf(
            algorithm.subSolutionFactory.produce(
                0,
                arrayOf(IntArray(algorithm.costGraph.objectives.size) { it })
            )
        )

        algorithm.population.forEach { permutation ->
            permutation.shuffle()
            var length = 0
            when (permutation) {
                is DTwoPartRepresentation ->
                    permutation.forEachSliceIndexed { index, _ ->
                        if (index == permutation.sliceLengths.size - 1) {
                            permutation.sliceLengths[index] = algorithm.costGraph.objectives.size - length

                        } else {
                            permutation.sliceLengths[index] =
                                Random.nextInt(0..(algorithm.costGraph.objectives.size - length))
                            length += permutation.sliceLengths[index]
                        }
                    }
                is DOnePartRepresentation -> {

                }
            }
            permutation.iteration = 0
            permutation.costCalculated = false
            permutation.inUse = true
        }
    }
}