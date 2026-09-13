package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.TemplateLibrary
import com.example.util.JsonEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("EJson", appName)
  }

  @Test
  fun `validate valid json`() {
    val json = """{"format_version": "1.26.40", "minecraft:item": {"description": {"identifier": "custom:ruby"}}}"""
    val result = JsonEngine.validate(json)
    assertTrue(result.isValid)
  }

  @Test
  fun `validate invalid json returns error location`() {
    val invalidJson = """{"name": "test",}"""
    val result = JsonEngine.validate(invalidJson)
    assertFalse(result.isValid)
    assertNotNull(result.errorMessage)
  }

  @Test
  fun `format and minify json roundtrip`() {
    val raw = """{"a":1,"b":[true,false],"c":"text"}"""
    val formatted = JsonEngine.format(raw, 2)
    assertTrue(formatted.contains("\n"))
    val minified = JsonEngine.minify(formatted)
    assertEquals(raw, minified)
  }

  @Test
  fun `bedrock analysis identifies 1_26_40 entity`() {
    val template = TemplateLibrary.templates.first { it.id == "bp_entity" }
    val analysis = JsonEngine.analyzeBedrock(template.content)
    assertTrue(analysis.isBedrock)
    assertEquals("Entity Definition", analysis.category)
    assertEquals("custom:my_entity", analysis.identifier)
    assertTrue(analysis.componentCount > 0)
  }
}
