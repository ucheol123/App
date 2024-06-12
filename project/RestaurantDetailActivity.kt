package com.example.project

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import android.content.Context
import android.content.SharedPreferences
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource

class RestaurantDetailActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var restaurantId: String // restaurantId 변수 추가

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("RestaurantReservation", Context.MODE_PRIVATE)

        // 인텐트에서 식당의 문서 ID 가져오기
        restaurantId = intent.getStringExtra("restaurantId") ?: "" // restaurantId 초기화

        val sharedPreferences = getSharedPreferences("RestaurantReservation", Context.MODE_PRIVATE)
        // Firestore에서 person_counts 가져오기
        val db = FirebaseFirestore.getInstance()

        // Firestore에서 person_counts 실시간으로 가져오기
        db.collection("restaurant").document(restaurantId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val personCounts = snapshot.getLong("person_counts")?.toInt() ?: 0
                    val isReserved =
                        sharedPreferences.getBoolean("isReserved", false) // 저장된 예약 상태 불러오기
                    // setContent 함수 내에서 Composable 함수를 호출하여 UI 설정
                    setContent {
                        RestaurantDetailScreen(
                            restaurantId = restaurantId, // restaurantId 전달
                            restaurantName = intent.getStringExtra("restaurantName") ?: "",
                            restaurantAddress = intent.getStringExtra("restaurantAddress") ?: "",
                            restaurantPhoneNumber = intent.getStringExtra("restaurantPhoneNumber")
                                ?: "",
                            restaurantMenu = intent.getStringExtra("restaurantMenu")?.split("\n")
                                ?: listOf(),
                            personCounts = personCounts,
                            isReserved = isReserved,
                            onReservationButtonClick = { isReserved ->
                                // 예약하기 버튼 클릭 시 호출되는 콜백
                                if (isReserved) {
                                    // 예약하기 로직
                                    db.collection("restaurant").document(restaurantId)
                                        .update("person_counts", personCounts + 1)
                                        .addOnSuccessListener {
                                            Log.d(
                                                "RestaurantDetailActivity",
                                                "Person count increased"
                                            )
                                            // 예약 상태를 SharedPreferences에 저장
                                            sharedPreferences.edit()
                                                .putBoolean("isReserved", isReserved).apply()

                                            // PersonCounts가 증가된 후에 로그 출력
                                            Log.d(
                                                "RestaurantDetailActivity",
                                                "Shared Preferences - Is Reserved: $isReserved, Person Counts: $personCounts"
                                            )
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w(
                                                "RestaurantDetailActivity",
                                                "Error updating document",
                                                e
                                            )
                                        }
                                } else {
                                    // 예약 취소 로직
                                    db.collection("restaurant").document(restaurantId)
                                        .update("person_counts", personCounts - 1)
                                        .addOnSuccessListener {
                                            Log.d(
                                                "RestaurantDetailActivity",
                                                "Person count decreased"
                                            )
                                            // 예약 상태를 SharedPreferences에 저장
                                            sharedPreferences.edit()
                                                .putBoolean("isReserved", isReserved).apply()

                                            // PersonCounts가 감소된 후에 로그 출력
                                            Log.d(
                                                "RestaurantDetailActivity",
                                                "Shared Preferences - Is Reserved: $isReserved, Person Counts: $personCounts"
                                            )
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w(
                                                "RestaurantDetailActivity",
                                                "Error updating document",
                                                e
                                            )
                                        }
                                }

                                // 예약 상태 변경 시 setResult()를 호출하여 데이터 전달
                                setResult(RESULT_OK, Intent().apply {
                                    putExtra("isReserved", isReserved)
                                    putExtra("personCounts", personCounts)
                                })
                            },
                            sharedPreferences = sharedPreferences // SharedPreferences 전달
                        )
                    }
                } else {
                    Log.d("TAG", "Current data: null")

                }
            }

        // SharedPreferences에서 예약 상태 가져오기
        val isReserved = sharedPreferences.getBoolean("isReserved", false)
    }

    // 예약 정보를 변경하는 함수
    private fun onReservationButtonClick(
        isReserved: Boolean,
        personCounts: Int
    ) {
        val intent = Intent(this, MyPageActivity::class.java).apply {
            putExtra("restaurantId", restaurantId)
            putExtra("isReserved", isReserved)
            putExtra("personCounts", personCounts)
        }
        startActivityForResult(intent, 1001)
    }

    // Firestore에 사용자의 예약 정보를 저장하는 함수
    private fun saveReservationToFirestore(
        isReserved: Boolean,
        personCounts: Int
    ) {
        // Firestore 레퍼런스 가져오기
        val db = FirebaseFirestore.getInstance()

        // 예약 정보를 Firestore에 저장할 때 사용할 문서 ID 가져오기
        val userId = "UserId"

        // 예약 정보를 Firestore에 저장
        db.collection("reservations").document(userId).set(
            hashMapOf(
                "isReserved" to isReserved,
                "personCounts" to personCounts
            )
        )
            .addOnSuccessListener {
                Log.d("RestaurantDetailActivity", "Reservation saved to Firestore")
            }
            .addOnFailureListener { e ->
                Log.w("RestaurantDetailActivity", "Error saving reservation to Firestore", e)
            }
    }

    // startActivityForResult()에서 결과를 처리하기 위한 코드 추가
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            val isReserved = data?.getBooleanExtra("isReserved", false) ?: false
            val personCounts = data?.getIntExtra("personCounts", 0) ?: 0
            // 예약 정보 변경 후에 화면에 적용하는 로직 추가
            updateReservationInfo(isReserved, personCounts)
        }
    }

    // 화면에 예약 정보를 업데이트하는 함수
    private fun updateReservationInfo(
        isReserved: Boolean, personCounts: Int
    ) {    // 화면에 예약 정보를 업데이트하는 함수
        fun updateReservationInfo(isReserved: Boolean, personCounts: Int) {
            // 예약 정보를 업데이트하고 화면에 적용하는 로직을 여기에 추가합니다.
        }
    }

    @Composable
    fun RestaurantDetailScreen(
        restaurantId: String, // restaurantId를 추가합니다.
        restaurantName: String,
        restaurantAddress: String,
        restaurantPhoneNumber: String,
        restaurantMenu: List<String>,
        personCounts: Int, // 이 부분을 추가
        isReserved: Boolean,
        onReservationButtonClick: (Boolean) -> Unit,
        sharedPreferences: SharedPreferences // SharedPreferences 추가
    ) {
        var isReservationDialogVisible by remember { mutableStateOf(false) }

        val buttonText = if (isReserved) "예약 취소" else "예약하기"

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(modifier = Modifier.height(15.dp))
            Image(
                painter = painterResource(id = when(restaurantId) {
                    "20240421180819" -> R.drawable.alchon
                    "20240421180842" -> R.drawable.lotte
                    "20240607235529" -> R.drawable.cong
                    "20240607235553" -> R.drawable.ho
                    "20240608000205" -> R.drawable.dal
                    "20240608000229" -> R.drawable.rico
                    "20240608000252" -> R.drawable.jiyou
                    "20240608000950" -> R.drawable.gimhae
                    "20240608001015" -> R.drawable.back
                    "20240608001227" -> R.drawable.sososo
                    "20240608001614" -> R.drawable.gimbab

                    else -> R.drawable.default_image // 선택된 식당이 없을 경우에는 디폴트 이미지 출력
                }),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)

                    .height(130.dp)
            )



            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = restaurantName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "주소: $restaurantAddress",
                fontSize = 16.sp
            )
            Text(
                text = "전화번호: $restaurantPhoneNumber",
                fontSize = 16.sp
            )



            Text(
                text = "Menu",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Column(
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                restaurantMenu.forEach { menuItem ->
                    Text(
                        text = menuItem,
                        fontSize = 16.sp,

                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
            Image(
                painter = painterResource(id = when(restaurantId) {
                    "20240421180819" -> R.drawable.a1
                    "20240421180842" -> R.drawable.a2
                    "20240607235529" -> R.drawable.a3
                    "20240607235553" -> R.drawable.a4
                    "20240608000205" -> R.drawable.a5
                    "20240608000229" -> R.drawable.a6
                    "20240608000252" -> R.drawable.a7
                    "20240608000950" -> R.drawable.a8
                    "20240608001015" -> R.drawable.a9
                    "20240608001227" -> R.drawable.a10
                    "20240608001614" -> R.drawable.a11
                    else -> R.drawable.default_image // 선택된 식당이 없을 경우에는 디폴트 이미지 출력
                }),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .width(130.dp)
                    .height(130.dp)
            )

            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "현재 출입인원: $personCounts/30", // 여기에서 $personCounts를 사용합니다.
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = {
                    // 예약하기 버튼 클릭 시 다이얼로그를 표시합니다.
                    isReservationDialogVisible = true
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = if (isReserved) Color.Red else Color.Green),
                modifier = Modifier
                    .width(700.dp)
                    .height(50.dp)
            )
            {
                Text(text = buttonText, color = Color.White)
            }

            // 예약하기 버튼 클릭 시 다이얼로그를 표시하는 대신에
            // onReservationButtonClick 함수를 호출합니다.
            if (isReservationDialogVisible) {
                AlertDialog(
                    onDismissRequest = {
                        // 다이얼로그 닫을 때 예약 상태 업데이트
                        isReservationDialogVisible = false
                    },
                    title = {
                        if (isReserved) {
                            Text(text = "정말로 예약을 취소하시겠습니까?")
                        } else {
                            Text(text = "정말로 예약하시겠습니까?")
                        }
                    }, // 다이얼로그 제목
                    confirmButton = {
                        Button(
                            onClick = {
                                // 예약 로직을 호출하고 다이얼로그를 닫습니다.
                                onReservationButtonClick(!isReserved)
                                isReservationDialogVisible = false
                                // 예약 상태를 SharedPreferences에 저장
                                sharedPreferences.edit().putBoolean("isReserved", !isReserved)
                                    .apply()
                                // Firestore에 예약 정보 저장
                                saveReservationToFirestore(!isReserved, personCounts)
                            }
                        ) {
                            Text("확인")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                isReservationDialogVisible = false
                            }
                        ) {
                            Text("취소")
                        }
                    }
                )
            }
        }
    }
}