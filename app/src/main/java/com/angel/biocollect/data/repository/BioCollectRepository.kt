package com.angel.biocollect.data.repository

import com.angel.biocollect.data.models.User
import com.angel.biocollect.data.models.CollectionWithSpecimens
import com.angel.biocollect.data.models.Collection
import com.angel.biocollect.data.models.Specimen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class BioCollectRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    //==================== AUTH ====================

    suspend fun signUp(email: String, password: String, user: User): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("User ID is null")

            firestore.collection("users").document(userId)
                .set(user.copy(id = userId, email = email))
                .await()

            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user?.uid ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    // ==================== USER ====================

    suspend fun getUser(userId: String): Result<User> {
        return try {
            val doc = firestore.collection("users").document(userId).get().await()
            val user = doc.toObject(User::class.java) ?: throw Exception("User not found")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUser(user: User): Result<Unit> {
        return try {
            firestore.collection("users").document(user.id).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== COLLECTIONS ====================

    fun getCollections(userId: String): Flow<List<Collection>> = callbackFlow {
        val listener = firestore.collection("collections")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val collections = snapshot?.documents?.mapNotNull {
                    it.toObject(Collection::class.java)?.copy(id = it.id)
                } ?: emptyList()
                trySend(collections)
            }
        awaitClose { listener.remove() }
    }

    suspend fun createCollection(collection: Collection): Result<String> {
        return try {
            val docRef = firestore.collection("collections").add(collection).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCollection(collection: Collection): Result<Unit> {
        return try {
            firestore.collection("collections").document(collection.id)
                .set(collection).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCollection(collectionId: String): Result<Unit> {
        return try {
            // Eliminar especímenes asociados
            val specimens = firestore.collection("specimens")
                .whereEqualTo("collectionId", collectionId)
                .get().await()

            specimens.documents.forEach { it.reference.delete().await() }

            // Eliminar colección
            firestore.collection("collections").document(collectionId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== SPECIMENS ====================

    fun getSpecimens(collectionId: String): Flow<List<Specimen>> = callbackFlow {
        val listener = firestore.collection("specimens")
            .whereEqualTo("collectionId", collectionId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val specimens = snapshot?.documents?.mapNotNull {
                    it.toObject(Specimen::class.java)?.copy(id = it.id)
                } ?: emptyList()
                trySend(specimens)
            }
        awaitClose { listener.remove() }
    }

    suspend fun createSpecimen(specimen: Specimen): Result<String> {
        return try {
            val docRef = firestore.collection("specimens").add(specimen).await()

            // Actualizar contador de especímenes en la colección
            updateSpecimenCount(specimen.collectionId)

            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateSpecimen(specimen: Specimen): Result<Unit> {
        return try {
            firestore.collection("specimens").document(specimen.id)
                .set(specimen).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteSpecimen(specimen: Specimen): Result<Unit> {
        return try {
            firestore.collection("specimens").document(specimen.id).delete().await()
            updateSpecimenCount(specimen.collectionId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun updateSpecimenCount(collectionId: String) {
        try {
            val count = firestore.collection("specimens")
                .whereEqualTo("collectionId", collectionId)
                .get().await().size()

            firestore.collection("collections").document(collectionId)
                .update("especimenesCount", count).await()
        } catch (e: Exception) {
        }
    }
}