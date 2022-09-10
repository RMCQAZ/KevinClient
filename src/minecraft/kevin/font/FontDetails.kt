package kevin.font

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class FontDetails(val fontName: String, val fontSize: Int = -1)