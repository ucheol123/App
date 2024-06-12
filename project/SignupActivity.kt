package com.example.project

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class SignupActivity : ComponentActivity() {
    private lateinit var mAuth: FirebaseAuth // FirebaseAuth 인스턴스를 담을 변수 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance() // FirebaseAuth 인스턴스 초기화

        setContentView(R.layout.activity_signup) // 회원가입 화면을 설정

        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val editTextConfirmPassword = findViewById<EditText>(R.id.editTextConfirmPassword)
        val signupButton = findViewById<Button>(R.id.signupButton)

        signupButton.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            val confirmPassword = editTextConfirmPassword.text.toString().trim()

            if (email.isEmpty()) {
                editTextEmail.error = "이메일을 입력하세요"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                editTextPassword.error = "비밀번호를 입력하세요"
                return@setOnClickListener
            }

            if (confirmPassword.isEmpty()) {
                editTextConfirmPassword.error = "비밀번호를 다시 입력하세요"
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                editTextConfirmPassword.error = "비밀번호가 일치하지 않습니다"
                return@setOnClickListener
            }

            signupButton.isEnabled = false // 비활성화

            // Firebase Authentication을 사용하여 회원가입 처리
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    signupButton.isEnabled = true // 완료 후 다시 활성화
                    if (task.isSuccessful) {
                        // 회원가입 성공 시
                        Toast.makeText(baseContext, "회원가입 성공", Toast.LENGTH_SHORT).show()
                        finish() // 현재 액티비티 종료
                    } else {
                        // 회원가입 실패 시 오류 메시지 출력
                        val errorMessage = when (task.exception) {
                            is FirebaseAuthWeakPasswordException -> "비밀번호는 최소 6자 이상이어야 합니다"
                            is FirebaseAuthInvalidCredentialsException -> "올바른 이메일 형식이 아닙니다"
                            else -> "회원가입 실패: ${task.exception?.message}"
                        }
                        Toast.makeText(baseContext, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // 화면 회전 및 재시작을 위한 상태 저장
        outState.putString("email", findViewById<EditText>(R.id.editTextEmail).text.toString())
        outState.putString("password", findViewById<EditText>(R.id.editTextPassword).text.toString())
        outState.putString("confirmPassword", findViewById<EditText>(R.id.editTextConfirmPassword).text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // 저장된 상태에서 회원가입 화면의 필드 값 복원
        findViewById<EditText>(R.id.editTextEmail).setText(savedInstanceState.getString("email", ""))
        findViewById<EditText>(R.id.editTextPassword).setText(savedInstanceState.getString("password", ""))
        findViewById<EditText>(R.id.editTextConfirmPassword).setText(savedInstanceState.getString("confirmPassword", ""))
    }
}
