package kevin.utils.render.shader

import kevin.utils.MinecraftInstance
import org.apache.commons.io.IOUtils
import org.lwjgl.opengl.*

abstract class Shader(fragmentShader: String) : MinecraftInstance() {
    var programId: Int = 0
    private var uniformsMap: MutableMap<String, Int>? = null
    open fun startShader() {
        GL11.glPushMatrix()
        GL20.glUseProgram(programId)
        if (uniformsMap == null) {
            uniformsMap = HashMap()
            setupUniforms()
        }
        updateUniforms()
    }

    open fun stopShader() {
        GL20.glUseProgram(0)
        GL11.glPopMatrix()
    }

    abstract fun setupUniforms()
    abstract fun updateUniforms()
    private fun createShader(shaderSource: String, shaderType: Int): Int {
        var shader = 0
        return try {
            shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType)
            if (shader == 0) return 0
            ARBShaderObjects.glShaderSourceARB(shader, shaderSource)
            ARBShaderObjects.glCompileShaderARB(shader)
            if (ARBShaderObjects.glGetObjectParameteriARB(
                    shader,
                    ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB
                ) == GL11.GL_FALSE
            ) throw RuntimeException("Error creating shader: " + getLogInfo(shader))
            shader
        } catch (e: java.lang.Exception) {
            ARBShaderObjects.glDeleteObjectARB(shader)
            throw e
        }
    }

    private fun getLogInfo(i: Int): String {
        return ARBShaderObjects.glGetInfoLogARB(
            i,
            ARBShaderObjects.glGetObjectParameteriARB(i, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB)
        )
    }

    fun setUniform(uniformName: String, location: Int) {
        uniformsMap!![uniformName] = location
    }

    fun setupUniform(uniformName: String) {
        setUniform(uniformName, GL20.glGetUniformLocation(programId, uniformName))
    }

    fun getUniform(uniformName: String): Int {
        return uniformsMap!![uniformName]!!
    }

    init {
        var vertexShaderID = 0
        var fragmentShaderID = 0
        var con = true
        try {
            val vertexStream = javaClass.getResourceAsStream("/kevin/utils/opengl/vertex.vert")
            vertexShaderID = createShader(IOUtils.toString(vertexStream), ARBVertexShader.GL_VERTEX_SHADER_ARB)
            IOUtils.closeQuietly(vertexStream)
            val fragmentStream =
                javaClass.getResourceAsStream("/kevin/utils/opengl/$fragmentShader")
            fragmentShaderID =
                createShader(IOUtils.toString(fragmentStream), ARBFragmentShader.GL_FRAGMENT_SHADER_ARB)
            IOUtils.closeQuietly(fragmentStream)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            con = false
        }
        if (con) if (vertexShaderID == 0 || fragmentShaderID == 0) con = false
        if (con) {
            programId = ARBShaderObjects.glCreateProgramObjectARB()
            if (programId == 0) con = false
            if (con){
                ARBShaderObjects.glAttachObjectARB(programId, vertexShaderID)
                ARBShaderObjects.glAttachObjectARB(programId, fragmentShaderID)
                ARBShaderObjects.glLinkProgramARB(programId)
                ARBShaderObjects.glValidateProgramARB(programId)
            }
        }
    }
}