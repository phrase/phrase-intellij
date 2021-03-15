package com.phrase.intellij.ui.projectconfig

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.vfs.VirtualFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import com.phrase.intellij.*
import com.phrase.intellij.api.Api
import com.phrase.intellij.api.ResponseLocale
import com.phrase.intellij.api.ResponseProject
import com.phrase.intellij.ui.SimpleDocumentListener
import java.awt.event.ItemEvent
import javax.swing.*

class ProjectConfigDialog(private val project: Project) : DialogWrapper(project) {
    private val TOKEN_LEN = 64
    private val FALLBACK_OUTPUT_FOLDER = "./app/src/main/res/values"

    private lateinit var rootPanel: JPanel
    private lateinit var lblWarningOverwritten: JLabel
    private lateinit var textfieldAccessToken: JTextField
    private lateinit var lblTokenError: JLabel
    private lateinit var comboProjects: JComboBox<Any>
    private lateinit var comboLocaleDefault: JComboBox<Any>
    private lateinit var checkboxListFiles: JCheckboxList
    private lateinit var btnAddFiles: JButton
    private lateinit var comboDownloadFolder: JComboBox<Any>
    private lateinit var progressProjects: JLabel
    private lateinit var progressLocales: JLabel

    private val projectScanner = ProjectScanner(project)
    private val modelProjects = DefaultComboBoxModel<Any>()
    private val modelLocales = DefaultComboBoxModel<Any>()
    private val modelFolders = DefaultComboBoxModel<Any>()

    init {
        init()
        title = PhraseBundle.message("createPhraseConf")
        lblWarningOverwritten.isVisible = YamlParser.exists(project)

        textfieldAccessToken.document.addDocumentListener(SimpleDocumentListener {
            onAccessTokenChanged(getAccessToken())
        })

        comboProjects.apply {
            model = modelProjects
            addItemListener { itemEvent ->
                if (itemEvent.stateChange == ItemEvent.SELECTED) {
                    val selectedProject = modelProjects.selectedItem as? ResponseProject
                    selectedProject?.let { onSelectedProject(getAccessToken(), it) }
                }
            }
        }
        comboLocaleDefault.model = modelLocales
        comboDownloadFolder.model = modelFolders

        progressProjects.isVisible = false
        progressLocales.isVisible = false

        btnAddFiles.addActionListener { onAddFiles() }
        scanProjectFiles()
    }

    fun createUIComponents(){
        checkboxListFiles = JCheckboxList()
    }
    override fun createCenterPanel() = rootPanel
    override fun getPreferredFocusedComponent() = textfieldAccessToken

    private fun getAccessToken(): String = textfieldAccessToken.text.trim()

    private fun onAccessTokenChanged(accessToken: String) {
        setProjects(null)
        setLocales(null)
        lblTokenError.text = ""
        if(accessToken.length!=TOKEN_LEN) return
        GlobalScope.launch(Dispatchers.Swing) {
            val api = Api(project, accessToken)
            progressProjects.isVisible = true
            runCatching { api.getProjects() }.fold(
                onSuccess = {
                    progressProjects.isVisible = false
                    if(it.isEmpty()) askCreateNewProject(accessToken)
                    else setProjects(it)
                },
                onFailure = {
                    progressProjects.isVisible = false
                    if(it.message?.contains("Unauthorized")==true){
                        lblTokenError.text = PhraseBundle.message("accessTokenInvalid")
                    }else{
                        lblTokenError.text = PhraseBundle.message("errorUnknown")
                    }
                }
            )
        }
    }

    private fun askCreateNewProject(accessToken: String){
        val choice = JOptionPane.showOptionDialog(null,
            PhraseBundle.message("noProjectsFound"), PhraseBundle.message("phrase"),
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null
        )
        if (choice == JOptionPane.YES_OPTION) {
            GlobalScope.launch(Dispatchers.Swing) {
                val api = Api(project, accessToken)
                val newProjectName = project.name
                progressProjects.isVisible = true
                runCatching { api.createProject(newProjectName) }.fold(
                    onSuccess = {
                        progressProjects.isVisible = false
                        setProjects(listOf(it))
                    },
                    onFailure = {
                        progressProjects.isVisible = false
                        JOptionPane.showMessageDialog(rootPanel, PhraseBundle.message("couldNotCreateProject")+"\n"+it.message)
                    }
                )
            }
        }
    }

    private fun onSelectedProject(accessToken: String, selectedProject: ResponseProject) {
        setLocales(null)
        GlobalScope.launch(Dispatchers.Swing) {
            val api = Api(project, accessToken)
            progressLocales.isVisible = true
            runCatching { api.getLocales(selectedProject.id) }.fold(
                onSuccess = {
                    progressLocales.isVisible = false
                    if(it.isEmpty()) askCreateLocale(accessToken, selectedProject)
                    else setLocales(it)
                },
                onFailure = {
                    progressLocales.isVisible = false
                    JOptionPane.showMessageDialog(rootPanel, it.message)
                }
            )
        }
    }

