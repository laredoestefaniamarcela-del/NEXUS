package com.nexus.mobilestore.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nexus.mobilestore.data.models.CarritoItem
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class CarritoRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun userId(): String = auth.currentUser?.uid ?: ""

    private fun carritoRef() = firestore.collection("carritos").document(userId())

    fun getCarrito(): Flow<List<CarritoItem>> = callbackFlow {
        val uid = userId()
        if (uid.isEmpty()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val sub = carritoRef().addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                @Suppress("UNCHECKED_CAST")
                val rawItems = snapshot.get("items") as? List<Map<String, Any>> ?: emptyList()
                val items = rawItems.map { map ->
                    CarritoItem(
                        productoId = map["productoId"] as? String ?: "",
                        nombre = map["nombre"] as? String ?: "",
                        marca = map["marca"] as? String ?: "",
                        precio = (map["precio"] as? Number)?.toDouble() ?: 0.0,
                        imagen = map["imagen"] as? String ?: "",
                        cantidad = (map["cantidad"] as? Number)?.toInt() ?: 1
                    )
                }
                trySend(items)
            } else {
                trySend(emptyList())
            }
        }
        awaitClose { sub.remove() }
    }

    fun guardarCarrito(items: List<CarritoItem>) {
        val uid = userId()
        if (uid.isEmpty()) return

        val total = items.sumOf { it.precio * it.cantidad }
        val data = mapOf(
            "usuarioId" to uid,
            "items" to items.map { item ->
                mapOf(
                    "productoId" to item.productoId,
                    "nombre" to item.nombre,
                    "marca" to item.marca,
                    "precio" to item.precio,
                    "imagen" to item.imagen,
                    "cantidad" to item.cantidad
                )
            },
            "total" to total
        )
        carritoRef().set(data)
    }
}
