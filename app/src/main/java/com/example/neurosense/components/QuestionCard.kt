package com.example.neurosense.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun QuestionCard(
    question: String,
    selectedAnswer: Boolean?,
    onAnswerSelected: (Boolean) -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = question,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row {

                Row(
                    modifier = Modifier.weight(1f)
                ) {

                    RadioButton(
                        selected = selectedAnswer == true,
                        onClick = {
                            onAnswerSelected(true)
                        }
                    )

                    Text(
                        text = "Yes",
                        modifier = Modifier.padding(top = 12.dp)
                    )

                }

                Row(
                    modifier = Modifier.weight(1f)
                ) {

                    RadioButton(
                        selected = selectedAnswer == false,
                        onClick = {
                            onAnswerSelected(false)
                        }
                    )

                    Text(
                        text = "No",
                        modifier = Modifier.padding(top = 12.dp)
                    )

                }

            }

        }

    }
}