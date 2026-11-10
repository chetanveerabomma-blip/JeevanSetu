package com.jeevansetu.preparedness;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "preparedness_guides")
public class PreparednessGuide {
    @Id
    private String id;
    private String category; // FLOOD, FIRE, EARTHQUAKE, CYCLONE, ROAD_ACCIDENT, FIRST_AID, HOME_SAFETY
    private String title;
    private String summary;
    private String detailedMarkdown;
    private String language = "en"; // en, hi, te, ta
    private String authoritativeSource; // NDMA, Red Cross, WHO
    private List<String> keyDos = new ArrayList<>();
    private List<String> keyDonts = new ArrayList<>();
    private List<String> emergencyKitItems = new ArrayList<>();

    public PreparednessGuide() {}

    public PreparednessGuide(String category, String title, String summary, String language, String authoritativeSource) {
        this.category = category;
        this.title = title;
        this.summary = summary;
        this.language = language;
        this.authoritativeSource = authoritativeSource;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getDetailedMarkdown() { return detailedMarkdown; }
    public void setDetailedMarkdown(String detailedMarkdown) { this.detailedMarkdown = detailedMarkdown; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getAuthoritativeSource() { return authoritativeSource; }
    public void setAuthoritativeSource(String authoritativeSource) { this.authoritativeSource = authoritativeSource; }

    public List<String> getKeyDos() { return keyDos; }
    public void setKeyDos(List<String> keyDos) { this.keyDos = keyDos; }

    public List<String> getKeyDonts() { return keyDonts; }
    public void setKeyDonts(List<String> keyDonts) { this.keyDonts = keyDonts; }

    public List<String> getEmergencyKitItems() { return emergencyKitItems; }
    public void setEmergencyKitItems(List<String> emergencyKitItems) { this.emergencyKitItems = emergencyKitItems; }
}
