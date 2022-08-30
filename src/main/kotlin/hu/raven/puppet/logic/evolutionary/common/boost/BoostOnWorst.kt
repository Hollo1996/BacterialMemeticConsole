package hu.raven.puppet.logic.evolutionary.common.boost

import hu.raven.puppet.logic.evolutionary.SEvolutionaryAlgorithm
import hu.raven.puppet.logic.evolutionary.common.boostoperator.BoostOperator
import hu.raven.puppet.logic.specimen.ISpecimenRepresentation
import org.koin.java.KoinJavaComponent.inject

class BoostOnWorst<S : ISpecimenRepresentation>(
    override val algorithm: SEvolutionaryAlgorithm<S>
) : Boost<S> {
    val boostOperator: BoostOperator<S> by inject(BoostOperator::class.java)

    override suspend operator fun invoke() {
        val worst = algorithm.population.last()
        boostOperator(worst)
    }
}