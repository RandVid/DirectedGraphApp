package org.randvid.directedgraphapp

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class GraphControllerTest {

    @Test
    fun `parseEdges should handle empty input`() {
        val controller = GraphController()
        val result = controller.parseEdges("")
        assertTrue(result.isEmpty(), "Should return empty list for empty input")
    }

    @Test
    fun `parseEdges should handle single edge`() {
        val controller = GraphController()
        val result = controller.parseEdges("A -> B")
        assertEquals(listOf("A" to "B"), result)
    }

    @Test
    fun `parseEdges should handle multiple edges`() {
        val controller = GraphController()
        val input = """
            A -> B
            B -> C
            C -> A
        """.trimIndent()
        val expected = listOf(
            "A" to "B",
            "B" to "C",
            "C" to "A"
        )
        assertEquals(expected, controller.parseEdges(input))
    }

    @Test
    fun `parseEdges should handle single vertex`() {
        val controller = GraphController()
        val result = controller.parseEdges("A")
        assertEquals(listOf("A" to null), result)
    }

    @Test
    fun `parseEdges should ignore empty lines`() {
        val controller = GraphController()
        val input = """
            A -> B
            
            B -> C
            
        """.trimIndent()
        val expected = listOf(
            "A" to "B",
            "B" to "C"
        )
        assertEquals(expected, controller.parseEdges(input))
    }

    @Test
    fun `generatePlantUml should include only enabled vertices`() {
        val controller = GraphController()
        val edges = listOf(
            "A" to "B",
            "B" to "C",
            "C" to "A"
        )
        val enabled = setOf("A", "B")
        val result = controller.generatePlantUml(edges, enabled)

        assertTrue(result.contains("@startuml"))
        assertTrue(result.contains("top to bottom direction"))
        assertTrue(result.contains("class A"))
        assertTrue(result.contains("class B"))
        assertTrue(result.contains("A --> B"))
        assertFalse(result.contains("class C"))
        assertFalse(result.contains("B --> C"))
        assertFalse(result.contains("C --> A"))
        assertTrue(result.contains("@enduml"))
    }

    @Test
    fun `generatePlantUml should handle empty edges`() {
        val controller = GraphController()
        val edges = emptyList<Pair<String, String?>>()
        val enabled = setOf("A", "B")
        val result = controller.generatePlantUml(edges, enabled)

        assertTrue(result.contains("@startuml"))
        assertTrue(result.contains("top to bottom direction"))
        assertTrue(result.contains("class A"))
        assertTrue(result.contains("class B"))
        assertTrue(result.contains("@enduml"))
    }

    @Test
    fun `generatePlantUml should handle no enabled vertices`() {
        val controller = GraphController()
        val edges = listOf(
            "A" to "B",
            "B" to "C",
            "C" to "A"
        )
        val enabled = emptySet<String>()
        val result = controller.generatePlantUml(edges, enabled)

        assertTrue(result.contains("@startuml"))
        assertTrue(result.contains("top to bottom direction"))
        assertFalse(result.contains("class A"))
        assertFalse(result.contains("class B"))
        assertFalse(result.contains("class C"))
        assertFalse(result.contains("A --> B"))
        assertFalse(result.contains("B --> C"))
        assertFalse(result.contains("C --> A"))
        assertTrue(result.contains("@enduml"))
    }

    @Test
    fun `vertex extraction should work from edges`() {
        val controller = GraphController()
        val edges = listOf(
            "A" to "B",
            "B" to "C",
            "C" to "A",
            "D" to null
        )
        val result = controller.generatePlantUml(edges, setOf("A", "B", "C", "D"))

        assertTrue(result.contains("class A"))
        assertTrue(result.contains("class B"))
        assertTrue(result.contains("class C"))
        assertTrue(result.contains("class D"))
    }

    @Test
    fun `vertex extraction should handle self-references`() {
        val controller = GraphController()
        val edges = listOf(
            "A" to "A",
            "B" to "B"
        )
        val result = controller.generatePlantUml(edges, setOf("A", "B"))

        assertTrue(result.contains("class A"))
        assertTrue(result.contains("class B"))
        assertTrue(result.contains("A --> A"))
        assertTrue(result.contains("B --> B"))
    }
}