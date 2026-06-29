package com.example

import com.example.api.Content
import com.example.api.GenerateContentRequest
import com.example.api.Part
import com.example.api.RetrofitClient
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    val logFile = File("debug_output.txt")
    val writer = PrintWriter(logFile.bufferedWriter())
    writer.println("DEBUG START")
    
    val apiKey = BuildConfig.GEMINI_API_KEY
    writer.println("DEBUG: API Key length = ${apiKey.length}, value starts with = ${apiKey.take(5)}")
    
    val request = GenerateContentRequest(
        contents = listOf(Content(parts = listOf(Part(text = "Hello, write a 1-word reply.")))),
        systemInstruction = Content(parts = listOf(Part(text = "You are a helpful assistant.")))
    )
    
    try {
        val response = runBlocking {
            RetrofitClient.service.generateContent("gemini-2.5-flash", apiKey, request)
        }
        val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
        writer.println("DEBUG: Success! Response = $text")
    } catch (e: Exception) {
        writer.println("DEBUG: Failed with exception: ${e.message}")
        val sw = StringWriter()
        e.printStackTrace(PrintWriter(sw))
        writer.println(sw.toString())
    } finally {
        writer.println("DEBUG END")
        writer.close()
    }
  }
}
