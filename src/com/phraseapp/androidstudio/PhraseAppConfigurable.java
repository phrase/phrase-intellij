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
import org.yaml.snakeyaml.Yaml;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class PhraseAppConfigurable implements Configurable {
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
    private String currentConfig;
    private JPanel rootPanel;

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
        rootPanel = new JPanel(new GridBagLayout());
        final JPanel topPanel = getTopPanel();
        final JPanel settingsUI = getPhraseConfigPanel();
        final JPanel cliPanel = getCliPanel();


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
        rootPanel.add(cliPanel, cs);
        cs.insets = new Insets(0, 0, 10, 0);
        cs.weighty = 0.5;
        cs.gridx = 0;
        cs.gridy = 2;
        rootPanel.add(settingsUI, cs);

        return rootPanel;
    }

    private JPanel getTopPanel() {
        final JPanel infoPanel = new JPanel(new GridLayout(0, 1));
        JEditorPane infoText = createtHyperTextPane("<p>The PhraseApp plugin requires a installed <b>PhraseApp Client</b> and a <b>.phraseapp.yml</b> configuration file. <a href=http://docs.phraseapp.com/developers/android_studio>Learn more</a></p>");
        infoPanel.add(infoText);
        return infoPanel;
    }

    private JPanel getCliPanel(){
        final JPanel cliPanel = new JPanel(new GridBagLayout());

        if (PropertiesRepository.getInstance().getClientPath() == null) {
            String detected = ClientDetection.findClientInstallation();
            if (detected != null) {
                PropertiesRepository.getInstance().setClientPath(detected);
                JOptionPane.showMessageDialog(cliPanel, "We found a PhraseApp client on your system: " + detected);
            }
        }


        clientPathField = new TextFieldWithBrowseButton();
        final FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(true, false, false, false, false, false) {
            public boolean isFileSelectable(VirtualFile file) {
                return file.getName().startsWith("phraseapp");
            }
        };
        clientPathField.addBrowseFolderListener("Choose PhraseApp Client", "", null, fileChooserDescriptor);
        clientPathField.setText(PropertiesRepository.getInstance().getClientPath());

        JLabel clientPathLabel = new JLabel();
        clientPathLabel.setText("PhraseApp Client Path");

        GridBagConstraints cs = new GridBagConstraints();

        cs.fill = GridBagConstraints.HORIZONTAL;
        cs.anchor = GridBagConstraints.NORTHWEST;
        cs.insets = new Insets(0, 0, 10, 0);
        cs.weightx = 0;
        cs.gridx = 0;
        cs.gridy = 0;
        cs.gridwidth = 1;
        cliPanel.add(clientPathLabel, cs);
        cs.weightx = 0.5;
        cs.gridx = 1;
        cs.gridy = 0;
        cs.gridwidth = 2;
        cliPanel.add(clientPathField, cs);

        return cliPanel;
    }

    @NotNull
    private JPanel getPhraseConfigPanel() {
        final JPanel phraseConfigPanel = new JPanel(new GridBagLayout());
        PhraseAppConfiguration configuration = new PhraseAppConfiguration(getProject());
        currentConfig = configuration.loadPhraseAppConfig();

        JEditorPane configInfoText;
        if (configExists()) {
            configInfoText = createtHyperTextPane("We have detected an existing <a href=http://docs.phraseapp.com/developers/cli/configuration/>.phraseapp.yml</a> configuration file. If you want to generate a new one then please fill in the following settings:");
        } else {
            configInfoText = createtHyperTextPane("Please fill in the following settings in order to generate a <a href=http://docs.phraseapp.com/developers/cli/configuration/>.phraseapp.yml</a> configuration file. ");
        }

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

        defaultStringsPathField = new TextFieldWithBrowseButton();
        final FileChooserDescriptor localeFileChooserDesc = new FileChooserDescriptor(true, false, false, false, false, false) {
            public boolean isFileSelectable(VirtualFile file) {
                return file.getName().startsWith("strings");
            }
        };

        defaultStringsPathField.addBrowseFolderListener("Choose default locale", "", null, localeFileChooserDesc);
        defaultStringsPathField.setText(PropertiesRepository.getInstance().getDefaultStringsPath());
        JLabel defaultStringsPathLabel = new JLabel();
        defaultStringsPathLabel.setText("Default strings");

        updateTranslationsCheckbox = new JCheckBox("Update Translations");

        GridBagConstraints cs = new GridBagConstraints();

        cs.fill = GridBagConstraints.HORIZONTAL;
        cs.anchor = GridBagConstraints.NORTHWEST;
        cs.insets = new Insets(0, 0, 10, 0);
        cs.weightx = 0.5;
        cs.gridx = 0;
        cs.gridy = 0;
        cs.gridwidth = 3;
        phraseConfigPanel.add(configInfoText, cs);
        cs.weightx = 0;
        cs.gridx = 0;
        cs.gridy = 1;
        cs.gridwidth = 1;
        phraseConfigPanel.add(accessTokenLabel, cs);
        cs.weightx = 0.5;
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 2;
        phraseConfigPanel.add(accessTokenField, cs);

        cs.weightx = 0.5;
        cs.gridx = 1;
        cs.gridy = 2;
        cs.gridwidth = 2;
        phraseConfigPanel.add(accessTokenHint, cs);

        cs.weightx = 0;
        cs.gridx = 0;
        cs.gridy = 3;
        cs.gridwidth = 1;
        phraseConfigPanel.add(projectIdLabel, cs);
        cs.weightx = 0.5;
        cs.gridx = 1;
        cs.gridy = 3;
        cs.gridwidth = 2;
        phraseConfigPanel.add(projectsScrollPane, cs);

        cs.weightx = 0;
        cs.gridx = 0;
        cs.gridy = 4;
        cs.gridwidth = 1;
        phraseConfigPanel.add(defaultLocaleLabel, cs);
        cs.weightx = 0.5;
        cs.gridx = 1;
        cs.gridy = 4;
        cs.gridwidth = 2;
        phraseConfigPanel.add(localesScrollPane, cs);

        cs.weightx = 0;
        cs.gridx = 0;
        cs.gridy = 5;
        cs.gridwidth = 1;
        phraseConfigPanel.add(defaultStringsPathLabel, cs);
        cs.weightx = 0.5;
        cs.gridx = 1;
        cs.gridy = 5;
        cs.gridwidth = 2;
        phraseConfigPanel.add(defaultStringsPathField, cs);

        cs.weightx = 0.5;
        cs.gridx = 1;
        cs.gridy = 6;
        cs.gridwidth = 2;
        phraseConfigPanel.add(updateTranslationsCheckbox, cs);

        return phraseConfigPanel;
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

    private void initializeDynamicFields() {
        accessTokenField = new JTextField();
        accessTokenField.setText(getAccessToken());

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
            API api = new API(accessTokenField.getText().trim(), getProject().getBasePath());
            projects = api.getProjects();
            if (projects != null) {
                projectSelect.setModel(projects);
                projectSelect.setEnabled(true);
            } else {
                accessTokenField.setText("");
            }
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
                        updateLocaleSelect();
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

    private void updateLocaleSelect() {
        API api = new API(accessTokenField.getText().trim(), getProject().getBasePath());
        APIResource project = projects.getModelAt(
                projectSelect.getSelectedIndex());
        projectId = project.getId();

        if (!projectId.isEmpty()) {
            locales = api.getLocales(projectId);
            if (locales.isEmpty()) {
                String[] localesList = {"en", "de", "fr", "es", "it", "pt", "zh"};

                String localeName = (String) JOptionPane.showInputDialog(rootPanel,
                        "No locales found. What is the name of the locale we should create for you?",
                        "PhraseApp",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        localesList,
                        localesList[0]);
                locales = api.postLocales(projectId, localeName);
                defaultLocaleSelect.setModel(locales);
                defaultLocaleSelect.setEnabled(true);
            } else {
                defaultLocaleSelect.setModel(locales);
                defaultLocaleSelect.setEnabled(true);
            }
        }
    }

    private void resetProjectSelect() {
        projects = new APIResourceListModel();
        projectSelect.setModel(projects);
    }

    private void updateProjectSelect() {
        API api = new API(accessTokenField.getText().trim(), getProject().getBasePath());
        projects = api.getProjects();

        if (projects != null) {
            if (projects.isEmpty()) {
                int choice = JOptionPane.showOptionDialog(null,
                        "No projects found. Should we create an initial project for you?",
                        "PhraseApp",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null, null, null);
                if (choice == JOptionPane.YES_OPTION) {
                    projects = api.postProjects(getProject().getName());
                    if (projects != null) {
                        projectSelect.setModel(projects);
                        projectSelect.setEnabled(true);
                    }
                }

            } else {
                projectSelect.setModel(projects);
                projectSelect.setEnabled(true);
            }
        } else {
            projectSelect.setEnabled(false);
            JOptionPane.showMessageDialog(rootPanel, "The access_token is not valid. Please generate a APIv2 token at: https://phraseapp.com/settings/oauth_access_tokens");
        }
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        if (clientPathField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(rootPanel, "Please select the phraseapp client");
            return;
        }

        PropertiesRepository.getInstance().setClientPath(clientPathField.getText().trim());

        int genrateConfigChoice = JOptionPane.YES_OPTION;
        if (configExists()) {
            genrateConfigChoice = JOptionPane.showOptionDialog(null,
                    "Should we generate a new .phraseap.yml with your current seetings?",
                    "PhraseApp",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, null, null);
        }

        if (genrateConfigChoice == JOptionPane.YES_OPTION) {
            PhraseAppConfiguration configuration = new PhraseAppConfiguration(getProject());
            configuration.generateConfig(getConfigMap());
        }

        PropertiesRepository.getInstance().setAccessToken(accessTokenField.getText().trim());
        PropertiesRepository.getInstance().setDefaultStringsPath(defaultStringsPathField.getText().trim());
    }

    @Override
    public void reset() {
        clientPathField.setText(PropertiesRepository.getInstance().getClientPath());
    }

    @Override
    public void disposeUIResources() {
        if (rootPanel != null) {
            rootPanel.removeAll();
            rootPanel = null;
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

    private Project getProject() {
        DataContext dataContext = DataManager.getInstance().getDataContext();
        Project project = (Project) dataContext.getData(DataConstants.PROJECT);
        return project;
    }

    private String getAccessToken() {
        if (configExists()) {
            System.out.printf(getAccessTokenFromConfig());
            return getAccessTokenFromConfig();
        } else {
            System.out.printf("no config");

            return PropertiesRepository.getInstance().getAccessToken();
        }
    }

    private String getAccessTokenFromConfig() {
        Yaml yaml = new Yaml();
        Map configYml = (Map) yaml.load(currentConfig);
        Map root = (Map) configYml.get("phraseapp");
        return (String) root.get("access_token");
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
                        JOptionPane.showMessageDialog(rootPanel, "Could not locate browser, please head to " + event.getURL().toString());
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(rootPanel, "Could not locate browser, please head to " + event.getURL().toString());
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

    private boolean configExists() {
        System.out.printf(currentConfig);
        return currentConfig.startsWith("phraseapp");
    }
}