package kevin.script

import kevin.module.*
import org.python.core.PyDictionary
import org.python.core.PyList
import org.python.core.PyObject

object Setting {
    @JvmStatic
    fun boolean(settingInfo: PyObject): BooleanValue {
        settingInfo as PyDictionary
        val name = settingInfo["name"] as String
        val default = settingInfo["default"] as Boolean

        return BooleanValue(name, default)
    }

    @JvmStatic
    fun integer(settingInfo: PyObject): IntegerValue {
        settingInfo as PyDictionary
        val name = settingInfo["name"] as String
        val default = (settingInfo["default"] as Number).toInt()
        val min = (settingInfo["min"] as Number).toInt()
        val max = (settingInfo["max"] as Number).toInt()

        return IntegerValue(name, default, min, max)
    }

    @JvmStatic
    fun float(settingInfo: PyObject): FloatValue {
        settingInfo as PyDictionary
        val name = settingInfo["name"] as String
        val default = (settingInfo["default"] as Number).toFloat()
        val min = (settingInfo["min"] as Number).toFloat()
        val max = (settingInfo["max"] as Number).toFloat()

        return FloatValue(name, default, min, max)
    }

    @JvmStatic
    fun text(settingInfo: PyObject): TextValue {
        settingInfo as PyDictionary
        val name = settingInfo["name"] as String
        val default = settingInfo["default"] as String

        return TextValue(name, default)
    }

    @JvmStatic
    fun block(settingInfo: PyObject): BlockValue {
        settingInfo as PyDictionary
        val name = settingInfo["name"] as String
        val default = (settingInfo["default"] as Number).toInt()

        return BlockValue(name, default)
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun list(settingInfo: PyObject): ListValue {
        settingInfo as PyDictionary
        val name = settingInfo["name"] as String
        val values = (settingInfo["values"] as PyList).toArray() as Array<String>
        val default = settingInfo["default"] as String

        return ListValue(name, values, default)
    }
}