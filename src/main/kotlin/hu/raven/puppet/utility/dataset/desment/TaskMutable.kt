package hu.raven.puppet.utility.dataset.desment

import hu.raven.puppet.model.dataset.desmet.graph.NodeCoordinate
import hu.raven.puppet.model.dataset.desmet.graph.NodeDemand

class TaskMutable {
    var name: String = ""
    var comments: MutableList<String> = mutableListOf()
    var type: String = ""
    var dimension: Int = 0
    var edgeWeightType: String = ""
    var edgeWeightFormat: String = ""
    var edgeWeightUnitOfMeasurement: String = ""
    var capacity: Int = 0
    var nodeCoordinates: MutableList<NodeCoordinate> = mutableListOf()
    var distanceMatrix: MutableList<DoubleArray> = mutableListOf()
    var nodeDemands: MutableList<NodeDemand> = mutableListOf()
    var depotId: Int = 0
}