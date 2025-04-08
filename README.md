# Directed Graph Visualizer

A Kotlin-based GUI application for visualizing and interacting with directed graphs, built with JavaFX and PlantUML.

## Features

- **Interactive Graph Visualization**: Render directed graphs in real-time as you type
- **Vertex Management**: 
  - Automatically detects all vertices from input
  - Toggle visibility of individual vertices with checkboxes
- **Responsive UI**: Asynchronous rendering prevents UI freezes
- **Simple Input Syntax**: Define graphs using intuitive edge notation (`A -> B`)

## Screenshot
![](https://github.com/user-attachments/assets/fb66b0b3-ed7b-4bae-b8fc-b0028f918fe2)

## Installation

### Building from Source
``` bash
git clone https://github.com/RandVid/DirectedGraphApp.git
cd DirectedGraphApp
.\gradlew build
.\gradlew run
```
## Usage

### 1. Define Your Graph:
In the text input area, enter edges using the format:

Enter edges in the text area, one edge or vertex per line:

```
A -> B
B -> C
C -> A
D
```

### 2. Managing Vertices
- The application automatically detects all vertices

- Each vertex appears as a checkbox in the toolbar

- To hide a vertex: Uncheck its checkbox

- To show a vertex: Check its checkbox

### 3. Viewing the Diagram
- The diagram updates automatically when:

  - You modify the graph text

  - You toggle vertex visibility

- Default layout: Top-to-bottom hierarchy

## Troubleshooting
If diagrams fail to render:

1. Ensure Graphviz is installed on your system

2. Verify Java 21+ is installed

3. Check console for error messages
