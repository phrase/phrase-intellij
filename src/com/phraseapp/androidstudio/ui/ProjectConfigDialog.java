package com.phraseapp.androidstudio.ui;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.phraseapp.androidstudio.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by gfrey on 30/10/15.
 */
public class ProjectConfigDialog extends DialogWrapper {
    private Project project = null;
    private PhraseAppConfiguration configuration;
    private String clientPath;

    private APIResourceListModel projects = new APIResourceListModel();
    private APIResourceListModel locales = new APIResourceListModel();

    private JPanel rootPanel;
    private JTextField accessTokenTextField;
    private JComboBox projectsComboBox;
    private JComboBox defaultLocaleComboBox;
    private JCheckBox uploadLocalesCheckbox;

    public ProjectConfigDialog(Project project, String clientPath) {
        super(project);
        init();
        setTitle("Create PhraseApp Configuration");

        this.project = project;
        this.clientPath = clientPath;

        initializeActions();

        configuration = new PhraseAppConfiguration(project);
        accessTokenTextField.setText(configuration.getAccessToken());
    }

    @Override
    protected ValidationInfo doValidate(){
        if (getSelectedProject().isEmpty()|| getSelectedLocale().isEmpty()) {
            return new ValidationInfo("Please verify that you have entered a valida access token and selected a project and locale.", accessTokenTextField);
        }

        return null;
    }

    @Override
    protected JComponent createCenterPanel() {
        return rootPanel;
    }

    private void initializeActions() {
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
                if (getAccessToken().length() == 64) {
                    updateProjectSelect();
                } else {
                    resetLocaleSelect();
                }
            }
        });

        projectsComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    updateLocaleSelect();
                }
            }
        });

        accessTokenTextField.requestFocus();
    }

    public void writeConfigFile() {
        Map<String, Object> cmap = getConfigMap();
        configuration.generateConfig(cmap);

        if (uploadLocalesCheckbox.isSelected()) {
            final API api = new API(clientPath, getAccessToken(), project);
            ProjectLocalesUploader projectLocalesUploader = new ProjectLocalesUploader(project, getSelectedProject(), api);
            if (projectLocalesUploader.detectedMissingRemoteLocales()) {
                projectLocalesUploader.upload();
            }
        }
    }


    private void resetProjectSelect() {
        projects = new APIResourceListModel();
        projectsComboBox.setModel(projects);
        projectsComboBox.setEnabled(false);
    }

    private void updateProjectSelect() {
        API api = new API(clientPath, getAccessToken(), project);
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
                    projects = api.postProjects(project.getName());
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
    }

    private void updateLocaleSelect() {
        API api = new API(clientPath, getAccessToken(), project);

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
                    defaultLocaleComboBox.setSelectedIndex(getLocaleIndex());
                    defaultLocaleComboBox.setEnabled(true);
                }
            } else {
                JOptionPane.showMessageDialog(rootPanel, "Could not fetch locales. Please verify that you have added a valid Access Token." + locales.getErrors());
            }
        }
    }

    @NotNull
    private String getAccessToken() {
        return accessTokenTextField.getText().trim();
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

    private int getProjectIndex() {
        String projectId = configuration.getProjectId();
        if (projectId != null) {
            for (int i = 0; i < projects.getSize(); i++) {

                APIResource model = projects.getModelAt(i);

                if (model.getId().equals(projectId)) {
                    return projects.getIndexOf(model);
                }
            }
        }

        return 0;
    }

    private int getLocaleIndex() {
        String localeId = configuration.getLocaleId();
        if (localeId != null) {
            for (int i = 0; i < locales.getSize(); i++) {

                APIResource model = locales.getModelAt(i);

                if (model.getId().equals(localeId)) {
                    return locales.getIndexOf(model);
                }
            }
        }

        return 0;
    }

    private Map<String, Object> getConfigMap() {
        Map<String, Object> base = new HashMap<String, Object>();
        Map<String, Object> root = new TreeMap<String, Object>();
        Map<String, Object> pull = new HashMap<String, Object>();
        Map<String, Object> push = new HashMap<String, Object>();
        Map<String, Object> pullFile = new HashMap<String, Object>();
        Map<String, Object> pushFile = new HashMap<String, Object>();
        Map<String, Object> pushParams = new HashMap<String, Object>();

        pushParams.put("locale_id", getSelectedLocale());
        pushFile.put("params", pushParams);
        String defaultLocalePath = "./app/src/main/res/values/strings.xml";
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


    private String getPullPath(String defaultLocalePath) {
        return defaultLocalePath.replaceAll("values", "values-<locale_name>");
    }
}

