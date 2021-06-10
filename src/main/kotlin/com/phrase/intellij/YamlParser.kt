package com.phrase.intellij

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VirtualFile
import java.io.File

object YamlParser {
    private val yamlMapper by lazy {
        ObjectMapper(YAMLFactory())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .registerKotlinModule()
    }

    private fun yamlFile(project: Project):File = File("${project.guessProjectDir()?.path}/.phrase.yml")
    fun yamlVirtualFile(project:Project): VirtualFile? = project.guessProjectDir()?.findChild(".phrase.yml")

    fun ensureExists(project: Project) {
        val file = yamlFile(project)
        if(!file.exists()) throw PhraseConfigurationNotFoundException()
    }

    fun exists(project: Project) = yamlFile(project).exists()

    fun read(project: Project):YamlModel{
        val file = yamlFile(project)
        if(!file.exists()) throw PhraseConfigurationNotFoundException()
        try{
            return yamlMapper.readValue(file, YamlModel::class.java)
        }catch (e:Throwable){
            throw PhraseLoadConfigurationException(e)
        }
    }

    fun write(project: Project, yamlModel: YamlModel){
        val file = yamlFile(project)
        try{
            yamlMapper.writeValue(file, yamlModel)
        }catch (e:Throwable){
            throw PhraseSaveConfigurationException(e)
        }
    }
}