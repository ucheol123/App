package com.example.project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class ActivityA : ComponentActivity() {
    private val TAG = "ActivityA"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 모든 음식점 문서 ID 가져오기
        fetchReservedRestaurantId { reservedRestaurantId ->
            if (reservedRestaurantId != null) {
                // MyPageActivity 시작
                startMyPageActivity(this, reservedRestaurantId)
            } else {
                Log.d(TAG, "No reserved restaurant found")
            }
        }
    }

    private fun fetchReservedRestaurantId(callback: (String?) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        // Firestore에서 예약된 음식점 문서 ID 가져오기
        db.collection("reserved_restaurants")
            .document("reserved_restaurant_id")
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val reservedRestaurantId = document.getString("restaurantId")
                    callback(reservedRestaurantId)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting document: ", exception)
                callback(null)
            }
    }

    private fun startMyPageActivity(context: Context, restaurantId: String) {
        Log.d(TAG, "Starting MyPageActivity with restaurantId: $restaurantId")

        val intent = Intent(context, MyPageActivity::class.java).apply {
            putExtra("restaurantId", restaurantId)
        }
        context.startActivity(intent)
    }
}