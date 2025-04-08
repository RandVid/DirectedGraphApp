package org.randvid.directedgraphapp

import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors
import java.util.concurrent.Future

class GraphController {
    @FXML
    private lateinit var graphInput: TextArea

    @FXML
    private lateinit var diagramImageView: ImageView

    @FXML
    private lateinit var vertexToolbar: ToolBar

    private var allEdges: List<Pair<String, String?>> = emptyList()
    private val renderExecutor = Executors.newSingleThreadExecutor()
    private var currentRenderTask: Future<*>? = null

    @FXML
    fun initialize() {
        graphInput.textProperty().addListener { _, _, _ -> updateGraph() }
        updateGraph()
    }

    private fun updateGraph() {
        allEdges = parseEdges(graphInput.text)
        val vertices : Set<String> =
            allEdges.flatMap { listOf(it.first, if (it.second == null || it.second == "") it.first else it.second!!) }.toSet()

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

        currentRenderTask?.cancel(true)

        currentRenderTask = renderExecutor.submit {
            try {
                val image = renderPlantUml(plantUmlSource)
                Platform.runLater { diagramImageView.image = image }
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun parseEdges(input: String): List<Pair<String, String?>> =
        input.lines()
            .mapNotNull { line ->
                val parts = line.split("->").map { it.trim() }
                if (parts.size == 2) parts[0] to parts[1]
                else if (parts.size == 1 && parts[0] != "") parts[0] to null
                else null
            }

    fun generatePlantUml(edges: List<Pair<String, String?>>, enabled: Set<String>): String {
        return buildString {
            appendLine("@startuml")
            appendLine("top to bottom direction")

            enabled.forEach { appendLine("class $it") }
            edges.filter { it.first in enabled && it.second in enabled }.forEach { (from, to) ->
                if (to != null) appendLine("$from --> $to")
            }
            appendLine("@enduml")
        }
    }

    private fun renderPlantUml(source: String): Image {
        val os = ByteArrayOutputStream()

        val reader = SourceStringReader(source)
        val desc = reader.generateImage(os, 0, FileFormatOption(FileFormat.PNG))
        if (desc == null) {
            throw RuntimeException("Diagram generation failed — possible Graphviz issue.")
        }
        return Image(ByteArrayInputStream(os.toByteArray()))
    }

    fun shutdown() {
        currentRenderTask?.cancel(true)
        renderExecutor.shutdownNow()
    }
}