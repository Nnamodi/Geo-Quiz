package com.bignerdranch.android.geoquiz

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
const val CHEAT = "cheater"
private const val SCORE = "scores"
private const val ANSWERED = "answered"
private const val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var questionTextView: TextView
    private lateinit var previousButton: ImageButton
    private lateinit var cheatButton: Button
    private lateinit var tokenTextView: TextView
    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProvider(this).get(QuizViewModel::class.java)
    }

    @SuppressLint("RestrictedApi", "SetTextI18n") // Makes code safe for older APIs.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_main)
        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex
        val score = savedInstanceState?.getInt(SCORE, 0) ?: 0
        quizViewModel.score = score
        val isCheater = savedInstanceState?.getBoolean(CHEAT, false) ?: false
        quizViewModel.isCheater = isCheater
        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        questionTextView = findViewById(R.id.question_text_view)
        previousButton = findViewById(R.id.previous_button)
        cheatButton = findViewById(R.id.cheat_button)
        tokenTextView = findViewById(R.id.token_text_view)
        trueButton.setOnClickListener {
            checkAnswer(true)
            scoreBoard(true)
        }
        falseButton.setOnClickListener {
            checkAnswer(false)
            scoreBoard(false)
        }
        nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            quizViewModel.isCheater = false
            isAnswered()
            updateQuestion()
        }
        questionTextView.setOnClickListener {
            quizViewModel.moveToNext()
            quizViewModel.isCheater = false
            isAnswered()
            updateQuestion()
        }
        previousButton.setOnClickListener {
            quizViewModel.moveToPrev()
            isAnswered()
            updateQuestion()
        }
        cheatButton.setOnClickListener {
            // Start CheatActivity
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            // Checks the Android Version first.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val options =
                    ActivityOptions.makeClipRevealAnimation(it, 0, 0, it.width, it.height)
                startActivityForResult(intent, REQUEST_CODE_CHEAT, options.toBundle())
            } else {
                startActivityForResult(intent, REQUEST_CODE_CHEAT)
            }
            quizViewModel.cheatToken--
            if (quizViewModel.cheatToken < 1) cheatButton.isEnabled = false
            val token = quizViewModel.cheatToken
            tokenTextView.text = "Cheat Token: $token"
        }
        updateQuestion()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }
    override fun onSaveInstanceState(savedInstanceState: Bundle) { // Saves current activity in memory during process death.
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
        savedInstanceState.putInt(SCORE, quizViewModel.score)
        savedInstanceState.putBoolean(CHEAT, quizViewModel.isCheater)
        savedInstanceState.putBoolean(ANSWERED, quizViewModel.questionAnswered)
        savedInstanceState.putInt(CHEAT, quizViewModel.cheatToken)
    }
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }
    @SuppressLint("SetTextI18n")
    private fun updateQuestion() {
        questionTextView.setText(quizViewModel.currentQuestionText)
        val token = quizViewModel.cheatToken
        tokenTextView.text = "Cheat Token: $token"
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer
        trueButton.isEnabled = false
        falseButton.isEnabled = false
        quizViewModel.questionBank[quizViewModel.currentIndex].answered = true
        val messageResId = when {
            quizViewModel.isCheater -> R.string.judgement_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }
        val toast = Toast.makeText(
            this,
            messageResId,
            Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP,0,200)
        toast.show()
    }

    private fun isAnswered() {
        val isQuestionAnswered = quizViewModel.questionAnswered
        trueButton.isEnabled = !isQuestionAnswered // or true
        falseButton.isEnabled = !isQuestionAnswered // or true
        if (quizViewModel.cheatToken < 1) cheatButton.isEnabled = false
    }

    private fun scoreBoard(userAnswer: Boolean) {
        val countPoint = quizViewModel.currentIndex == quizViewModel.questionBank.lastIndex
        if (userAnswer == quizViewModel.currentQuestionAnswer) {
            quizViewModel.score = (quizViewModel.score + 10)
        } else {
            quizViewModel.score
        }
        if (countPoint) {
            val toast = Toast.makeText(
                this,
                "You scored ${quizViewModel.score}%.",
                Toast.LENGTH_LONG)
            toast.setGravity(Gravity.TOP, 0, 200)
            toast.show()
        }
    }
}