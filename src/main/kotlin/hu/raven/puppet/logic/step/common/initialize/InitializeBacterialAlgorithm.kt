package hu.raven.puppet.logic.step.common.initialize

import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import hu.raven.puppet.logic.state.EvolutionaryAlgorithmState
import hu.raven.puppet.logic.step.evolutionary.common.OrderPopulationByCost
import hu.raven.puppet.logic.step.evolutionary.common.initializePopulation.InitializePopulation
import hu.raven.puppet.utility.inject
import kotlinx.coroutines.runBlocking


class InitializeBacterialAlgorithm<S : ISpecimenRepresentation> : InitializeAlgorithm<S>() {
    val initializePopulation: InitializePopulation<S> by inject()
    val orderPopulationByCost: OrderPopulationByCost<S> by inject()
    val algorithm: EvolutionaryAlgorithmState<S> by inject()

    override fun invoke() {
        logger("initializePopulation")
        initializePopulation()
        logger("orderByCost")
        runBlocking { orderPopulationByCost() }
        logger("orderedByCost")
        algorithm.apply {
            copyOfBest = subSolutionFactory.copy(population.first())
            copyOfWorst = subSolutionFactory.copy(population.last())
        }
    }
}