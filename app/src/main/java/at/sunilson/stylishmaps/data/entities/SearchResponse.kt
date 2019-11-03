package at.sunilson.stylishmaps.data.entities

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    val type: String,
    val query: List<String>,
    val features: List<SearchFeature>,
    val attribution: String
)

data class SearchFeature(
    val id: String,
    val type: String,
    val text: String,
    @SerializedName("place_name")
    val placeName: String,
    @SerializedName("place_type")
    val placetype: List<String>,
    val relevance: Float,
    val properties: Map<String, String>,
    val center: List<Double>
)