package hu.raven.puppet.logic.logging

import hu.raven.puppet.logic.logging.CSVLineBuilderUtility.appendDuration
import hu.raven.puppet.logic.logging.CSVLineBuilderUtility.appendField
import hu.raven.puppet.logic.logging.CSVLineBuilderUtility.appendString
import hu.raven.puppet.logic.logging.CSVLineBuilderUtility.buildCsvLine
import hu.raven.puppet.model.logging.*
import java.io.File

class CSVLogger : AlgorithmLogger() {
    override val targetFile: File = File("$outputFolderPath\\statistics-$creationTime.csv")

    init {
        val header = produceHeader()
        targetFile.appendText(header)
    }

    private fun produceHeader(): String {
        return buildCsvLine {
            appendString("generation")
            appendString("timeTotal")
            appendString("timeOfIteration")
            appendString("fitnessCallCountSoFar")
            appendString("fitnessCallCountOfIteration")

            appendString("idOfBest")
            appendString("costOfBest")

            appendString("idOfSecond")
            appendString("costOfSecond")

            appendString("idOfThird")
            appendString("costOfThird")

            appendString("idOfWorst")
            appendString("costOfWorst")

            appendString("idOfMedian")
            appendString("costOfMedian")

            appendString("diversity")
            appendString("geneBalance")

            appendString("spentTimeOfMutation")
            appendString("spentBudgetOfMutation")
            appendString("improvementCountPerRunOfMutation")
            appendString("improvementCountPerBudgetOfMutation")

            appendString("spentTimeOfMutationOnBest")
            appendString("spentBudgetOfMutationOnBest")
            appendString("improvementCountPerRunOfMutationOnBest")
            appendString("improvementCountPerBudgetOfMutationOnBest")

            appendString("spentTimeOfGeneTransfer")
            appendString("spentBudgetOfGeneTransfer")
            appendString("improvementCountPerRunOfGeneTransfer")
            appendString("improvementCountPerBudgetOfGeneTransfer")


            appendString("spentTimeOfBoost")
            appendString("spentBudgetOfBoost")
            appendString("improvementCountPerRunOfBoost")
            appendString("improvementCountPerBudgetOfBoost")

            appendString("spentTimeOfBoostOnBest")
            appendString("spentBudgetOfBoostOnBest")
            appendString("improvementCountPerRunOfBoostOnBest")
            appendString("improvementCountPerBudgetOfBoostOnBest")
        }
    }

    operator fun invoke(message: BacterialMemeticAlgorithmLogLine) {
        val line = toCsvLine(message)
        targetFile.appendText(line)
    }

    private fun toCsvLine(message: BacterialMemeticAlgorithmLogLine): String {
        return buildCsvLine {
            appendProgressData(message.progressData)
            appendPopulationData(message.populationData)

            appendStepEfficiencyData(message.mutationImprovement)
            appendStepEfficiencyData(message.mutationOnBestImprovement)
            appendStepEfficiencyData(message.geneTransferImprovement)
            appendStepEfficiencyData(message.boostImprovement)
            appendStepEfficiencyData(message.boostOnBestImprovement)
        }
    }

    private fun StringBuilder.appendProgressData(progressData: ProgressData) {
        appendField(progressData.generation)
        appendDuration(progressData.spentTimeTotal)
        appendDuration(progressData.spentTimeOfGeneration)
        appendField(progressData.spentBudgetTotal)
        appendField(progressData.spentBudgetOfGeneration)
    }

    private fun StringBuilder.appendPopulationData(populationData: PopulationData) {
        appendSpecimenData(populationData.best)
        appendSpecimenData(populationData.second)
        appendSpecimenData(populationData.third)
        appendSpecimenData(populationData.worst)
        appendSpecimenData(populationData.median)

        appendField(populationData.diversity)
        appendField(populationData.geneBalance)
    }

    private fun StringBuilder.appendSpecimenData(specimenData: SpecimenData?) {
        appendField(specimenData?.id)
        appendField(specimenData?.cost)
    }

    private fun StringBuilder.appendStepEfficiencyData(stepEfficiencyData: StepEfficiencyData) {
        appendField(stepEfficiencyData.spentTime)
        appendField(stepEfficiencyData.spentBudget)

        appendField(stepEfficiencyData.spentTime)
        appendField(stepEfficiencyData.improvementPercentagePerBudget)
    }


}