    private fun askCreateLocale(accessToken: String, selectedProject: ResponseProject){
        val localesList = arrayOf("en", "de", "fr", "es", "it", "pt", "ru", "zh") //TODO enhance list of initial locale choices
        val chosenLocale = JOptionPane.showInputDialog(
            rootPanel, PhraseBundle.message("noLocalesFound"), PhraseBundle.message("phrase"),
            JOptionPane.QUESTION_MESSAGE, null, localesList, localesList[0]
        ) as String?
        if (chosenLocale != null) {
            GlobalScope.launch(Dispatchers.Swing) {
                val api = Api(project, accessToken)
                progressLocales.isVisible = true
                runCatching { api.createLocale(projectId=selectedProject.id, localeCode=chosenLocale) }.fold(
                    onSuccess = {
                        progressLocales.isVisible = false
                        setLocales(listOf(it))
                    },
                    onFailure = {
                        progressLocales.isVisible = false
                        JOptionPane.showMessageDialog(rootPanel, PhraseBundle.message("couldNotCreateLocale")+"\n"+it.message)
                    }
                )
            }
        }
    }

    private fun setProjects(projects: List<ResponseProject>?) {
        modelProjects.apply {
            removeAllElements()
            projects?.forEach { addElement(it) }
        }
        comboProjects.isEnabled = projects?.isNotEmpty() ?: false
    }

    private fun setLocales(locales: List<ResponseLocale>?) {
        modelLocales.apply {
            removeAllElements()
            locales?.forEach { addElement(it) }
        }
        comboLocaleDefault.isEnabled = locales?.isNotEmpty() ?: false
    }

    private fun scanProjectFiles(){
        GlobalScope.launch(Dispatchers.Swing) {
            //files to upload
            val files = projectScanner.findStringsXmls()
            val localFiles = projectScanner.relativizePath(files)
            checkboxListFiles.addAll(localFiles)

            //folders to download
            val folders = projectScanner.findValuesFolders()
            val localFolders = projectScanner.relativizePath(folders)
            modelFolders.apply {
                removeAllElements()
                localFolders.forEach { addElement(FolderModelItem(it)) }
                if(localFolders.isEmpty()) addElement(FolderModelItem(FALLBACK_OUTPUT_FOLDER))
            }
        }
    }

    private fun onAddFiles(){
        val fcd = object: FileChooserDescriptor(true, false, false, false, false, true){
            override fun isFileSelectable(file: VirtualFile) = file.extension == "xml"
        }
        val files = FileChooser.chooseFiles(fcd, project, null).map { it.path }
        val localFiles = projectScanner.relativizePath(files)
        checkboxListFiles.addAll(localFiles)
    }

    override fun doValidate(): ValidationInfo? {
        return when {
            getAccessToken().length!=TOKEN_LEN -> ValidationInfo(PhraseBundle.message("verifyAccessToken"), textfieldAccessToken)
            comboProjects.selectedIndex==-1 -> ValidationInfo(PhraseBundle.message("verifyProject"), comboProjects)
            comboLocaleDefault.selectedIndex==-1 -> ValidationInfo(PhraseBundle.message("verifyLocale"), comboLocaleDefault)
            !checkboxListFiles.hasCheckedPaths() -> ValidationInfo(PhraseBundle.message("verifyFilesToUpload"), checkboxListFiles)
            comboDownloadFolder.selectedIndex==-1 -> ValidationInfo(PhraseBundle.message("verifyFolder"), comboDownloadFolder)
            else -> null
        }
    }

    fun save(){
        if(doValidate()!=null) return
        val accessToken = getAccessToken()
        val chosenProject = modelProjects.selectedItem as ResponseProject
        val defaultLocale = modelLocales.selectedItem as ResponseLocale
        val otherLocales = modelLocales.getAll<ResponseLocale>() - (defaultLocale)  //TODO 0 other locales?
        val filesToPush = checkboxListFiles.getCheckedPaths()
        val outputFolder = (modelFolders.selectedItem as FolderModelItem).path

        val modelPush = YamlModelPush(ArrayList<YamlModelSource>().apply{
            addAll(filesToPush.map { YamlModelSource(it, YamlModelSourceParams(defaultLocale.id, true)) })
        })
        val modelPull = YamlModelPull(ArrayList<YamlModelTarget>().apply{
            addAll(otherLocales.map { YamlModelTarget("$outputFolder-${it.code}/strings.xml", YamlModelTargetParams(it.id)) })
        })
        val model = YamlModel(YamlModelPhrase(accessToken, "xml", chosenProject.id, modelPull, modelPush))
        YamlParser.write(project, model)
    }
}