package awab.quran.ar.data

import kotlinx.serialization.Serializable

@Serializable
data class Ayah(
    val number: Int,
    val text: String
)
