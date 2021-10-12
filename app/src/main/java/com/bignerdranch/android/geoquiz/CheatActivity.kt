package com.bignerdranch.android.geoquiz

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider

private const val TAG = "CheatActivity"
const val EXTRA_ANSWER_SHOWN = "com.bignerdranch.android.geoquiz.answer_shown"
private const val EXTRA_ANSWER_IS_TRUE = "com.bignerdranch.android.geoquiz.answer_is_true"

class CheatActivity : AppCompatActivity() {
    private lateinit var answerTextView: TextView
    private lateinit var showAnswerButton: Button
    private lateinit var apiLevel: TextView
    private var answerIsTrue = false
    private val cheatViewModel: QuizViewModel by lazy {
        ViewModelProvider(this).get(QuizViewModel::class.java)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) is called")
        setContentView(R.layout.activity_cheat)
        val cheatStatus = savedInstanceState?.getBoolean(CHEAT, false) ?: false
        setAnswerShownResult(cheatStatus)
        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)
        answerTextView = findViewById(R.id.answer_text_view)
        showAnswerButton = findViewById(R.id.show_answer_button)
        apiLevel = findViewById(R.id.api_level)
        showAnswerButton.setOnClickListener {
            val answerText = when {
                answerIsTrue -> R.string.true_button
                else -> R.string.false_button
            }
            answerTextView.setText(answerText)
            setAnswerShownResult()
            showAnswerButton.isEnabled = false
            cheatViewModel.cheatStatus = true
        }
        val buildNumber = Build.VERSION.SDK_INT
        apiLevel.text = "API Level: $buildNumber"
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() is called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() is called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() is called")
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(CHEAT, "onSavedInstanceState called")
        savedInstanceState.putBoolean(CHEAT, cheatViewModel.cheatStatus)
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() is called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() is called")
    }

    private fun setAnswerShownResult(isAnswerShown: Boolean = true) { // Sends information to the MainActivity.
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
        }
        setResult(Activity.RESULT_OK, data)
    }

    companion object { // Intent sent to the OS(ActivityManager).
        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }
    }
}