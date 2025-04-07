module org.randvid.directedgraphapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;
    requires plantuml;  // Add this line

    opens org.randvid.directedgraphapp to javafx.fxml;
    exports org.randvid.directedgraphapp;  // Add this line
}