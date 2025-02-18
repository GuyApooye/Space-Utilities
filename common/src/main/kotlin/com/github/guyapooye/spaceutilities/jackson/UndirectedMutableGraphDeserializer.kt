package com.github.guyapooye.spaceutilities.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.google.common.graph.GraphBuilder
import com.google.common.graph.MutableGraph
import org.valkyrienskies.core.api.ships.properties.ShipId


class UndirectedMutableShipIdGraphDeserializer : StdDeserializer<MutableGraph<ShipId>>(MutableGraph::class.java) {

    override fun deserialize(p: JsonParser, ctx: DeserializationContext): MutableGraph<ShipId> {
        val node: JsonNode = p.codec.readTree(p)

        val graph: MutableGraph<ShipId> = GraphBuilder.directed().build()

        if (node.isArray) {
            for (elem in node) {
                val source = elem.get("source").asLong()
                val target = elem.get("target").asLong()
                graph.putEdge(source, target)
            }
        }

        return graph
    }
}