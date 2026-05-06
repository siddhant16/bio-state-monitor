package com.biostate.monitor;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/fermentation")
@CrossOrigin(origins = "*") 
public class FermentationController {

    private final FermentationAnalyzer analyzer;

    @Autowired
    public FermentationController(FermentationAnalyzer analyzer) {
        this.analyzer = analyzer;
    }

    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("{\"message\": \"Bio-State Fermentation Monitor API is running. Use /api/fermentation/analyze to analyze cultures.\"}");
    }

    @PostMapping("/analyze")
    public ResponseEntity<String> analyze(@RequestBody AnalysisRequest request) {
        if (request.getBase64Image() == null || request.getBase64Image().isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"Missing base64Image in payload\"}");
        }
        if (request.getCultureType() == null || request.getCultureType().isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"Missing cultureType in payload\"}");
        }

        try {
            String jsonResult = analyzer.analyzeCulture(request.getBase64Image(), request.getCultureType());
            return ResponseEntity.ok(jsonResult);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}

class AnalysisRequest {
    private String base64Image;
    private String cultureType;

    public String getBase64Image() { return base64Image; }
    public void setBase64Image(String base64Image) { this.base64Image = base64Image; }
    
    public String getCultureType() { return cultureType; }
    public void setCultureType(String cultureType) { this.cultureType = cultureType; }
}
