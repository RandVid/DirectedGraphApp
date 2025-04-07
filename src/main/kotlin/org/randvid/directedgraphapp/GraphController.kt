package org.randvid.directedgraphapp

import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class GraphController {
    @FXML
    private lateinit var graphInput: TextArea

    @FXML
    private lateinit var diagramImageView: ImageView

    @FXML
    private lateinit var vertexToolbar: ToolBar

    private var allEdges: List<Pair<String, String?>> = emptyList()

    @FXML
    fun initialize() {
        graphInput.textProperty().addListener { _, _, _ -> updateGraph() }
        updateGraph() // Initial render
    }

    private fun updateGraph() {
        allEdges = parseEdges(graphInput.text)
        val vertices : Set<String> =
            allEdges.flatMap { listOf(it.first, if (it.second == null || it.second == "") it.first else it.second!!) }.toSet()

        // Regenerate checkboxes
        vertexToolbar.items.clear()
        vertices.sorted().forEach { vertex ->
            val checkBox = CheckBox(vertex).apply {
                isSelected = true
                setOnAction { updateDiagram() }
            }
            vertexToolbar.items.add(checkBox)
        }

        updateDiagram()
    }

    private fun updateDiagram() {
        val enabledVertices = vertexToolbar.items
            .filterIsInstance<CheckBox>()
            .filter { it.isSelected }
            .map { it.text }
            .toSet()

        val plantUmlSource = generatePlantUml(allEdges, enabledVertices)
        val image = renderPlantUml(plantUmlSource)
        diagramImageView.image = image
    }

    private fun parseEdges(input: String): List<Pair<String, String?>> =
        input.lines()
            .mapNotNull { line ->
                val parts = line.split("->").map { it.trim() }
                if (parts.size >= 2) parts[0] to parts[1] else null
            }

    private fun generatePlantUml(edges: List<Pair<String, String?>>, enabled: Set<String>): String {
        val filteredEdges = edges.filter { it.first in enabled && it.second in enabled }
        return buildString {
            append("@startuml\n")
            append("skinparam classAttributeIconSize 0\n")
            enabled.forEach{ append("class $it\n") }
            for ((from, to) in filteredEdges) {
                println("$from $to")
                if (to != null) append("$from -> $to\n")
            }
            append("@enduml\n")
        }
    }

    private fun renderPlantUml(source: String): Image {
        val os = ByteArrayOutputStream()

        val reader = SourceStringReader(source)
        val desc = reader.generateImage(os, 0, FileFormatOption(FileFormat.PNG, false))
        if (desc == null) {
            throw RuntimeException("Diagram generation failed — possible Graphviz issue.")
        }

        return Image(ByteArrayInputStream(os.toByteArray()))
    }
}