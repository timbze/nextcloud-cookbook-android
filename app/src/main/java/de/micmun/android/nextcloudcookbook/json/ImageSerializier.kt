/*
 * ImageSerializier.kt
 *
 * Copyright 2021 by MicMun
 */
package de.micmun.android.nextcloudcookbook.json

import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.*

/**
 * Serializer for image url.
 *
 * @author MicMun
 * @version 1.0, 09.07.21
 */
object ImageSerializier : JsonTransformingSerializer<String>(String.serializer()) {
   override fun transformDeserialize(element: JsonElement): JsonElement {
      return if (element is JsonObject) {
         if (element.jsonObject.containsKey("url"))
            element.jsonObject["url"] ?: JsonPrimitive("")
         else
            JsonPrimitive("")
      } else
         element
   }
}
