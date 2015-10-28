package com.phraseapp.androidstudio.ui;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.phraseapp.androidstudio.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by gfrey on 22/10/15.
 */
public class MyProjectConfigurable implements SearchableConfigurable, Configurable.NoScroll {
    private String currentConfig;
    private Project project = null;

    private APIResourceListModel projects = new APIResourceListModel();
    private APIResourceListModel locales = new APIResourceListModel();

    private JPanel rootPanel;
    private TextFieldWithBrowseButton clientPathFormattedTextField;
    private JTextField accessTokenTextField;
    private JComboBox projectsComboBox;
    private JComboBox defaultLocaleComboBox;
    private JPanel configPanel;
    private JCheckBox updateTranslationsCheckBox;
    private JPanel clientPanel;
    private JTextPane infoPane;
    private JButton createConfigButton;

    public MyProjectConfigurable(Project project) {
        this.project = project;
    }


    @Override
    public void disposeUIResources() {
        if (rootPanel != null) {
            rootPanel.removeAll();
            rootPanel = null;
        }
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return "The PhraseApp plugin requires a installed PhraseApp CLI Client and a .phraseapp.yml configuration file";
    }

    @Override
    public void reset() {
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        loadExistingConfig();
        initializeActions();
        detectAndSetClientPath();
        createHypertext(infoPane, "<p>The PhraseApp plugin requires a installed <b>PhraseApp Client</b> and a <b>.phraseapp.yml</b> configuration file. <a href=http://docs.phraseapp.com/developers/android_studio>Learn more</a></p>");

        clientPathFormattedTextField.setText(PropertiesRepository.getInstance().getClientPath());
        accessTokenTextField.setText(getAccessTokenFromConfig());

        if (API.validateClient(getClientPath(), project.getBasePath())) {
            enableClientRelatedFields();
        }

        return rootPanel;
    }

    private void detectAndSetClientPath() {
        if (PropertiesRepository.getInstance().getClientPath() == null) {
            String detected = ClientDetection.findClientInstallation();
            if (detected != null) {
                PropertiesRepository.getInstance().setClientPath(detected);
                JOptionPane.showMessageDialog(rootPanel, "We found a PhraseApp client on your system: " + detected);
            }
        }
    }

    private void loadExistingConfig() {
        PhraseAppConfiguration configuration = new PhraseAppConfiguration(getProject());
        currentConfig = configuration.loadPhraseAppConfig();
    }


