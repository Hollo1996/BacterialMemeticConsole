package hu.raven.puppet.logic.step.evolutionary.common.diversity

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.abs

class DiversityByMatrixDistanceFromBest<S : ISpecimenRepresentation> : Diversity<S>() {

    override fun invoke(): Unit = runBlocking {
        val best = algorithmState.copyOfBest!!
        val matrixOfBest = preceditionMatrixWithDistance(best)
        statistics.diversity = 0.0

        algorithmState.population
            .map {
                CoroutineScope(Dispatchers.IO).launch {
                    val matrix = preceditionMatrixWithDistance(it)
                    val distance = distanceOfSpecimen(matrixOfBest, matrix)
                    synchronized(statistics) {
                        statistics.diversity += distance
                    }
                }
            }
            .forEach { it.join() }
    }

    private fun distanceOfSpecimen(
        matrixFrom: Array<IntArray>,
        matrixTo: Array<IntArray>
    ): Long {
        var distance = 0L
        for (fromIndex in matrixFrom.indices) {
            for (toIndex in matrixFrom[fromIndex].indices) {
                distance += abs(
                    matrixFrom[fromIndex][toIndex] -
                            matrixTo[fromIndex][toIndex]
                )
            }
        }
        return distance
    }

    private fun <S : ISpecimenRepresentation> preceditionMatrixWithDistance(
        specimen: S
    ): Array<IntArray> {
        val inverse = specimen.inverseOfPermutation()
        return Array(inverse.size) { fromIndex ->
            IntArray(inverse.size) { toIndex ->
                inverse[fromIndex] - inverse[toIndex]
            }
        }
    }
}