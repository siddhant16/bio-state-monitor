package com.biostate.monitor;

import com.biostate.monitor.model.Analysis;
import com.biostate.monitor.model.Culture;
import com.biostate.monitor.service.AnalysisService;
import com.biostate.monitor.service.CultureService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import java.util.Optional;

@RestController
@RequestMapping("/api/fermentation")
public class FermentationController {

    private final FermentationAnalyzer analyzer;

    @Autowired
    private CultureService cultureService;

    @Autowired
    private AnalysisService analysisService;

    @Autowired
    public FermentationController(FermentationAnalyzer analyzer) {
        this.analyzer = analyzer;
    }

    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("{\"message\": \"Bio-State Fermentation Monitor API is running. Use /api/fermentation/analyze to analyze cultures.\"}");
    }

    @PostMapping("/analyze")
    public ResponseEntity<String> analyze(@RequestBody AnalysisRequest request, Authentication auth) {
        if (request.getBase64Image() == null || request.getBase64Image().isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"Missing base64Image in payload\"}");
        }
        if (request.getCultureType() == null || request.getCultureType().isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"Missing cultureType in payload\"}");
        }
        if (request.getCultureId() == null) {
            return ResponseEntity.badRequest().body("{\"error\": \"Missing cultureId in payload\"}");
        }

        Optional<Culture> cultureOpt = cultureService.getCultureById(request.getCultureId());
        if (cultureOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"Culture not found\"}");
        }
        Culture culture = cultureOpt.get();
        // Check if culture belongs to user
        String username = auth.getName();
        if (!culture.getUser().getUsername().equals(username)) {
            return ResponseEntity.badRequest().body("{\"error\": \"Unauthorized\"}");
        }

        try {
            String jsonResult = analyzer.analyzeCulture(request.getBase64Image(), request.getCultureType());
            Analysis analysis = analysisService.saveAnalysis(culture, jsonResult);
            return ResponseEntity.ok(jsonResult);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}

class AnalysisRequest {
    private String base64Image;
    private String cultureType;
    private Long cultureId;

    public String getBase64Image() { return base64Image; }
    public void setBase64Image(String base64Image) { this.base64Image = base64Image; }
    
    public String getCultureType() { return cultureType; }
    public void setCultureType(String cultureType) { this.cultureType = cultureType; }

    public Long getCultureId() { return cultureId; }
    public void setCultureId(Long cultureId) { this.cultureId = cultureId; }
}
