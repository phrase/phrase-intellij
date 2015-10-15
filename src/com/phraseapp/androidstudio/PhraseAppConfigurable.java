package com.phraseapp.androidstudio;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class PhraseAppConfigurable implements Configurable {
    private JComponent settingsUI;
    private TextFieldWithBrowseButton clientPathField;
    private JTextArea configField;
    private JTextField accessTokenField;
    private JList projectSelect;
    private JCheckBox updateTranslationsCheckbox;
    private TextFieldWithBrowseButton defaultStringsPathField;
    private JList defaultLocaleSelect;
    private ResourceListModel projects = new ResourceListModel();
    private ResourceListModel locales = new ResourceListModel();
    private String projectId = "";
    private String localeId = "";

    @Nls
    @Override
    public String getDisplayName() {
        return "PhraseApp";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        clientPathField = new TextFieldWithBrowseButton();
        final FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(true, false, false, false, false, false) {
            public boolean isFileSelectable(VirtualFile file) {
                return file.getName().startsWith("phraseapp");
            }
        };

        clientPathField.addBrowseFolderListener("Choose PhraseApp Client", "Test", null, fileChooserDescriptor);
        clientPathField.setText(TokenRepository.getInstance().getClientPath());
        JLabel clientPathLabel = new JLabel();
        clientPathLabel.setText("PhraseApp Client Path");


        final JPanel settingsUI = new JPanel(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();
        cs.fill = GridBagConstraints.HORIZONTAL;
        cs.gridx = 0;
        cs.gridy = 0;
        cs.gridwidth = 1;
        settingsUI.add(clientPathLabel, cs);
        cs.gridx = 1;
        cs.gridy = 0;
        cs.gridwidth = 2;
        settingsUI.add(clientPathField, cs);

        String config = TokenRepository.getInstance().loadPhraseAppConfig();

        if (config.startsWith("phraseapp")) {
            configField = new JTextArea();
            configField.setText(config);

            JLabel configLabel = new JLabel();
            configLabel.setText("Your .phraseapp.yml");

            cs.gridx = 0;
            cs.gridy = 1;
            cs.gridwidth = 1;
            settingsUI.add(configLabel, cs);
            cs.gridx = 1;
            cs.gridy = 1;
            cs.gridwidth = 2;
            settingsUI.add(configField, cs);
        } else {
            initializeDynamicFields();

            JScrollPane localesScrollPane = new JScrollPane(defaultLocaleSelect);
            JScrollPane projectsScrollPane = new JScrollPane(projectSelect);

            JLabel projectIdLabel = new JLabel();
            projectIdLabel.setText("PhraseApp Project");

            JLabel accessTokenLabel = new JLabel();
            accessTokenLabel.setText("PhraseApp Access Token");

            JLabel defaultLocaleLabel = new JLabel();
            defaultLocaleLabel.setText("Default locale");

            defaultStringsPathField = new TextFieldWithBrowseButton();
            final FileChooserDescriptor localeFileChooserDesc = new FileChooserDescriptor(true, false, false, false, false, false) {
                public boolean isFileSelectable(VirtualFile file) {
                    return file.getName().startsWith("strings");
                }
            };

            defaultStringsPathField.addBrowseFolderListener("Choose default locale", "Test", null, localeFileChooserDesc);
            defaultStringsPathField.setText(TokenRepository.getInstance().getDefaultStringsPath());
            JLabel defaultStringsPathLabel = new JLabel();
            defaultStringsPathLabel.setText("Default strings");

            updateTranslationsCheckbox = new JCheckBox("Update Translations");
            updateTranslationsCheckbox.setSelected(TokenRepository.getInstance().getUpdateTranslations());

            JTextArea generateConfigLabel = new JTextArea();
            generateConfigLabel.setSize(new Dimension(120, 20));
            generateConfigLabel.setEditable(false);
            generateConfigLabel.setOpaque(false);
            generateConfigLabel.setText("A .phareapp.yml will be generated with your settings. The .phraseapp.yml will be added to your projects root folder and be editable.\nSee here for more information: http://docs.phraseapp.com/developers/cli/configuration/");

            cs.gridx = 0;
            cs.gridy = 1;
            cs.gridwidth = 1;
            settingsUI.add(accessTokenLabel, cs);
            cs.gridx = 1;
            cs.gridy = 1;
            cs.gridwidth = 2;
            settingsUI.add(accessTokenField, cs);

            cs.gridx = 0;
            cs.gridy = 2;
            cs.gridwidth = 1;
            settingsUI.add(projectIdLabel, cs);
            cs.gridx = 1;
            cs.gridy = 2;
            cs.gridwidth = 2;
            settingsUI.add(projectsScrollPane, cs);

            cs.gridx = 0;
            cs.gridy = 3;
            cs.gridwidth = 1;
            settingsUI.add(defaultLocaleLabel, cs);
            cs.gridx = 1;
            cs.gridy = 3;
            cs.gridwidth = 2;
            settingsUI.add(localesScrollPane, cs);

            cs.gridx = 0;
            cs.gridy = 4;
            cs.gridwidth = 1;
            settingsUI.add(defaultStringsPathLabel, cs);
            cs.gridx = 1;
            cs.gridy = 4;
            cs.gridwidth = 2;
            settingsUI.add(defaultStringsPathField, cs);

            cs.gridx = 1;
            cs.gridy = 5;
            cs.gridwidth = 2;
            settingsUI.add(updateTranslationsCheckbox, cs);

            cs.gridx = 1;
            cs.gridy = 6;
            cs.gridwidth = 2;
            settingsUI.add(generateConfigLabel, cs);
        }

        return settingsUI;
    }

    private void initializeDynamicFields() {
        accessTokenField = new JTextField(64);
        accessTokenField.setSize(new Dimension(120, 20));
        accessTokenField.setText(TokenRepository.getInstance().getAccessToken());

        projectSelect = new JList(projects);
        projectSelect.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectSelect.setSelectedIndex(0);

        defaultLocaleSelect = new JList(locales);
        defaultLocaleSelect.setEnabled(false);
        defaultLocaleSelect.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        defaultLocaleSelect.setSelectedIndex(0);

        if (accessTokenField.getText().isEmpty()) {
            projectSelect.setEnabled(false);
        } else {
            API api = new API(accessTokenField.getText().trim());
            projects = api.getProjects();
            projectSelect.setModel(projects);
            projectSelect.setEnabled(true);
        }

        accessTokenField.getDocument().addDocumentListener(new DocumentListener() {
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
                if (accessTokenField.getText().length() == 64) {
                    updateProjectSelect();
                } else {
                    resetProjectSelect();
                }
            }
        });

        projectSelect.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {

                    if (projectSelect.getSelectedIndex() == -1) {
                        //No selection, disable fire button.
                        defaultLocaleSelect.setEnabled(false);

                    } else {
                        //Selection, enable the fire button.
                        API api = new API(accessTokenField.getText().trim());
                        PhraseResource project = projects.getModelAt(
                                projectSelect.getSelectedIndex());
                        projectId = project.getId();

                        if (!projectId.isEmpty()) {
                            locales = api.getLocales(projectId);
                            defaultLocaleSelect.setModel(locales);
                            defaultLocaleSelect.setEnabled(true);
                        }
                    }
                }
            }
        });

        defaultLocaleSelect.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                PhraseResource locale = locales.getModelAt(
                        defaultLocaleSelect.getSelectedIndex());
                localeId = locale.getId();
            }
        });
    }

    private void resetProjectSelect() {
        projects = new ResourceListModel();
        projectSelect.setModel(projects);
    }

    private void updateProjectSelect() {
        API api = new API(accessTokenField.getText().trim());
        projects = api.getProjects();

        if (projects != null) {
            projectSelect.setModel(projects);
            projectSelect.setEnabled(true);
        } else {
            projectSelect.setEnabled(false);
            JOptionPane.showMessageDialog(settingsUI, "The access_token is not valid. Please generate a APIv2 token at: https://phraseapp.com/settings/oauth_access_tokens");
        }
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        if (clientPathField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(settingsUI, "Please select the phraseapp client");
            return;
        }

        TokenRepository.getInstance().setClientPath(clientPathField.getText().trim());
        if (configField != null) {
            TokenRepository.getInstance().setConfig(configField.getText().trim());
        } else {
            if (accessTokenField.getText().isEmpty() || projectId.isEmpty() || localeId.isEmpty()) {
                JOptionPane.showMessageDialog(settingsUI, "Please make sure to enter a PhraseApp access_token, select a project and default locale.");
                return;
            }

            TokenRepository.getInstance().generateConfig(getConfigMap());
            TokenRepository.getInstance().setAccessToken(accessTokenField.getText().trim());
            TokenRepository.getInstance().setProjectId(projectId);
            TokenRepository.getInstance().setDefaultStringsPath(defaultStringsPathField.getText().trim());
            TokenRepository.getInstance().setUpdateTranslations(updateTranslationsCheckbox.isSelected());
            TokenRepository.getInstance().setDefaultLocale(localeId);
        }
    }

    @Override
    public void reset() {
        clientPathField.setText(TokenRepository.getInstance().getClientPath());
    }

    @Override
    public void disposeUIResources() {
        if (settingsUI != null) {
            settingsUI.removeAll();
            settingsUI = null;
        }
    }

    private Map<String, Object> getConfigMap() {
        Map<String, Object> base = new HashMap<String, Object>();
        Map<String, Object> root = new TreeMap<String, Object>();
        Map<String, Object> pull = new HashMap<String, Object>();
        Map<String, Object> push = new HashMap<String, Object>();
        Map<String, Object> pullFile = new HashMap<String, Object>();
        Map<String, Object> pushFile = new HashMap<String, Object>();
        Map<String, Boolean> pullParams = new HashMap<String, Boolean>();
        Map<String, String> pushParams = new HashMap<String, String>();


        if (updateTranslationsCheckbox.isSelected()) {
            pullParams.put("update_translations", true);
            pullFile.put("params", pullParams);
        }

        pushParams.put("locale_id", localeId);
        pushFile.put("params", pushParams);
        String defaultLocalePath = getPushPath();
        pushFile.put("file", defaultLocalePath);
        pullFile.put("file", getPullPath(defaultLocalePath));

        push.put("sources", new Map[]{pushFile});
        pull.put("targets", new Map[]{pullFile});

        root.put("push", push);
        root.put("pull", pull);
        root.put("project_id", projectId);
        root.put("access_token", accessTokenField.getText().trim());
        root.put("file_format", "xml");

        base.put("phraseapp", root);
        return base;
    }

    private String getPushPath() {
        DataContext dataContext = DataManager.getInstance().getDataContext();
        Project project = (Project) dataContext.getData(DataConstants.PROJECT);
        String path = defaultStringsPathField.getText().trim();
        String[] parts = path.split(project.getName());
        return "." + parts[1];
    }

    private String getPullPath(String defaultLocalePath) {
        return defaultLocalePath.replaceAll("values", "values-<locale_name>");
    }
}