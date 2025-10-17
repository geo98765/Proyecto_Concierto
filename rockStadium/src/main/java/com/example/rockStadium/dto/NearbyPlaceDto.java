package com.example.rockStadium.dto;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para un lugar individual en búsquedas cercanas de SerpApi
 * ACTUALIZADO: Más robusto para manejar diferentes respuestas de SerpAPI
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NearbyPlaceDto {
    
    private Integer position;
    
    // SerpApi puede usar "title" o "name"
    private String title;
    private String name;
    
    @SerializedName("place_id")
    private String placeId;
    
    @SerializedName("data_id")
    private String dataId;
    
    @SerializedName("data_cid")
    private String dataCid;
    
    private BigDecimal rating;
    private Integer reviews;
    private String price;
    
    // IMPORTANTE: type puede venir como String o Array
    // Usamos @SerializedName para capturar ambos "type" y "types"
    @SerializedName(value = "types", alternate = {"type"})
    private List<String> types;
    
    @SerializedName("type_id")
    private String typeId;
    
    @SerializedName("type_ids")
    private List<String> typeIds;
    
    // Dirección completa
    private String address;
    
    private String phone;
    private String website;
    private String description;
    
    @SerializedName("gps_coordinates")
    private GpsCoordinates gpsCoordinates;
    
    private List<String> amenities;
    
    private String thumbnail;
    
    @SerializedName("serpapi_thumbnail")
    private String serpapiThumbnail;
    
    @SerializedName("reviews_link")
    private String reviewsLink;
    
    @SerializedName("photos_link")
    private String photosLink;
    
    // Método helper para obtener el título (puede venir como "title" o "name")
    public String getTitle() {
        return title != null ? title : name;
    }
    
    // Método helper para obtener el primer tipo como String (si existe)
    public String getType() {
        return (types != null && !types.isEmpty()) ? types.get(0) : null;
    }
    
    // Clase interna para coordenadas GPS
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GpsCoordinates {
        private BigDecimal latitude;
        private BigDecimal longitude;
    }
}