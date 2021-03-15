package com.phrase.intellij

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.roots.ContentIterator
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream

class ProjectScanner(private val project: Project) {
    private val factory by lazy { XmlPullParserFactory.newInstance() }

    fun findValuesFolders(): Collection<String>{
        val folders = ArrayList<String>()
        val filter = VirtualFileFilter{
            it.isValid && it.isDirectory && it.parent?.name=="res" && it.name=="values"
        }
        val iterator = ContentIterator {
            folders += it.path
            true
        }
        ProjectFileIndex.SERVICE.getInstance(project).iterateContent(iterator, filter)
        return folders
    }

    suspend fun findStringsXmls(): Collection<String> = withContext(Dispatchers.IO){
        val xmls = ArrayList<String>()
        val filter = VirtualFileFilter{
            it.isValid && it.extension == "xml" && it.parent?.parent?.name=="res" && it.parent?.name=="values"
        }
        val iterator = ContentIterator {
            try {
                if (xmlHasStrings(it.inputStream)) xmls += it.path
            }catch(ignored:Exception){}
            true
        }
        ProjectFileIndex.SERVICE.getInstance(project).iterateContent(iterator, filter)
        return@withContext xmls
    }

    private fun xmlHasStrings(inputStream: InputStream):Boolean{
        inputStream.use { stream ->
            factory.newPullParser().apply {
                setInput(stream, null)
                next()
                if(eventType!= XmlPullParser.START_TAG || name!="resources") return false
                while (nextTag() != XmlPullParser.END_TAG) {
                    when(name) {
                        "string" -> return true
                        "string-array" -> return true
                        "plurals" -> return true
                        else -> skip(this)
                    }
                }
            }
        }
        return false
    }

    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) throw IllegalStateException()
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }


    fun relativizePath(paths:Collection<String>):Collection<String>{
        val retval = ArrayList<String>()
        val projectPath = project.guessProjectDir()?.path
        if(projectPath!=null) {
            for(path in paths){
                if(path.startsWith(projectPath)) retval.add("." + path.substring(projectPath.length))
                else retval.add(path)
            }
        }
        return retval
    }
}