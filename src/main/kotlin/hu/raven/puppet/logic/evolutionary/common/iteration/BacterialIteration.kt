package hu.raven.puppet.logic.evolutionary.common.iteration

import hu.raven.puppet.logic.AAlgorithm4VRP
import hu.raven.puppet.logic.common.logging.DoubleLogger
import hu.raven.puppet.logic.evolutionary.BacterialAlgorithm
import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.utility.runIfInstanceOf
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class BacterialIteration : EvolutionaryIteration {
    val logger: DoubleLogger by KoinJavaComponent.inject(DoubleLogger::class.java)

    override fun <S : ISpecimenRepresentation> invoke(
        algorithm: SEvolutionaryAlgorithm<S>,
        manageLifeCycle: Boolean
    ) = runBlocking {
        algorithm.runIfInstanceOf<BacterialAlgorithm<S>> {
            if (manageLifeCycle)
                state = AAlgorithm4VRP.State.RESUMED

            runAndLogTime("boost") {
                boost()
            }
            runAndLogTime("geneTransfer") {
                geneTransfer()
            }
            runAndLogTime("mutate") {
                mutate()
            }
            runAndLogTime("orderPopulationByCost") {
                orderPopulationByCost()
            }

            copyOfBest = subSolutionFactory.copy(population.first())
            copyOfWorst = subSolutionFactory.copy(population.last())
            iteration++
            if (manageLifeCycle)
                state = AAlgorithm4VRP.State.INITIALIZED
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun runAndLogTime(name: String, action: suspend () -> Unit) {
        measureTime {
            action()
        }.let {
            logger("$name time: $it")
        }
    }
}