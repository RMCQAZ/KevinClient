package kevin.file

import java.io.File
import java.io.IOException

abstract class FileConfig (val file: File) {

    @Throws(IOException::class)
    protected abstract fun loadConfig()

    @Throws(IOException::class)
    protected abstract fun saveConfig()

    @Throws(IOException::class)
    fun createConfig() {
        file.createNewFile()
    }

    fun hasConfig(): Boolean {
        return file.exists()
    }

    open fun getConfigFile(): File {
        return file
    }
}