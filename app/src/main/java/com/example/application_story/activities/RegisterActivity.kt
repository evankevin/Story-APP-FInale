package com.example.application_story.activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.example.application_story.databinding.ActivityRegisterBinding
import com.example.application_story.model.api.RegisterUser
import com.example.application_story.view.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val regisviewmodel by viewModels<RegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()
        setupAction()
        setupView()

        regisviewmodel.status.observe(this) {
            if(!it) {
                Toast.makeText(this@RegisterActivity, "Register Failed", Toast.LENGTH_SHORT).show()
                AlertDialog.Builder(this@RegisterActivity).apply {
                    setTitle("Oops")
                    setMessage("Registration failed (email address may already be in use)")
                    setPositiveButton("Try Again") { _, _ ->
                    }
                    create()
                    show()
                }
            }
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener{
                val name = binding.nameEditText.text.toString()
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()

                when{
                    name.isEmpty() -> {
                        binding.nameEditText.error = "Please fill your name"
                    }
                    email.isEmpty() -> {
                        binding.emailEditText.error = "Field Please fill your email"
                    }
                    password.isEmpty() -> {
                        binding.passwordEditText.error = "Please fill your password"
                    }
                    else -> {
                        if(checkEmail(email) &&  password.length >= 6) {
                            regisviewmodel.regisUser(RegisterUser(name, email, password))
                        } else {
                            Toast.makeText(this, "Please fill it correctly", Toast.LENGTH_SHORT).show()
                        }

                        regisviewmodel.loginResult.observe(this) {
                            if(it.token != "") {
                                regisviewmodel.saveUser(it)
                            }
                            AlertDialog.Builder(this@RegisterActivity).apply {
                                setTitle("Yeah!")
                                setMessage("Your account has been registered!.")
                                setPositiveButton("Login") { _, _ ->
                                    finish()
                                }
                                create()
                                show()
                            }
                        }
                    }
                }
            }
        }





    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val nameTextView = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(500)
        val nameEditTextLayout = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val emailTextView = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val passwordTextView = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(500)


        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup
            )
            startDelay = 500
        }.start()
    }

    private fun checkEmail(email : String) : Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    companion object{
        private const val TAG = "Registeeeer Activity"
    }

}