package com.phrase.intellij.api

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.CapturingProcessHandler
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.phrase.intellij.PhraseApiException
import com.phrase.intellij.PhraseClientNotFoundException
import com.phrase.intellij.PhrasePrefs
import java.io.File
import java.lang.reflect.Type
import java.nio.charset.Charset
import kotlin.jvm.Throws

private const val PER_PAGE = "100"

class Api(private val project: Project, private val accessToken:String) {

    private data class ProjectCreateParameters(val Name:String, val MainFormat:String)
    private data class LocaleCreateParameters(val Name:String, val Code:String)

    enum class Command(val cmd:String){
        PUSH("push"),
        PULL("pull")
    }

    companion object{
        @Throws(PhraseClientNotFoundException::class)
        fun ensureClientExists(){
            val clientPath = PhrasePrefs.clientPath
            if(clientPath==null || !File(clientPath).exists()) throw PhraseClientNotFoundException()
        }
    }

    private val gson = Gson()

    suspend fun runWithCallback(command: Command, apiCallback: ApiCallback){
        execute(apiCallback, command.cmd)
    }

    suspend fun getProjects():List<ResponseProject> = paginate{ page ->
        val resultType: Type = object: TypeToken<List<ResponseProject>>(){}.type
        execute(resultType, "projects", "list", "--access_token", accessToken, "--per_page", PER_PAGE, "--page", page.toString())
    }

    suspend fun getLocales(projectId:String):List<ResponseLocale> = paginate { page ->
        val resultType: Type = object: TypeToken<List<ResponseLocale>>(){}.type
        execute(resultType, "locales", "list", "--access_token", accessToken, "--project_id", projectId, "--per_page", PER_PAGE, "--page", page.toString())
    }

    suspend fun createProject(projectName:String):ResponseProject{
        val json = gson.toJson(ProjectCreateParameters(Name=projectName, MainFormat="xml"))
        val resultType: Type = object: TypeToken<ResponseProject>(){}.type
        return execute(resultType, "projects", "create", "--access_token", accessToken, "--data", json)
    }

    suspend fun createLocale(projectId:String, localeCode:String):ResponseLocale{
        val json = gson.toJson(LocaleCreateParameters(Name=localeCode, Code=localeCode))
        val resultType: Type = object: TypeToken<ResponseLocale>(){}.type
        return execute(resultType, "locales", "create", "--access_token", accessToken, "--project_id", projectId, "--data", json)
    }

    private suspend fun <T> paginate(function:suspend(page:Int)->List<T>):List<T>{
        val list = ArrayList<T>()
        var page = 1
        do {
            val part = function.invoke(page++)
            list.addAll(part)
        }while (part.isNotEmpty())
        return list
    }

    //for projects&locales
    private suspend fun <T> execute(resultType: Type, vararg args:String):T = withContext(Dispatchers.IO) {
        val clientPath = PhrasePrefs.clientPath
        if(clientPath==null || !File(clientPath).exists()) throw PhraseClientNotFoundException()

        val gcl = GeneralCommandLine(clientPath).apply{
            withWorkDirectory(project.basePath)
            for(arg in args) addParameters(arg)
        }
        val processHandler = CapturingProcessHandler(gcl.createProcess(), Charset.defaultCharset(), gcl.commandLineString)
        val output = processHandler.runProcess()
        if (output.exitCode != 0) {
            throw PhraseApiException(output.stdout.trim())
        } else {
            val response = output.stdout
            return@withContext gson.fromJson<T>(response, resultType)
        }
    }

    //for push&pull
    private suspend fun execute(apiCallback: ApiCallback, vararg args:String) = withContext(Dispatchers.IO) {
        val clientPath = PhrasePrefs.clientPath
        if(clientPath==null || !File(clientPath).exists()) throw PhraseClientNotFoundException()

        val gcl = GeneralCommandLine(clientPath).apply{
            withWorkDirectory(project.basePath)
            for(arg in args) addParameters(arg)
        }
        val processHandler = CapturingProcessHandler(gcl.createProcess(), Charset.defaultCharset(), gcl.commandLineString)
        processHandler.addProcessListener(object: ProcessListener{
            override fun startNotified(event: ProcessEvent) {}
            override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
//                val channel = when(outputType.toString()){
//                    "stdout" -> MessageChannel.STDOUT
//                    "system" -> MessageChannel.SYSTEM
//                    else -> MessageChannel.OTHER
//                }
                apiCallback.onMessage(event.text)
            }
            override fun processTerminated(event: ProcessEvent) {
                apiCallback.onFinish()
            }
        })
        processHandler.runProcess()
    }
}