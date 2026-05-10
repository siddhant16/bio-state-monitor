package com.biostate.monitor.service;

import com.biostate.monitor.model.Analysis;
import com.biostate.monitor.model.Culture;
import com.biostate.monitor.repository.AnalysisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalysisService {

    @Autowired
    private AnalysisRepository analysisRepository;

    public Analysis saveAnalysis(Culture culture, String result) {
        Analysis analysis = new Analysis(culture, result);
        return analysisRepository.save(analysis);
    }

    public List<Analysis> getAnalysesByCulture(Culture culture) {
        return analysisRepository.findByCultureIdOrderByCreatedAtDesc(culture.getId());
    }
}