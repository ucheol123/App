package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()

        setContentView(R.layout.activity_main)

        val editTextID = findViewById<EditText>(R.id.editTextID)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signupButton = findViewById<Button>(R.id.signupButton)

        signupButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, SignupActivity::class.java))
        }

        loginButton.setOnClickListener {
            val id = editTextID.text.toString()
            val password = editTextPassword.text.toString()

            if (id.isEmpty()) {
                editTextID.error = "ID를 입력하세요"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                editTextPassword.error = "Password를 입력하세요"
                return@setOnClickListener
            }

            mAuth.signInWithEmailAndPassword(id, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // 로그인 성공 시 홈 화면으로 이동
                        startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                        finish() // 현재 액티비티 종료
                    } else {
                        // 로그인 실패 시 오류 메시지 출력
                        Toast.makeText(baseContext, "로그인 실패: ${task.exception?.message}",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
