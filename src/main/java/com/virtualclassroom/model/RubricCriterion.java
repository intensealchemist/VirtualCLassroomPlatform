package com.virtualclassroom.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public class RubricCriterion {
    
    @Column(name = "criterion_name")
    private String name;
    
    @Column(name = "criterion_description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "max_points")
    private BigDecimal maxPoints;
    
    @Column(name = "criterion_order")
    private Integer order;
    
    // Constructors
    public RubricCriterion() {}
    
    public RubricCriterion(String name, String description, BigDecimal maxPoints, Integer order) {
        this.name = name;
        this.description = description;
        this.maxPoints = maxPoints;
        this.order = order;
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getMaxPoints() { return maxPoints; }
    public void setMaxPoints(BigDecimal maxPoints) { this.maxPoints = maxPoints; }
    
    public Integer getOrder() { return order; }
    public void setOrder(Integer order) { this.order = order; }
}