    private void createHypertext(JTextPane infoPane, String s) {
        infoPane.setContentType("text/html");
        HTMLDocument doc = (HTMLDocument) infoPane.getDocument();
        HTMLEditorKit editorKit = (HTMLEditorKit) infoPane.getEditorKit();
        String text = s;
        try {
            editorKit.insertHTML(doc, doc.getLength(), text, 0, 0, null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeActions() {
        final FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(true, false, false, false, false, false) {
            public boolean isFileSelectable(VirtualFile file) {
                return file.getName().startsWith("phraseapp");
            }
        };
        clientPathFormattedTextField.addBrowseFolderListener("Choose PhraseApp Client", "", null, fileChooserDescriptor);

        JTextField clientPathTextField = clientPathFormattedTextField.getTextField();
        clientPathTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                handleClientValidation();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                handleClientValidation();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
            }

            private void handleClientValidation() {
                if (API.validateClient(getClientPath(), project.getBasePath())) {
                    enableClientRelatedFields();
                } else {
                    disableCLientRelatedFields();
                }
            }
        });

        infoPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent event) {
                handleLinkClick(event);
            }
        });

        createConfigButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                PhraseAppConfiguration configuration = new PhraseAppConfiguration(getProject());
                configuration.generateConfig(getConfigMap());
                JOptionPane.showMessageDialog(rootPanel, "Successfully created new .phraseapp.yml configuration file");
            }
        });

        accessTokenTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                checkToken();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                checkToken();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
            }

            private void checkToken() {
                if (accessTokenTextField.getText().length() == 64) {
                    updateProjectSelect();
                } else {
                    resetProjectSelect();
                    resetLocaleSelect();
                }
            }
        });

        projectsComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == 1) {
                    updateLocaleSelect();
                }
            }
        });

        projectsComboBox.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                if (projects == null || projects.isEmpty()) {
                    updateProjectSelect();
                }
            }

            @Override
            public void focusLost(FocusEvent focusEvent) {

            }
        });
    }

    private void handleLinkClick(HyperlinkEvent event) {
        if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
                Desktop.getDesktop().browse(event.getURL().toURI());
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(rootPanel, "Could not locate browser, please head to " + event.getURL().toString());
            } catch (URISyntaxException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(rootPanel, "Could not locate browser, please head to " + event.getURL().toString());
            }
            ;
        }
    }

    private void disableCLientRelatedFields() {
        accessTokenTextField.setEnabled(false);
        updateTranslationsCheckBox.setEnabled(false);
        projectsComboBox.setEnabled(false);
        defaultLocaleComboBox.setEnabled(false);
    }

    private void enableClientRelatedFields() {
        accessTokenTextField.setEnabled(true);
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "PhraseApp";
    }

    @Override
    public void apply() {
        if (clientPathFormattedTextField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(rootPanel, "Please select the phraseapp client");
            return;
        }

        if (!API.validateClient(getClientPath(), project.getBasePath())) {
            JOptionPane.showMessageDialog(rootPanel, "PhraseApp Client validation failed. Please make sure it is installed correctly.");
            return;
        }

        if (getSelectedProject() == null || getSelectedLocale() == null) {
            JOptionPane.showMessageDialog(rootPanel, "Please verify that you have entered a valida access token and selected a project and locale.");
            return;
        }

        PropertiesRepository.getInstance().setClientPath(getClientPath());
        PropertiesRepository.getInstance().setAccessToken(getAccessToken());
        PropertiesRepository.getInstance().setProjectId(getSelectedProject());

        final API api = new API(getClientPath(), getAccessToken(), project);
        ProjectLocalesUploader projectLocalesUploader = new ProjectLocalesUploader(project, getSelectedProject(), api);
        if (projectLocalesUploader.detectedMissingRemoteLocales()) {
            int uploadLocalesChoice = JOptionPane.showOptionDialog(null,
                    "We found Locales in your Project that aren't in PhraseApp yet, upload them?",
                    "PhraseApp",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, null, null);
            if (uploadLocalesChoice == JOptionPane.YES_OPTION) {
                projectLocalesUploader.upload();
            }
        }
    }

    @Override
    public boolean isModified() {
        return true;
    }


    @Nullable
    @Override
    public Runnable enableSearch(String option) {
        return null;
    }

    @NotNull
    @Override
    public String getId() {
        return "phraseapp";
    }

    private boolean configExists() {
        return currentConfig.startsWith("phraseapp");
    }

    private void resetProjectSelect() {
        projects = new APIResourceListModel();
        projectsComboBox.setModel(projects);
        projectsComboBox.setEnabled(false);
        createConfigButton.setEnabled(false);
    }

    private void updateProjectSelect() {
        API api = new API(getClientPath(), getAccessToken(), project);
        projects = api.getProjects();

        if (projects.isValid()) {
            if (projects.isEmpty()) {
                int choice = JOptionPane.showOptionDialog(null,
                        "No projects found. Should we create an initial project for you?",
                        "PhraseApp",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null, null, null);
                if (choice == JOptionPane.YES_OPTION) {
                    projects = api.postProjects(getProject().getName());
                    if (!projects.isValid()) {
                        JOptionPane.showMessageDialog(rootPanel, "Could not create new project. Please verify that you have added a valid Access Token. " + projects.getErrors());
                    }
                }
            }

            if (projects.isEmpty()) {
                resetProjectSelect();
                resetLocaleSelect();
            } else {
                projectsComboBox.setModel(projects);
                projectsComboBox.setEnabled(true);
                projectsComboBox.setSelectedIndex(getProjectIndex());
                createConfigButton.setEnabled(true);
            }

        } else {
            resetLocaleSelect();
            resetProjectSelect();
            JOptionPane.showMessageDialog(rootPanel, "Could not fetch projects from PhraseApp. Please verify that you have added a valid Access Token. " + projects.getErrors());
        }
    }

    private void resetLocaleSelect() {
        locales = new APIResourceListModel();
        defaultLocaleComboBox.setModel(locales);
        defaultLocaleComboBox.setEnabled(false);
        updateTranslationsCheckBox.setEnabled(false);
    }

    private void updateLocaleSelect() {
        API api = new API(getClientPath(), getAccessToken(), project);

        if (!getSelectedProject().isEmpty()) {
            locales = api.getLocales(getSelectedProject());
            if (locales.isValid()) {
                if (locales.isEmpty()) {
                    String[] localesList = {"en", "de", "fr", "es", "it", "pt", "zh"};

                    String localeName = (String) JOptionPane.showInputDialog(rootPanel,
                            "No locales found. What is the name of the locale we should create for you?",
                            "PhraseApp",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            localesList,
                            localesList[0]);
                    locales = api.postLocales(getSelectedProject(), localeName);
                    if (!locales.isValid()) {
                        JOptionPane.showMessageDialog(rootPanel, "Could not create locale. Please verify that you have added a valid Access Token." + locales.getErrors());
                    }
                }

                if (locales.isEmpty()) {
                    resetLocaleSelect();
                } else {
                    defaultLocaleComboBox.setModel(locales);
                    defaultLocaleComboBox.setSelectedIndex(0);
                    defaultLocaleComboBox.setEnabled(true);
                    updateTranslationsCheckBox.setEnabled(true);

                }
            } else {
                JOptionPane.showMessageDialog(rootPanel, "Could not fetch locales. Please verify that you have added a valid Access Token." + locales.getErrors());
            }
        }
    }

    private Map<String, Object> getConfigMap() {
        Map<String, Object> base = new HashMap<String, Object>();
        Map<String, Object> root = new TreeMap<String, Object>();
        Map<String, Object> pull = new HashMap<String, Object>();
        Map<String, Object> push = new HashMap<String, Object>();
        Map<String, Object> pullFile = new HashMap<String, Object>();
        Map<String, Object> pushFile = new HashMap<String, Object>();
        Map<String, Object> pushParams = new HashMap<String, Object>();


        if (updateTranslationsCheckBox.isSelected()) {
            pushParams.put("update_translations", true);
        }
        pushParams.put("locale_id", getSelectedLocale());
        pushFile.put("params", pushParams);
        String defaultLocalePath = getPushPath();
        pushFile.put("file", defaultLocalePath);
        pullFile.put("file", getPullPath(defaultLocalePath));

        push.put("sources", new Map[]{pushFile});
        pull.put("targets", new Map[]{pullFile});

        root.put("push", push);
        root.put("pull", pull);
        root.put("project_id", getSelectedProject());
        root.put("access_token", getAccessToken());
        root.put("file_format", "xml");

        base.put("phraseapp", root);
        return base;
    }

    private String getPushPath() {
        DataContext dataContext = DataManager.getInstance().getDataContext();
        Project project = (Project) dataContext.getData(DataConstants.PROJECT);
        return project.getBasePath() + "/app/src/main/res/values/strings.xml";
    }

    private String getPullPath(String defaultLocalePath) {
        return defaultLocalePath.replaceAll("values", "values-<locale_name>");
    }

    @NotNull
    private String getAccessToken() {
        return accessTokenTextField.getText().trim();
    }

    @NotNull
    private String getClientPath() {
        return clientPathFormattedTextField.getText().trim();
    }

    private String getSelectedProject() {
        if (projectsComboBox.getSelectedIndex() == -1) {
            return "";
        }

        APIResource project = projects.getModelAt(
                projectsComboBox.getSelectedIndex());
        return project.getId();
    }

    private String getSelectedLocale() {
        if (defaultLocaleComboBox.getSelectedIndex() == -1) {
            return "";
        }

        APIResource locale = locales.getModelAt(
                defaultLocaleComboBox.getSelectedIndex());
        return locale.getId();
    }

    private Project getProject() {
        DataContext dataContext = DataManager.getInstance().getDataContext();
        Project project = (Project) dataContext.getData(DataConstants.PROJECT);
        return project;
    }

    private String getAccessTokenFromConfig() {
        String accessToken = null;

        if (configExists()) {
            Yaml yaml = new Yaml();
            Map configYml = (Map) yaml.load(currentConfig);
            Map root = (Map) configYml.get("phraseapp");
            if (root != null) {
                accessToken = (String) root.get("access_token");
            }
        }

        if (accessToken == null) {
            accessToken = PropertiesRepository.getInstance().getAccessToken();
        }

        return accessToken;
    }

    private int getProjectIndex() {
        String projectId = getProjectIdFromConfig();
        for (int i = 0; i < projects.getSize(); i++) {

            APIResource model = projects.getModelAt(i);
            System.out.printf("check " + model.getId() + " existing " + projectId);

            if (model.getId().equals(projectId)) {
                System.out.printf("project index " + projects.getIndexOf(model));
                return projects.getIndexOf(model);
            }
        }
        System.out.printf("no project index " + projectId);

        return 0;
    }

    private String getProjectIdFromConfig() {
        String projectId = null;
        if (configExists()) {
            Yaml yaml = new Yaml();
            Map configYml = (Map) yaml.load(currentConfig);
            Map root = (Map) configYml.get("phraseapp");
            if (root != null) {
                projectId = (String) root.get("project_id");
            }
        }

        if (projectId == null) {
            projectId = PropertiesRepository.getInstance().getProjectId();
        }

        return projectId;
    }
}


