package hu.raven.puppet.utility.extention

import hu.raven.puppet.logic.logging.DoubleLogger
import hu.raven.puppet.model.logging.ProgressData
import hu.raven.puppet.model.logging.SpecimenData
import hu.raven.puppet.model.logging.StepEfficiencyData


fun DoubleLogger.logProgress(progressData: ProgressData) {
    progressData.apply {
        invoke("generation: ${this.generation}")
        invoke("spent budget total: ${this.spentBudgetTotal}")
        invoke("spent time total: ${this.spentTimeTotal}")
        invoke("spent budget of generation: ${this.spentBudgetOfGeneration}")
        invoke("spent time of generation: ${this.spentTimeOfGeneration}")
    }
}

fun DoubleLogger.logSpecimen(name: String, specimenData: SpecimenData) {
    invoke("$name: id: ${specimenData.id} cost: ${specimenData.cost}")
}

fun DoubleLogger.logStepEfficiency(name: String, stepEfficiencyData: StepEfficiencyData) {
    stepEfficiencyData.apply {
        invoke("$name:")
        invoke("spentTime: $spentTime spentBudget: $spentBudget")
        invoke("improvement: count: $improvementCountPerRun percentage: $improvementPercentagePerBudget")
    }

}