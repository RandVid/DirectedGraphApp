package org.randvid.directedgraphapp

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.stage.Stage

class GraphApplication : Application() {
    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(GraphApplication::class.java.getResource("graph-view.fxml"))
        val root: VBox = fxmlLoader.load()
        val controller: GraphController = fxmlLoader.getController()
        val scene = Scene(root, 960.0, 600.0)

        stage.title = "Directed Graph Visualizer"
        stage.scene = scene

        stage.setOnCloseRequest {
            controller.shutdown()
        }
        stage.show()
    }
}

fun main() {
    Application.launch(GraphApplication::class.java)
}