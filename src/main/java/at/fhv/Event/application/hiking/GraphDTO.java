package at.fhv.Event.application.hiking;

import java.util.List;

public record GraphDTO(List<GraphNodeDTO> nodes, List<GraphEdgeDTO> edges) {}
