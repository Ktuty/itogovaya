package com.example.itogovaya.model

data class Plant(
    val id: String? = null,
    val name: String? = null,
    val type: String? = null,
    val wateringFrequency: Int? = null,
    var lastWateredDate: Long? = null,
    var nextWateringDate: Long? = null
) {
    constructor() : this("", "", "", 0, 0, 0)
}