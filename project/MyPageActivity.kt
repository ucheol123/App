package com.example.project

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Font

class MyPageActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth

    // TAG 상수 정의
    private val TAG = "MyPageActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // SharedPreferences 및 FirebaseAuth 초기화
        sharedPreferences = getSharedPreferences("RestaurantReservation", Context.MODE_PRIVATE)
        auth = FirebaseAuth.getInstance()

        // 사용자의 UID 가져오기
        val userId = auth.currentUser?.uid

        // Firestore에서 예약 정보 가져오기
        getReservation(userId) { reservationInfo ->
            // 화면 설정
            setContent {
                MyPageScreen(reservationInfo)
            }

            // 데이터 확인 함수 호출
            checkReservationInfo(reservationInfo)
        }
    }

    // Firestore에서 예약 정보를 가져오는 함수
    // Firestore에서 예약 정보를 가져오는 함수
    private fun getReservation(userId: String?, onResult: (ReservationInfo) -> Unit) {
        // 사용자의 UID가 없을 경우 기본값 반환
        if (userId.isNullOrEmpty()) {
            onResult(ReservationInfo(false, 0))
            return
        }

        // Firestore 인스턴스 가져오기
        val db = FirebaseFirestore.getInstance()

        // 해당 사용자의 문서 가져오기 (사용자의 UID를 문서 ID로 사용)
        val docRef = db.collection("reservations").document("UserId")
        docRef.get()
            .addOnSuccessListener { document ->
                var isReserved = false
                var personCounts = 0
                if (document != null && document.exists()) { // 문서가 존재하는 경우에만 데이터 가져오기
                    // 문서가 있는 경우 데이터 가져오기
                    val data = document.data
                    if (data != null) {
                        isReserved = data["isReserved"] as? Boolean ?: false
                        personCounts = (data["personCounts"] as? Long)?.toInt() ?: 0
                    }
                } else {
                    Log.d(TAG, "No such document")
                }

                // 예약 정보 반환
                onResult(ReservationInfo(isReserved, personCounts))

                // SharedPreferences 업데이트
                updateSharedPreferences(isReserved, personCounts)
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
                // 실패할 경우 기본값 반환
                onResult(ReservationInfo(false, 0))
            }
    }


    // SharedPreferences 업데이트 함수
    private fun updateSharedPreferences(isReserved: Boolean, personCounts: Int) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isReserved", isReserved)
        editor.putInt("personCounts", personCounts)
        editor.apply()
    }


    // 예약 정보를 확인하는 함수
    private fun checkReservationInfo(reservationInfo: ReservationInfo) {
        // 여기서 reservationInfo를 사용하여 예약 정보를 확인할 수 있습니다.
        // 예약 정보를 로그로 출력하거나 화면에 표시하는 등의 작업을 수행할 수 있습니다.
        Log.d(
            TAG,
            "Is Reserved: ${reservationInfo.isReserved}, Person Counts: ${reservationInfo.personCounts}"
        )
    }
}

data class ReservationInfo(
    val isReserved: Boolean,
    val personCounts: Int
)

@Composable
fun MyPageScreen(reservationInfo: ReservationInfo) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = "Restaurant Congestion",
            style = TextStyle(
                fontSize = 35.sp,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.title)),
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "예약 상태",
            style = TextStyle(
                fontSize = 24.sp,
                color = Color.Black
            ),
            modifier = Modifier.padding(bottom = 5.dp)
        )
        Text(
            text = if (reservationInfo.isReserved) "예약됨 (음식점 : 알촌 창원대점 / 인원: ${reservationInfo.personCounts+1}/30)" else "예약되지 않음",
            fontSize = 20.sp
        )
    }
}
