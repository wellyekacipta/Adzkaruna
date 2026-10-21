package com.example.doaharianmuslimin.data.remote

import android.util.Log
import com.example.doaharianmuslimin.data.model.CommunityPrayer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * Layanan Realtime Firebase Firestore & Otentikasi Anonim (Tanpa Login)
 */
object FirebasePrayerService {
    private const val TAG = "FirebasePrayerService"
    private const val COLLECTION_PRAYERS = "prayers"

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val firestore by lazy { FirebaseFirestore.getInstance() }

    /**
     * Otentikasi Anonim: User langsung mendapat UID unik tanpa perlu input email/password
     */
    fun ensureAnonymousAuth(onSuccess: (String) -> Unit = {}) {
        if (auth.currentUser != null) {
            onSuccess(auth.currentUser!!.uid)
            return
        }
        auth.signInAnonymously()
            .addOnSuccessListener { result ->
                Log.d(TAG, "Login anonim berhasil: ${result.user?.uid}")
                result.user?.uid?.let { onSuccess(it) }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Gagal login anonim", e)
            }
    }

    /**
     * Memasang listener realtime untuk update doa otomatis detik itu juga
     */
    fun listenToPrayersRealtime(onPrayersUpdated: (List<CommunityPrayer>) -> Unit) {
        firestore.collection(COLLECTION_PRAYERS)
            .orderBy("createdAtTimestamp", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error realtime snapshot", error)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val list = mutableListOf<CommunityPrayer>()
                    for (doc in snapshot.documents) {
                        try {
                            val prayer = CommunityPrayer(
                                id = doc.id,
                                senderName = doc.getString("senderName") ?: "Hamba Allah",
                                location = doc.getString("location") ?: "Indonesia",
                                prayerText = doc.getString("prayerText") ?: "",
                                timestamp = doc.getString("timestamp") ?: "Baru saja",
                                aamiinCount = (doc.getLong("aamiinCount") ?: 1L).toInt(),
                                likeCount = (doc.getLong("likeCount") ?: 0L).toInt()
                            )
                            list.add(prayer)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parse doc ${doc.id}", e)
                        }
                    }
                    onPrayersUpdated(list)
                }
            }
    }

    /**
     * Mengirimkan doa baru ke Firestore secara realtime
     */
    fun sendPrayer(prayer: CommunityPrayer, onComplete: (Boolean) -> Unit = {}) {
        val data = hashMapOf(
            "senderName" to prayer.senderName,
            "location" to prayer.location,
            "prayerText" to prayer.prayerText,
            "timestamp" to prayer.timestamp,
            "createdAtTimestamp" to System.currentTimeMillis(),
            "aamiinCount" to prayer.aamiinCount,
            "likeCount" to prayer.likeCount,
            "creatorUid" to (auth.currentUser?.uid ?: "anonymous")
        )

        firestore.collection(COLLECTION_PRAYERS)
            .document(prayer.id)
            .set(data)
            .addOnSuccessListener {
                Log.d(TAG, "Doa berhasil dikirim ke Firebase Firestore")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Gagal kirim doa ke Firebase", e)
                onComplete(false)
            }
    }

    /**
     * Mengaminkan doa di Firestore (Atomic Increment realtime)
     */
    fun incrementAamiin(prayerId: String) {
        firestore.collection(COLLECTION_PRAYERS)
            .document(prayerId)
            .update("aamiinCount", FieldValue.increment(1))
            .addOnFailureListener { e ->
                Log.e(TAG, "Gagal update aamiin di Firebase", e)
            }
    }

    /**
     * Membatalkan aamiin (Atomic Decrement)
     */
    fun decrementAamiin(prayerId: String) {
        firestore.collection(COLLECTION_PRAYERS)
            .document(prayerId)
            .update("aamiinCount", FieldValue.increment(-1))
            .addOnFailureListener { e ->
                Log.e(TAG, "Gagal decrement aamiin di Firebase", e)
            }
    }
}
