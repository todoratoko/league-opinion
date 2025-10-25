package com.example.demo.model.dto;

import lombok.Data;

@Data
public class PortfolioSettingsRequest {
    private Double portfolioSize;
    private Double minEdge;
    private Double minWin;
}
