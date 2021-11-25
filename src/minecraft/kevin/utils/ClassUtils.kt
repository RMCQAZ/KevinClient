package kevin.utils

import kevin.module.Value

object ClassUtils {
    @JvmStatic
    fun getValues(clazz: Class<*>, instance: Any) = clazz.declaredFields.map { valueField ->
        valueField.isAccessible = true
        valueField[instance]
    }.filterIsInstance<Value<*>>()
}