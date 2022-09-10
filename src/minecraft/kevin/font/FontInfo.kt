package kevin.font

import java.awt.Font

class FontInfo(val name: String?, val fontSize: Int) {

    constructor(font: Font) : this(font.name, font.size)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val fontInfo = other as FontInfo
        return if (fontSize != fontInfo.fontSize) false else name == fontInfo.name
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + fontSize
        return result
    }
}