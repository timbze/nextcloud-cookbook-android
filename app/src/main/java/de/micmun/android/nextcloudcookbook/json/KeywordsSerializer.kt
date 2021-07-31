/*
 * KeywordsSerializer.kt
 *
 * Copyright 2021 by MicMun
 */
package de.micmun.android.nextcloudcookbook.json

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.*
import java.util.stream.Collectors

/**
 * Serializer for Keywords.
 *
 * @author MicMun
 * @version 1.0, 09.07.21
 */
object KeywordsSerializer : JsonTransformingSerializer<List<String>>(ListSerializer(String.serializer())) {
   override fun transformDeserialize(element: JsonElement): JsonElement {
      return if (element is JsonArray)
         element
      else {
         val content = element.jsonPrimitive.content
         val splits = content.split(",")
         JsonArray(splits.stream()
                      .map { k -> JsonPrimitive(k.trim()) }
                      .collect(Collectors.toList()))
      }
   }
}
