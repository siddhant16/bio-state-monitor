package com.biostate.monitor.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "analyses")
public class Analysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "culture_id", nullable = false)
    private Culture culture;

    @Column(columnDefinition = "TEXT")
    private String result; // JSON result from AI

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Constructors
    public Analysis() {}

    public Analysis(Culture culture, String result) {
        this.culture = culture;
        this.result = result;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Culture getCulture() { return culture; }
    public void setCulture(Culture culture) { this.culture = culture; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}