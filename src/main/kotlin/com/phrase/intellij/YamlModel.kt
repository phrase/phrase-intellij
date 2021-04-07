package com.phrase.intellij

data class YamlModel(val phrase: YamlModelPhrase)
data class YamlModelPhrase (
    val access_token: String,
    val file_format: String,
    val project_id: String,
    val pull: YamlModelPull,
    val push: YamlModelPush
)
data class YamlModelPull (val targets: List<YamlModelTarget>)
data class YamlModelTarget (val file: String, val params: YamlModelTargetParams)
data class YamlModelTargetParams (val locale_id: String)
data class YamlModelPush (val sources: List<YamlModelSource>)
data class YamlModelSource (val file: String, val params: YamlModelSourceParams)
data class YamlModelSourceParams (val locale_id: String, val update_translations: Boolean)