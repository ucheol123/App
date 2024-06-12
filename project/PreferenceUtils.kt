package com.example.project// ReservationUtil.kt 파일에 저장
import android.content.Context

// SharedPreferences 키 상수
private const val PREF_NAME = "RestaurantDetailPrefs"
private const val KEY_RESERVATION_STATUS = "reservation_status"

// SharedPreferences에 예약 상태 저장
fun saveReservationStatus(context: Context, isReserved: Boolean) {
    val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.putBoolean(KEY_RESERVATION_STATUS, isReserved)
    editor.apply()
}

// SharedPreferences에서 예약 상태 가져오기
fun loadReservationStatus(context: Context): Boolean {
    val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    return sharedPref.getBoolean(KEY_RESERVATION_STATUS, false) // 기본값은 false로 설정
}
