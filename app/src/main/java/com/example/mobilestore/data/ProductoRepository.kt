package com.nexus.mobilestore.data

import com.nexus.mobilestore.data.models.Producto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ProductoRepository {
    private val firestore = FirebaseFirestore.getInstance()

    fun getProductos(): Flow<List<Producto>> = callbackFlow {
        val subscription = firestore.collection("productos")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val productos = snapshot?.documents?.mapNotNull { it.toObject(Producto::class.java) } ?: emptyList()
                trySend(productos)
            }
        awaitClose { subscription.remove() }
    }

    fun getProductoById(id: String): Flow<Producto?> = callbackFlow {
        // En un caso real, Firestore usualmente usa el ID del documento. 
        // Si el modelo Producto no tiene el ID, lo buscamos por nombre o similar, 
        // pero lo ideal es que el documento de Firestore coincida.
        val subscription = firestore.collection("productos").document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject(Producto::class.java))
            }
        awaitClose { subscription.remove() }
    }
}
