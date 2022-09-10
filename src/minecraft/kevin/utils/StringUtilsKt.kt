package kevin.utils

val String.fixToChar: Array<String>
    get() {
        val l = this.length
        var i = 0
        val list = arrayListOf<String>()
        while (i < l) {
            val char = this[i]
            i++
            if (i >= l || !Character.isHighSurrogate(char)) {
                list.add(char.toString())
            } else {
                val char2 = this[i]
                if (Character.isLowSurrogate(char2)) {
                    i++
                    list.add(char.toString()+char2.toString())
                } else {
                    list.add(char.toString())
                }
            }
        }
        return list.toTypedArray()
    }