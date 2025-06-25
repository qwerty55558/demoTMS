package com.myTMS.demo.dto.chart;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChartDataDTO {
    private List<ChartUnitDTO> charts;

    public ChartDataDTO(List<ChartUnitDTO> charts) {
        this.charts = charts;
    }

    // getters, setters
}

