package com.biostate.monitor;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

@Service
public class FermentationAnalyzer {

    // IMPORTANT: Set your Gemini API key as an environment variable before running the server.
    private static final String API_KEY = System.getenv().getOrDefault("GEMINI_API_KEY", "YOUR_GEMINI_API_KEY"); 
    private static final String MODEL_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-09-2025:generateContent?key=" + API_KEY;
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public FermentationAnalyzer() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public String analyzeCulture(String base64Image, String cultureType) throws Exception {
        
        String systemPrompt = String.format("""
            You are an expert microbiological analysis AI for a "Bio-State" Fermentation Monitor.
            Analyze the provided image of a %s culture.
            
            Knowledge Base (Sourdough):
            - Optimal Peak: Doming surface, highly aerated bubble structure, webbing visible on the sides if in glass.
            - Exhausted/Hungry: Flat or collapsed surface, hooch (liquid) forming on top or middle.
            - Contamination: Pink or orange streaks (Serratia marcescens), fuzzy spots (mold).
            
            Knowledge Base (Kombucha/SCOBY):
            - Optimal: Creamy, opaque pellicle forming on the surface. Brown yeast strands dangling below are normal.
            - Kahm Yeast: Dry, wrinkly, powdery, geometric surface layer. Not dangerous, but alters taste.
            - Contamination: Fuzzy, dry, distinct circular patches on top of the pellicle.
            
            Task: Provide a factual, clinical assessment based strictly on visual evidence.
            """, cultureType);

        ObjectNode rootNode = objectMapper.createObjectNode();
        
        ArrayNode contentsArray = rootNode.putArray("contents");
        ObjectNode contentNode = contentsArray.addObject();
        contentNode.put("role", "user");
        
        ArrayNode partsArray = contentNode.putArray("parts");
        partsArray.addObject().put("text", systemPrompt);
        
        ObjectNode inlineData = partsArray.addObject().putObject("inlineData");
        inlineData.put("mimeType", "image/jpeg");
        inlineData.put("data", base64Image);

        ObjectNode schemaNode = rootNode.putObject("generationConfig")
                .put("responseMimeType", "application/json")
                .putObject("responseSchema");
        schemaNode.put("type", "OBJECT");
        
        ObjectNode propertiesNode = schemaNode.putObject("properties");
        
        ObjectNode statusNode = propertiesNode.putObject("status");
        statusNode.put("type", "STRING");
        statusNode.putArray("enum").add("Optimal Peak").add("Active Fermentation").add("Exhausted/Needs Feeding").add("Kahm Yeast Present").add("Contamination Risk (Mold/Pathogen)").add("Indeterminate");
        
        propertiesNode.putObject("confidence").put("type", "NUMBER").put("description", "Confidence score between 0 and 100");
        
        ObjectNode obsNode = propertiesNode.putObject("visual_observations");
        obsNode.put("type", "ARRAY");
        obsNode.putObject("items").put("type", "STRING");
        
        propertiesNode.putObject("rag_reference").put("type", "STRING");
        propertiesNode.putObject("actionable_advice").put("type", "STRING");
        
        schemaNode.putArray("required").add("status").add("confidence").add("visual_observations").add("rag_reference").add("actionable_advice");

        String jsonPayload = objectMapper.writeValueAsString(rootNode);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(MODEL_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("API inference failed with status code: " + response.statusCode());
        }

        // Extract the JSON analysis from the nested API response
        try {
            com.fasterxml.jackson.databind.JsonNode responseJson = objectMapper.readTree(response.body());
            com.fasterxml.jackson.databind.JsonNode candidates = responseJson.get("candidates");
            
            if (candidates != null && candidates.isArray() && candidates.size() > 0) {
                com.fasterxml.jackson.databind.JsonNode content = candidates.get(0).get("content");
                if (content != null) {
                    com.fasterxml.jackson.databind.JsonNode parts = content.get("parts");
                    if (parts != null && parts.isArray() && parts.size() > 0) {
                        String analysisJson = parts.get(0).get("text").asText();
                        return analysisJson;
                    }
                }
            }
            throw new RuntimeException("Invalid or empty API response format");
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException("Failed to parse API response: " + e.getMessage());
        }
    }
}
