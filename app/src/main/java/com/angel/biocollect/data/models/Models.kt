package com.angel.biocollect.data.models

import com.google.firebase.Timestamp

data class User(
    val id: String = "",
    val nombre: String = "",
    val apellidos: String = "",
    val institucion: String = "",
    val email: String = "",
    val descripcion: String = "",
    val photoUrl: String = "",
    val createdAt: Timestamp = Timestamp.now()
)

data class Collection(
    val id: String = "",
    val userId: String = "",
    val nombre: String = "",
    val categoria: String = "",
    val descripcion: String = "",
    val imageUrl: String = "",
    val especimenesCount: Int = 0,
    val createdAt: Timestamp = Timestamp.now()
)

data class Specimen(
    val id: String = "",
    val userId: String = "",
    val collectionId: String = "",
    val nombre: String = "",
    val familia: String = "",
    val especie: String = "",
    val pais: String = "",
    val imageUrl: String = "",
    val createdAt: Timestamp = Timestamp.now()
)

data class CollectionWithSpecimens(
    val collection: Collection,
    val specimens: List<Specimen> = emptyList()
)