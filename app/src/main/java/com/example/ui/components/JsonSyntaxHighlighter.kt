package com.example.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import com.example.ui.theme.SyntaxBoolean
import com.example.ui.theme.SyntaxBracket
import com.example.ui.theme.SyntaxKey
import com.example.ui.theme.SyntaxNumber
import com.example.ui.theme.SyntaxPunctuation
import com.example.ui.theme.SyntaxString
import java.util.regex.Pattern

class JsonVisualTransformation(
    private val isHighlightingEnabled: Boolean = true,
    private val searchQuery: String = "",
    private val cursorPosition: Int = -1
) : VisualTransformation {

    companion object {
        // Fast compiled patterns
        private val TOKEN_PATTERN = Pattern.compile(
            "(\"(?:\\\\.|[^\"\\\\])*\")\\s*(:)?|(-?\\b\\d+(?:\\.\\d+)?(?:[eE][+-]?\\d+)?\\b)|\\b(true|false|null)\\b|([{}[\\]])|([:,])"
        )
    }

    override fun filter(text: AnnotatedString): TransformedText {
        if (!isHighlightingEnabled && searchQuery.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val raw = text.text
        // Limit highlighting on extraordinarily huge files to keep UI 60fps
        if (raw.length > 500_000) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val builder = AnnotatedString.Builder(raw)

        if (isHighlightingEnabled) {
            val matcher = TOKEN_PATTERN.matcher(raw)
            while (matcher.find()) {
                val strLiteral = matcher.group(1)
                val colonAfter = matcher.group(2)
                val number = matcher.group(3)
                val keyword = matcher.group(4)
                val bracket = matcher.group(5)
                val punct = matcher.group(6)

                when {
                    strLiteral != null -> {
                        val start = matcher.start(1)
                        val end = matcher.end(1)
                        if (colonAfter != null) {
                            // Key
                            builder.addStyle(
                                SpanStyle(color = SyntaxKey, fontWeight = FontWeight.SemiBold),
                                start,
                                end
                            )
                        } else {
                            // String value
                            builder.addStyle(
                                SpanStyle(color = SyntaxString),
                                start,
                                end
                            )
                        }
                    }
                    number != null -> {
                        builder.addStyle(
                            SpanStyle(color = SyntaxNumber),
                            matcher.start(3),
                            matcher.end(3)
                        )
                    }
                    keyword != null -> {
                        builder.addStyle(
                            SpanStyle(color = SyntaxBoolean, fontWeight = FontWeight.Medium),
                            matcher.start(4),
                            matcher.end(4)
                        )
                    }
                    bracket != null -> {
                        builder.addStyle(
                            SpanStyle(color = SyntaxBracket, fontWeight = FontWeight.Bold),
                            matcher.start(5),
                            matcher.end(5)
                        )
                    }
                    punct != null -> {
                        builder.addStyle(
                            SpanStyle(color = SyntaxPunctuation),
                            matcher.start(6),
                            matcher.end(6)
                        )
                    }
                }
            }

            // Bracket Matching for cursor
            if (cursorPosition in 0..raw.length) {
                findMatchingBracket(raw, cursorPosition)?.let { (b1, b2) ->
                    val highlight = SpanStyle(
                        background = Color(0x66FFD13B),
                        fontWeight = FontWeight.ExtraBold,
                        textDecoration = TextDecoration.Underline
                    )
                    builder.addStyle(highlight, b1, b1 + 1)
                    builder.addStyle(highlight, b2, b2 + 1)
                }
            }
        }

        // Search query highlighting
        if (searchQuery.isNotEmpty() && searchQuery.length <= raw.length) {
            var index = raw.indexOf(searchQuery, 0, ignoreCase = true)
            val matchStyle = SpanStyle(
                background = Color(0x99F5BA13),
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            var count = 0
            while (index >= 0 && count < 200) {
                builder.addStyle(matchStyle, index, index + searchQuery.length)
                index = raw.indexOf(searchQuery, index + searchQuery.length, ignoreCase = true)
                count++
            }
        }

        return TransformedText(builder.toAnnotatedString(), OffsetMapping.Identity)
    }

    private fun findMatchingBracket(text: String, pos: Int): Pair<Int, Int>? {
        val checkIndices = listOf(pos, pos - 1).filter { it in text.indices }
        for (i in checkIndices) {
            val char = text[i]
            val pair = when (char) {
                '{' -> findForward(text, i, '{', '}')
                '[' -> findForward(text, i, '[', ']')
                '}' -> findBackward(text, i, '{', '}')
                ']' -> findBackward(text, i, '[', ']')
                else -> null
            }
            if (pair != null) return pair
        }
        return null
    }

    private fun findForward(text: String, start: Int, open: Char, close: Char): Pair<Int, Int>? {
        var depth = 0
        var inString = false
        var escaped = false
        for (i in start until text.length) {
            val c = text[i]
            if (escaped) { escaped = false; continue }
            if (c == '\\') { escaped = true; continue }
            if (c == '"') { inString = !inString; continue }
            if (inString) continue

            if (c == open) depth++
            else if (c == close) {
                depth--
                if (depth == 0) return Pair(start, i)
            }
        }
        return null
    }

    private fun findBackward(text: String, start: Int, open: Char, close: Char): Pair<Int, Int>? {
        var depth = 0
        var inString = false
        for (i in start downTo 0) {
            val c = text[i]
            if (c == '"' && (i == 0 || text[i - 1] != '\\')) {
                inString = !inString
                continue
            }
            if (inString) continue

            if (c == close) depth++
            else if (c == open) {
                depth--
                if (depth == 0) return Pair(i, start)
            }
        }
        return null
    }
}
