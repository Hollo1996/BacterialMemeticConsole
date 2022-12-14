package hu.raven.puppet.logic.step.evolutionary.bacterial.genetransfer

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.statistics.BacterialAlgorithmStatistics
import hu.raven.puppet.logic.step.evolutionary.bacterial.genetransferoperator.GeneTransferOperator
import hu.raven.puppet.utility.extention.sum
import hu.raven.puppet.utility.inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class GeneTransferByFold<S : ISpecimenRepresentation> : GeneTransfer<S>() {
    val geneTransferOperator: GeneTransferOperator<S> by inject()
    val statistics: BacterialAlgorithmStatistics by inject()

    override suspend fun invoke(): Unit = withContext(Dispatchers.Default) {
        algorithmState.run {
            (0 until injectionCount)
                .map { injectionIndex ->
                    async {
                        val specimenIndex = injectionIndex % (sizeOfPopulation / 2)

                        val donor =
                            population[specimenIndex]
                        val acceptor =
                            population[population.lastIndex - specimenIndex]

                        synchronized(acceptor) {
                            geneTransferOperator(donor, acceptor)
                        }
                    }
                }
                .map { it.await() }
                .sum()
                .also {
                    synchronized(statistics) {
                        statistics.geneTransferImprovement = it
                    }
                }
        }
    }
}