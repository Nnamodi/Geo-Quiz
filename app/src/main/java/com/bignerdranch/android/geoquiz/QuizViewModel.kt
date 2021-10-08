package com.bignerdranch.android.geoquiz

import androidx.lifecycle.ViewModel

class QuizViewModel: ViewModel() {
    var currentIndex = 0
    var score = 0
    var isCheater = false
    var answerIsTrue = false
    val questionBank = listOf(
        Question(R.string.question_rivers, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_mountains, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_asia, false),
        Question(R.string.question_region, true),
        Question(R.string.question_australia, false),
        Question(R.string.question_mideast, true),
        Question(R.string.question_buildings, false),
        Question(R.string.question_oceans, true)
    )
    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer
    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId
    val questionAnswered: Boolean
        get() = questionBank[currentIndex].answered

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }
    fun moveToPrev() {
        currentIndex = (currentIndex - 1) % questionBank.size
        if (currentIndex == -1) currentIndex = questionBank.lastIndex
    }
}