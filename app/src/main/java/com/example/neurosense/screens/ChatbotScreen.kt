
package com.example.neurosense.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.neurosense.components.BackButton
import com.example.neurosense.ChatApi
import kotlinx.coroutines.launch

data class ChatbotMessage(
    val text: String,
    val isUser: Boolean
)

// --------------------------------------------------
// QUESTION
// --------------------------------------------------

data class ChatQuestion(
    val question: String,
    val options: List<String>,
    val explanation: String
)

// --------------------------------------------------
// CHATBOT SCREEN
// --------------------------------------------------

@Composable
fun ChatbotScreen(
    navController: NavController
) {

    // --------------------------------------------------
    // QUESTIONS
    // --------------------------------------------------

    val questions = remember {

        listOf(

            ChatQuestion(
                question =
                    "How have you been feeling overall recently?",
                options = listOf(
                    "I feel good most of the time",
                    "I feel okay, but I have some concerns",
                    "I have been feeling noticeably different",
                    "I have been struggling quite a bit",
                    "It changes from day to day",
                    "Other"
                ),
                explanation =
                    "This helps us understand how you have been feeling recently."
            ),

            ChatQuestion(
                question =
                    "When did you first start noticing these changes?",
                options = listOf(
                    "Within the last few days",
                    "Within the last few weeks",
                    "A few months ago",
                    "More than six months ago",
                    "I am not sure",
                    "Other"
                ),
                explanation =
                    "Knowing when the changes started helps understand how they have developed."
            ),

            ChatQuestion(
                question =
                    "How often do you notice these changes during your day?",
                options = listOf(
                    "Almost never",
                    "Once or twice a day",
                    "Several times a day",
                    "Most of the day",
                    "It varies from day to day",
                    "Other"
                ),
                explanation =
                    "The frequency can help us understand how much it affects your daily routine."
            ),

            ChatQuestion(
                question =
                    "Is there a particular time when you notice the changes more?",
                options = listOf(
                    "Mostly in the morning",
                    "Mostly in the afternoon",
                    "Mostly in the evening",
                    "Mostly at night",
                    "It does not seem to have a particular time",
                    "It varies",
                    "Other"
                ),
                explanation =
                    "Some changes may appear differently depending on the time of day."
            ),

            ChatQuestion(
                question =
                    "Do you notice the changes more when you are doing something with your hands?",
                options = listOf(
                    "No, not really",
                    "Sometimes",
                    "Yes, during small or precise tasks",
                    "Yes, during most hand activities",
                    "I have difficulty with some specific tasks",
                    "I have not noticed",
                    "Other"
                ),
                explanation =
                    "This helps understand whether everyday hand activities are being affected."
            ),

            ChatQuestion(
                question =
                    "How are you managing your usual daily activities?",
                options = listOf(
                    "I manage everything normally",
                    "I take a little longer than usual",
                    "I sometimes need help",
                    "I frequently need help",
                    "Some activities have become difficult",
                    "I have stopped doing some activities",
                    "Other"
                ),
                explanation =
                    "Daily activities give us a better idea of how the changes are affecting you."
            ),

            ChatQuestion(
                question =
                    "How have you been feeling while walking or moving around?",
                options = listOf(
                    "I move normally",
                    "I sometimes feel less steady",
                    "I occasionally need extra support",
                    "I often feel unsteady",
                    "I avoid some activities because of it",
                    "I have difficulty with longer walks",
                    "Other"
                ),
                explanation =
                    "This helps understand how comfortable and confident you feel while moving."
            ),

            ChatQuestion(
                question =
                    "Have you noticed any changes when getting up from a chair or bed?",
                options = listOf(
                    "No changes",
                    "Sometimes it takes more effort",
                    "I need to move slowly",
                    "I occasionally need support",
                    "I frequently find it difficult",
                    "I have not noticed",
                    "Other"
                ),
                explanation =
                    "Changes during simple movements can provide useful information about your daily experience."
            ),

            ChatQuestion(
                question =
                    "How would you describe your coordination recently?",
                options = listOf(
                    "No noticeable change",
                    "Slightly different than before",
                    "I occasionally make mistakes",
                    "I find some tasks harder than before",
                    "I frequently struggle with coordination",
                    "It varies depending on the activity",
                    "Other"
                ),
                explanation =
                    "Coordination during everyday activities can help us understand changes in movement."
            ),

            ChatQuestion(
                question =
                    "Have you noticed any changes in your handwriting or ability to write?",
                options = listOf(
                    "No change",
                    "Slight change",
                    "My writing sometimes looks different",
                    "My writing has become smaller or less clear",
                    "Writing takes more effort",
                    "I rarely write, so I cannot say",
                    "Other"
                ),
                explanation =
                    "Changes in handwriting can sometimes provide useful information about movement and coordination."
            ),

            ChatQuestion(
                question =
                    "Have you noticed any changes in your voice or speaking recently?",
                options = listOf(
                    "No change",
                    "My voice sometimes sounds different",
                    "I sometimes speak more softly",
                    "I sometimes have difficulty speaking clearly",
                    "Others have noticed a change",
                    "It varies from day to day",
                    "Other"
                ),
                explanation =
                    "Changes in speaking can be useful to mention during a health consultation."
            ),

            ChatQuestion(
                question =
                    "Do you notice that stress, tiredness, or lack of sleep affects how you feel?",
                options = listOf(
                    "Not at all",
                    "A little",
                    "Sometimes",
                    "Quite noticeably",
                    "Very noticeably",
                    "I am not sure",
                    "Other"
                ),
                explanation =
                    "Things such as tiredness and stress can influence how someone feels during the day."
            ),

            ChatQuestion(
                question =
                    "How much do these changes affect your normal routine?",
                options = listOf(
                    "Not at all",
                    "Very little",
                    "A little",
                    "Moderately",
                    "Quite a lot",
                    "They significantly affect my routine",
                    "Other"
                ),
                explanation =
                    "This helps understand the practical impact on your everyday life."
            ),

            ChatQuestion(
                question =
                    "Is there anything you have stopped doing because of these changes?",
                options = listOf(
                    "No",
                    "Only small activities",
                    "Some hobbies",
                    "Some physical activities",
                    "Some household activities",
                    "Several things I used to do",
                    "Other"
                ),
                explanation =
                    "Knowing what has changed in your routine can make the conversation more useful."
            ),

            ChatQuestion(
                question =
                    "What would you most like help understanding today?",
                options = listOf(
                    "Why I may be experiencing these changes",
                    "How I can monitor my changes",
                    "Whether my daily routine may be affecting them",
                    "What information I should discuss with a professional",
                    "How to keep track of my progress",
                    "I have a different question",
                    "Other"
                ),
                explanation =
                    "This lets you guide the conversation toward what matters most to you."
            )
        )
    }

    // --------------------------------------------------
    // STATE
    // --------------------------------------------------

    var currentQuestionIndex by remember {
        mutableStateOf(0)
    }

    val messages = remember {
        mutableStateListOf<ChatbotMessage>()
    }

    var showOptions by remember {
        mutableStateOf(true)
    }

    var showOtherInput by remember {
        mutableStateOf(false)
    }

    var otherText by remember {
        mutableStateOf("")
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    val answers = remember {
        mutableStateListOf<String>()
    }

    val coroutineScope = rememberCoroutineScope()

    // --------------------------------------------------
    // FREE TEXT CHAT STATE
    // --------------------------------------------------

    var showFreeTextInput by remember {
        mutableStateOf(false)
    }

    var userQuestion by remember {
        mutableStateOf("")
    }

    // --------------------------------------------------
    // AUTO SCROLL
    // --------------------------------------------------

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {

        if (messages.isNotEmpty()) {

            listState.animateScrollToItem(
                messages.lastIndex
            )
        }
    }

    // --------------------------------------------------
    // FIRST MESSAGE
    // --------------------------------------------------

    LaunchedEffect(Unit) {

        if (messages.isEmpty()) {

            messages.add(
                ChatbotMessage(
                    text =
                        "Hello. I’m here to understand how you have been feeling. I’ll ask you a few questions, and you can choose the answer that fits you best.",
                    isUser = false
                )
            )

            messages.add(
                ChatbotMessage(
                    text =
                        questions[0].question,
                    isUser = false
                )
            )
        }
    }

    // --------------------------------------------------
    // CURRENT QUESTION
    // --------------------------------------------------

    val currentQuestion =
        questions.getOrNull(currentQuestionIndex)

    // --------------------------------------------------
    // SUBMIT QUESTIONNAIRE ANSWER
    // --------------------------------------------------

    fun submitAnswer(answer: String) {

        answers.add(answer)

        messages.add(
            ChatbotMessage(
                text = answer,
                isUser = true
            )
        )

        showOptions = false
        showOtherInput = false
        otherText = ""

        val explanation =
            currentQuestion?.explanation

        if (explanation != null) {

            messages.add(
                ChatbotMessage(
                    text = explanation,
                    isUser = false
                )
            )
        }

        if (
            currentQuestionIndex <
            questions.lastIndex
        ) {

            currentQuestionIndex++

            val nextQuestion =
                questions[currentQuestionIndex]

            messages.add(
                ChatbotMessage(
                    text = nextQuestion.question,
                    isUser = false
                )
            )

            showOptions = true

        } else {

            messages.add(
                ChatbotMessage(
                    text =
                        "Thank you for sharing that information. I’ll review your responses and provide some general guidance.",
                    isUser = false
                )
            )

            isLoading = true

            val summary = buildString {

                append(
                    "The user completed a neurological health questionnaire. "
                )

                append(
                    "Please provide a general, non-diagnostic summary based on these responses.\n\n"
                )

                questions.forEachIndexed { index, question ->

                    append("Question ${index + 1}: ")
                    append(question.question)
                    append("\n")

                    append("Answer: ")
                    append(answers[index])
                    append("\n\n")
                }

                append(
                    "Explain the overall pattern in simple language. "
                )

                append(
                    "Do not diagnose Parkinson's disease, essential tremor, "
                )

                append(
                    "or any other neurological disorder. "
                )

                append(
                    "Mention that a qualified healthcare professional should "
                )

                append(
                    "evaluate concerning or persistent symptoms."
                )
            }

            coroutineScope.launch {

                val response =
                    ChatApi.sendMessage(summary)

                messages.add(
                    ChatbotMessage(
                        text = response,
                        isUser = false
                    )
                )

                isLoading = false

                showFreeTextInput = true
            }
        }
    }

    // --------------------------------------------------
    // SEND FREE TEXT QUESTION
    // --------------------------------------------------

    fun sendUserQuestion() {

        val question =
            userQuestion.trim()

        if (
            question.isEmpty() ||
            isLoading
        ) {
            return
        }

        messages.add(
            ChatbotMessage(
                text = question,
                isUser = true
            )
        )

        userQuestion = ""

        isLoading = true

        coroutineScope.launch {

            val response =
                ChatApi.sendMessage(question)

            messages.add(
                ChatbotMessage(
                    text = response,
                    isUser = false
                )
            )

            isLoading = false
        }
    }

    // --------------------------------------------------
    // SCREEN
    // --------------------------------------------------

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .imePadding()
    ) {

        // --------------------------------------------------
        // HEADER
        // --------------------------------------------------

        Column(
            modifier =
                Modifier.padding(20.dp)
        ) {

            BackButton(
                navController =
                    navController
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    "Health Assistant",

                fontSize =
                    28.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            Text(
                text =
                    "A guided conversation about how you have been feeling."
            )
        }

        Divider()

        // --------------------------------------------------
        // CHAT
        // --------------------------------------------------

        LazyColumn(

            state =
                listState,

            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),

            contentPadding =
                PaddingValues(16.dp),

            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            items(messages) { message ->

                ChatBubble(
                    message = message
                )
            }
        }

        // --------------------------------------------------
        // LOADING
        // --------------------------------------------------

        if (isLoading) {

            Text(
                text =
                    "Health Assistant is thinking...",

                modifier =
                    Modifier.padding(16.dp),

                fontWeight =
                    FontWeight.SemiBold
            )
        }

        // --------------------------------------------------
        // OPTIONS
        // --------------------------------------------------

        if (
            showOptions &&
            currentQuestion != null &&
            !isLoading
        ) {

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
            ) {

                Text(
                    text =
                        "Choose the answer that fits you best:",

                    fontWeight =
                        FontWeight.SemiBold
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                currentQuestion.options.forEach { option ->

                    OutlinedButton(

                        onClick = {

                            if (option == "Other") {

                                showOtherInput = true
                                showOptions = false

                            } else {

                                submitAnswer(option)
                            }
                        },

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = 3.dp
                                )
                    ) {

                        Text(
                            text = option
                        )
                    }
                }
            }
        }

        // --------------------------------------------------
        // OTHER INPUT
        // --------------------------------------------------

        if (
            showOtherInput &&
            !isLoading
        ) {

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
            ) {

                Text(
                    text =
                        "Please tell me in your own words:",

                    fontWeight =
                        FontWeight.SemiBold
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                OutlinedTextField(

                    value =
                        otherText,

                    onValueChange = {
                        otherText = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    placeholder = {
                        Text(
                            "Type your answer..."
                        )
                    },

                    minLines = 2,

                    maxLines = 4
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Button(

                    onClick = {

                        if (
                            otherText
                                .trim()
                                .isNotEmpty()
                        ) {

                            submitAnswer(
                                otherText.trim()
                            )
                        }
                    },

                    enabled =
                        otherText
                            .trim()
                            .isNotEmpty(),

                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Text(
                        "Send"
                    )
                }
            }
        }

        // --------------------------------------------------
        // FREE TEXT QUESTION INPUT
        // --------------------------------------------------

        if (
            showFreeTextInput &&
            !showOptions &&
            !showOtherInput
        ) {

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 8.dp,
                            bottom = 8.dp
                        )
            ) {

                Text(
                    text =
                        "Do you have any other questions?",

                    fontWeight =
                        FontWeight.SemiBold,

                    fontSize =
                        16.sp
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    OutlinedTextField(

                        value =
                            userQuestion,

                        onValueChange = {
                            userQuestion = it
                        },

                        modifier =
                            Modifier.weight(1f),

                        placeholder = {
                            Text(
                                "Type your question..."
                            )
                        },

                        minLines = 1,

                        maxLines = 3
                    )

                    Spacer(
                        modifier =
                            Modifier.width(8.dp)
                    )

                    Button(

                        onClick = {
                            sendUserQuestion()
                        },

                        enabled =
                            userQuestion
                                .trim()
                                .isNotEmpty() &&
                                    !isLoading,

                        modifier =
                            Modifier.height(56.dp)
                    ) {

                        Text(
                            "Send"
                        )
                    }
                }
            }
        }
    }
}

// --------------------------------------------------
// CHAT BUBBLE
// --------------------------------------------------

@Composable
private fun ChatBubble(
    message: ChatbotMessage
) {

    Column(
        modifier =
            Modifier.fillMaxWidth()
    ) {

        Text(
            text =
                if (message.isUser)
                    "You"
                else
                    "Health Assistant",

            fontSize =
                13.sp,

            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            modifier =
                Modifier.height(4.dp)
        )

        Card(
            modifier =
                Modifier.fillMaxWidth(),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        if (message.isUser)
                            MaterialTheme
                                .colorScheme
                                .primaryContainer
                        else
                            MaterialTheme
                                .colorScheme
                                .secondaryContainer
                )
        ) {

            Text(
                text =
                    message.text,

                modifier =
                    Modifier.padding(14.dp),

                fontSize =
                    15.sp
            )
        }
    }
}

