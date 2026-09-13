package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EditorSettings
import com.example.ui.theme.EJsonDarkBg
import com.example.ui.theme.EJsonDarkBorder
import com.example.ui.theme.EJsonRed
import com.example.ui.theme.EJsonTextMuted

@Composable
fun CodeEditor(
    textFieldValue: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    settings: EditorSettings,
    searchQuery: String = "",
    highlightedErrorLine: Int? = null,
    modifier: Modifier = Modifier
) {
    val lines = remember(textFieldValue.text) {
        val count = textFieldValue.text.count { it == '\n' } + 1
        (1..count).toList()
    }

    val visualTransformation = remember(
        settings.syntaxHighlighting,
        searchQuery,
        textFieldValue.selection.start
    ) {
        JsonVisualTransformation(
            isHighlightingEnabled = settings.syntaxHighlighting,
            searchQuery = searchQuery,
            cursorPosition = textFieldValue.selection.start
        )
    }

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    val textStyle = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = settings.fontSizeSp.sp,
        lineHeight = (settings.fontSizeSp * 1.45).sp,
        color = MaterialTheme.colorScheme.onBackground
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(EJsonDarkBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(verticalScrollState)
        ) {
            // Line numbers gutter
            if (settings.showLineNumbers) {
                val maxLineDigits = lines.size.toString().length
                val gutterWidth = (maxLineDigits * settings.fontSizeSp * 0.75 + 24).dp

                Box(
                    modifier = Modifier
                        .width(gutterWidth)
                        .fillMaxHeight()
                        .background(Color(0xFF14161A))
                        .padding(vertical = 12.dp, horizontal = 4.dp)
                ) {
                    val gutterText = buildString {
                        lines.forEachIndexed { index, lineNum ->
                            if (index > 0) append("\n")
                            append(lineNum.toString().padStart(maxLineDigits, ' '))
                        }
                    }

                    Text(
                        text = gutterText,
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = (settings.fontSizeSp * 0.95).sp,
                            lineHeight = (settings.fontSizeSp * 1.45).sp,
                            color = EJsonTextMuted,
                            textAlign = TextAlign.End
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 4.dp)
                    )
                }

                // Gutter divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(EJsonDarkBorder)
                )
            }

            // Editable Text Area
            val textModifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .testTag("json_code_input")

            val wrapModifier = if (!settings.wordWrap) {
                textModifier.horizontalScroll(horizontalScrollState)
            } else {
                textModifier
            }

            BasicTextField(
                value = textFieldValue,
                onValueChange = onValueChange,
                modifier = wrapModifier,
                textStyle = textStyle,
                visualTransformation = visualTransformation,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
            )
        }
    }
}
