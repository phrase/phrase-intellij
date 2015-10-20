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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class PhraseAppConfigurable implements Configurable {
    private JComponent settingsUI;
    private TextFieldWithBrowseButton clientPathField;
    private JTextField accessTokenField;
    private JList projectSelect;
    private JCheckBox updateTranslationsCheckbox;
    private TextFieldWithBrowseButton defaultStringsPathField;
    private JList defaultLocaleSelect;
    private APIResourceListModel projects = new APIResourceListModel();
    private APIResourceListModel locales = new APIResourceListModel();
    private String projectId = "";
    private String localeId = "";
    private Boolean generateConfig = false;

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

        if(TokenRepository.getInstance().getClientPath() == null){
            String detected = ClientDetection.findClientInstallation();
            if (detected != null){
                TokenRepository.getInstance().setClientPath(detected);
                JOptionPane.showMessageDialog(settingsUI, "We found a PhraseApp client on your system: " + detected);
            }
        }

        JPanel rootPanel = new JPanel(new GridBagLayout());
        final JPanel topPanel = getTopPanel();
        final JPanel settingsUI = getSettingsUI();

        GridBagConstraints cs = new GridBagConstraints();
        cs.fill = GridBagConstraints.HORIZONTAL;
        cs.anchor = GridBagConstraints.NORTHWEST;
        cs.insets = new Insets(0, 0, 20, 0);
        cs.weightx = 0.5;
        cs.gridx = 0;
        cs.gridy = 0;
        cs.gridwidth = 3;
        rootPanel.add(topPanel, cs);
        cs.insets = new Insets(0, 0, 10, 0);
        cs.weighty = 0.5;
        cs.gridx = 0;
        cs.gridy = 1;
        rootPanel.add(settingsUI,cs);

        return rootPanel;
    }

    private JPanel getTopPanel() {
        final JPanel infoPanel = new JPanel(new GridLayout(0,1));
        JEditorPane infoText = createtHyperTextPane("<p>The PhraseApp plugin requires a installed <b>PhraseApp Client</b> and a <b>.phraseapp.yml</b> configuration file. <a href=http://docs.phraseapp.com/developers/android_studio>Learn more</a></p>");
        infoPanel.add(infoText);
        return infoPanel;
    }

    @NotNull
    private JPanel getSettingsUI() {
        final JPanel settingsUI = new JPanel(new GridBagLayout());

        clientPathField = new TextFieldWithBrowseButton();
        final FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(true, false, false, false, false, false) {
            public boolean isFileSelectable(VirtualFile file) {
                return file.getName().startsWith("phraseapp");
            }
        };
        clientPathField.addBrowseFolderListener("Choose PhraseApp Client", "", null, fileChooserDescriptor);
        clientPathField.setText(TokenRepository.getInstance().getClientPath());

        JLabel clientPathLabel = new JLabel();
        clientPathLabel.setText("PhraseApp Client Path");

        GridBagConstraints cs = new GridBagConstraints();

        cs.fill = GridBagConstraints.HORIZONTAL;
        cs.anchor = GridBagConstraints.PAGE_START;
        cs.insets = new Insets(0, 0, 10, 0);
        cs.weightx = 0;
        cs.gridx = 0;
        cs.gridy = 0;
        cs.gridwidth = 1;
        settingsUI.add(clientPathLabel, cs);
        cs.weightx = 0.5;
        cs.gridx = 1;
        cs.gridy = 0;
        cs.gridwidth = 2;
        settingsUI.add(clientPathField, cs);

        String config = TokenRepository.getInstance().loadPhraseAppConfig();

        if (config.startsWith("phraseapp")) {

            JTextArea configLabel = new JTextArea();
            configLabel.setLineWrap(true);
            configLabel.setEditable(false);
            configLabel.setOpaque(false);
            configLabel.setText("A .phraseap.yml configuration file has been found in your project. Please verify that it matches your projects requirenments.");
            cs.weightx = 0.5;
            cs.gridx = 0;
            cs.gridy = 1;
            cs.gridwidth = 3;
            settingsUI.add(configLabel, cs);

        } else {
            generateConfig = true;
            initializeDynamicFields();

            JScrollPane localesScrollPane = new JScrollPane(defaultLocaleSelect);
            JScrollPane projectsScrollPane = new JScrollPane(projectSelect);

            JLabel projectIdLabel = new JLabel();
            projectIdLabel.setText("PhraseApp Project");

            JLabel accessTokenLabel = new JLabel();
            accessTokenLabel.setText("PhraseApp Access Token");

            JEditorPane accessTokenHint = createtHyperTextPane("Please generate a <a href=https://phraseapp.com/settings/oauth_access_tokens>PhraseApp API Access Token</a>");

            JLabel defaultLocaleLabel = new JLabel();
            defaultLocaleLabel.setText("Default locale");

            JTextArea generateConfigLabel = createTextPane("A .phraseapp.yml will be generated with the provided settings. The .phraseapp.yml will be added to your projects root folder.");

            defaultStringsPathField = new TextFieldWithBrowseButton();
            final FileChooserDescriptor localeFileChooserDesc = new FileChooserDescriptor(true, false, false, false, false, false) {
                public boolean isFileSelectable(VirtualFile file) {
                    return file.getName().startsWith("strings");
                }
            };

            defaultStringsPathField.addBrowseFolderListener("Choose default locale", "", null, localeFileChooserDesc);
            defaultStringsPathField.setText(TokenRepository.getInstance().getDefaultStringsPath());
            JLabel defaultStringsPathLabel = new JLabel();
            defaultStringsPathLabel.setText("Default strings");

            updateTranslationsCheckbox = new JCheckBox("Update Translations");
            updateTranslationsCheckbox.setSelected(TokenRepository.getInstance().getUpdateTranslations());

            cs.weightx = 0;
            cs.gridx = 0;
            cs.gridy = 1;
            cs.gridwidth = 1;
            settingsUI.add(accessTokenLabel, cs);
            cs.weightx = 0.5;
            cs.gridx = 1;
            cs.gridy = 1;
            cs.gridwidth = 2;
            settingsUI.add(accessTokenField, cs);

            cs.weightx = 0.5;
            cs.gridx = 1;
            cs.gridy = 2;
            cs.gridwidth = 2;
            settingsUI.add(accessTokenHint, cs);

            cs.weightx = 0;
            cs.gridx = 0;
            cs.gridy = 3;
            cs.gridwidth = 1;
            settingsUI.add(projectIdLabel, cs);
            cs.weightx = 0.5;
            cs.gridx = 1;
            cs.gridy = 3;
            cs.gridwidth = 2;
            settingsUI.add(projectsScrollPane, cs);

            cs.weightx = 0;
            cs.gridx = 0;
            cs.gridy = 4;
            cs.gridwidth = 1;
            settingsUI.add(defaultLocaleLabel, cs);
            cs.weightx = 0.5;
            cs.gridx = 1;
            cs.gridy = 4;
            cs.gridwidth = 2;
            settingsUI.add(localesScrollPane, cs);

            cs.weightx = 0;
            cs.gridx = 0;
            cs.gridy = 5;
            cs.gridwidth = 1;
            settingsUI.add(defaultStringsPathLabel, cs);
            cs.weightx = 0.5;
            cs.gridx = 1;
            cs.gridy = 5;
            cs.gridwidth = 2;
            settingsUI.add(defaultStringsPathField, cs);

            cs.weightx = 0.5;
            cs.gridx = 1;
            cs.gridy = 6;
            cs.gridwidth = 2;
            settingsUI.add(updateTranslationsCheckbox, cs);

            cs.weightx = 0.5;
            cs.gridx = 0;
            cs.gridy = 7;
            cs.gridwidth = 3;
            settingsUI.add(generateConfigLabel, cs);
        }
        return settingsUI;
    }

    @NotNull
    private JTextArea createTextPane(String text) {
        JTextArea textPane = new JTextArea();
        textPane.setLineWrap(true);
        textPane.setWrapStyleWord(true);
        textPane.setEditable(false);
        textPane.setOpaque(false);
        textPane.setText(text);
        return textPane;
    }

    @NotNull
    private JEditorPane createtHyperTextPane(String text) {
        JEditorPane hyperTextPane = new JEditorPane();
        hyperTextPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        hyperTextPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent event) {
                if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        Desktop.getDesktop().browse(event.getURL().toURI());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    ;
                }
            }
        });
        hyperTextPane.setEditable(false);
        hyperTextPane.setOpaque(false);
        hyperTextPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
        hyperTextPane.setText(text);
        return hyperTextPane;
    }

    private void initializeDynamicFields() {
        accessTokenField = new JTextField();
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
                        defaultLocaleSelect.setEnabled(false);

                    } else {
                        API api = new API(accessTokenField.getText().trim());
                        APIResource project = projects.getModelAt(
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
                APIResource locale = locales.getModelAt(
                        defaultLocaleSelect.getSelectedIndex());
                localeId = locale.getId();
            }
        });
    }

    private void resetProjectSelect() {
        projects = new APIResourceListModel();
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

        if (generateConfig) {
            if (accessTokenField.getText().isEmpty() || projectId.isEmpty() || localeId.isEmpty()) {
                JOptionPane.showMessageDialog(settingsUI, "Please make sure to enter a PhraseApp access_token, select a project and a default locale.");
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