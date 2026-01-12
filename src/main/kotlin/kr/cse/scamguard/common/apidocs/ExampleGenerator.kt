package kr.cse.scamguard.common.apidocs

import io.swagger.v3.core.converter.ModelConverters
import io.swagger.v3.oas.models.media.Schema
import kr.cse.scamguard.common.model.CommonResponse
import org.springframework.stereotype.Component
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

@Component
class ExampleGenerator {

    fun getSchemaForType(type: Type): Schema<*>? {
        return try {
            ModelConverters.getInstance().readAllAsResolvedSchema(type).schema
        } catch (e: Exception) {
            null
        }
    }

    fun generateExample(type: Type, isSuccessResponse: Boolean = false): Any? {
        if (type is ParameterizedType && type.rawType == CommonResponse::class.java) {
            val responseDataType = type.actualTypeArguments[0]
            return createExampleFromType(responseDataType, mutableSetOf())
        }
        return null // 리턴 값이 CommonResponse<Nothing?> 등 void 인 경우 data 필드 null
    }

    private fun createExampleFromType(type: Type, processingTypes: MutableSet<Type>): Any? {
        if (type in processingTypes) return null
        processingTypes.add(type)

        return try {
            when {
                isListType(type) -> {
                    val itemType = getListItemType(type)
                    val itemExample = createExampleFromType(itemType, processingTypes)
                    listOfNotNull(itemExample)
                }
                else -> {
                    val schema = getSchemaForType(type)
                    createExampleFromSchema(schema, type, processingTypes)
                }
            }
        } catch (e: Exception) {
            null
        } finally {
            processingTypes.remove(type)
        }
    }

    private fun createExampleFromSchema(schema: Schema<*>?, type: Type, processingTypes: MutableSet<Type>): Any? {
        if (schema == null) return null

        if (schema.properties != null && schema.properties.isNotEmpty()) {
            return createExampleObjectFromSchema(schema, type, processingTypes)
        }

        return createPrimitiveExample(schema)
    }

    private fun createExampleObjectFromSchema(schema: Schema<*>, type: Type, processingTypes: MutableSet<Type>): Map<String, Any?> {
        val exampleMap = mutableMapOf<String, Any?>()
        val actualClass = (type as? Class<*>) ?: ((type as? ParameterizedType)?.rawType as? Class<*>)

        schema.properties?.forEach { (propertyName, propertySchema) ->
            val field = actualClass?.let { findFieldInClassHierarchy(it, propertyName) }
            val fieldType = field?.genericType ?: Any::class.java

            exampleMap[propertyName] = getExampleForField(field, propertySchema)
                ?: createExampleFromType(fieldType, processingTypes)
        }

        return exampleMap
    }

    private fun getExampleForField(field: Field?, propertySchema: Schema<*>): Any? {
        // 1. 필드의 @Schema 어노테이션에서 example 값 추출
        field?.getAnnotation(io.swagger.v3.oas.annotations.media.Schema::class.java)?.let {
            if (it.example.isNotBlank()) return convertExampleValue(it.example, propertySchema.type)
        }

        // 2. OpenAPI Schema 객체에서 example 값 추출
        propertySchema.example?.let { return it }

        return null
    }

    private fun createPrimitiveExample(schema: Schema<*>): Any? {
        return when (schema.type) {
            "string" -> when(schema.format) {
                "date-time" -> "2024-01-01T12:00:00Z"
                "email" -> "user@example.com"
                else -> "string"
            }
            "integer" -> 1
            "number" -> 1.0
            "boolean" -> true
            "array" -> emptyList<Any>()
            else -> null
        }
    }

    // Helper Methods
    private fun findFieldInClassHierarchy(clazz: Class<*>, fieldName: String): Field? {
        var currentClass: Class<*>? = clazz
        while (currentClass != null) {
            try {
                return currentClass.getDeclaredField(fieldName)
            } catch (e: NoSuchFieldException) {
                currentClass = currentClass.superclass
            }
        }
        return null
    }

    private fun convertExampleValue(example: String, type: String?): Any? {
        return when (type) {
            "integer" -> example.toLongOrNull() ?: example.toIntOrNull() ?: 1
            "number" -> example.toDoubleOrNull() ?: 1.0
            "boolean" -> example.toBooleanStrictOrNull() ?: true
            else -> example
        }
    }

    private fun isListType(type: Type): Boolean = when (type) {
        is ParameterizedType -> Collection::class.java.isAssignableFrom(type.rawType as Class<*>)
        is Class<*> -> Collection::class.java.isAssignableFrom(type)
        else -> false
    }

    private fun getListItemType(type: Type): Type =
        if (type is ParameterizedType && type.actualTypeArguments.isNotEmpty()) {
            type.actualTypeArguments[0]
        } else {
            Any::class.java
        }
}
