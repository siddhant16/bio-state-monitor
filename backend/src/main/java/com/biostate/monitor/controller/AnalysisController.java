package com.biostate.monitor.controller;

import com.biostate.monitor.model.Analysis;
import com.biostate.monitor.model.Culture;
import com.biostate.monitor.service.AnalysisService;
import com.biostate.monitor.service.CultureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/analyses")
public class AnalysisController {

    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private CultureService cultureService;

    @GetMapping("/culture/{cultureId}")
    public ResponseEntity<?> getAnalyses(@PathVariable Long cultureId, Authentication auth) {
        String username = auth.getName();
        Optional<Culture> cultureOpt = cultureService.getCultureById(cultureId);
        if (cultureOpt.isEmpty() || !cultureOpt.get().getUser().getUsername().equals(username)) {
            return ResponseEntity.badRequest().body("Culture not found or unauthorized");
        }
        Culture culture = cultureOpt.get();
        List<Analysis> analyses = analysisService.getAnalysesByCulture(culture);
        return ResponseEntity.ok(analyses);
    }
}