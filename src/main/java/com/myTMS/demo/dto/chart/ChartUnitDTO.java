package com.myTMS.demo.dto.chart;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChartUnitDTO {
    private String canvasId;
    private String label;
    private String type;
    private List<String> labels;
    private List<String> data;

    public ChartUnitDTO(String canvasId, String label, String type, List<String> labels, List<String> data) {
        this.canvasId = canvasId;
        this.label = label;
        this.type = type;
        this.labels = labels;
        this.data = data;
    }

    // getters, setters
}

