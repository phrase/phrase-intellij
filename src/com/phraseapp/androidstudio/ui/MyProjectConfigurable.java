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
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by gfrey on 22/10/15.
 */
public class MyProjectConfigurable implements SearchableConfigurable, Configurable.NoScroll {
    private String projectId = "";
    private String localeId = "";
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
    private JPanel generatePanel;
    private JButton generateConfig;
    private JCheckBox updateTranslationsCheckBox;
    private JPanel clientPanel;
    private JTextPane infoPane;
    private JTextPane configExistInfoText;
    private boolean alreadyGeneratedConfig = false;

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
        initializeActions();

        createHypertext(infoPane, "<p>The PhraseApp plugin requires a installed <b>PhraseApp Client</b> and a <b>.phraseapp.yml</b> configuration file. <a href=http://docs.phraseapp.com/developers/android_studio>Learn more</a></p>");
        createHypertext(configExistInfoText, "We have detected an existing <a href=http://docs.phraseapp.com/developers/cli/configuration/>.phraseapp.yml</a> configuration file.");

        PhraseAppConfiguration configuration = new PhraseAppConfiguration(getProject());
        currentConfig = configuration.loadPhraseAppConfig();

        if(PropertiesRepository.getInstance().getClientPath() == null){
            String detected = ClientDetection.findClientInstallation();
            if(detected != null){
                PropertiesRepository.getInstance().setClientPath(detected);
                JOptionPane.showMessageDialog(rootPanel, "We found a PhraseApp client on your system: " + detected);
            }
        }

        String clientPath = PropertiesRepository.getInstance().getClientPath();
        clientPathFormattedTextField.setText(clientPath);

        String accessToken = getAccessToken();
        accessTokenTextField.setText(accessToken);

        configPanel.setVisible(!configExists());
        generatePanel.setVisible(configExists());

        if (API.validateClient(clientPathFormattedTextField.getText().trim(), project.getBasePath())){
            enableClientRelatedFields();
        }

        return rootPanel;
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
                if (API.validateClient(clientPathFormattedTextField.getText().trim(), project.getBasePath())) {
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

        configExistInfoText.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent event) {
                handleLinkClick(event);
            }
        });

        generateConfig.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                configPanel.setVisible(true);
                generatePanel.setVisible(false);
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
                    APIResource project = projects.getModelAt(
                            projectsComboBox.getSelectedIndex());
                    projectId = project.getId();
                    updateLocaleSelect();
                }
            }
        });

        projectsComboBox.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                if (projects == null || projects.isEmpty()){
                    updateProjectSelect();
                }
            }

            @Override
            public void focusLost(FocusEvent focusEvent) {

            }
        });

        defaultLocaleComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == 1) {
                    APIResource locale = locales.getModelAt(
                            defaultLocaleComboBox.getSelectedIndex());
                    localeId = locale.getId();
                }
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
        generateConfig.setEnabled(false);
        updateTranslationsCheckBox.setEnabled(false);
        projectsComboBox.setEnabled(false);
        defaultLocaleComboBox.setEnabled(false);
    }

    private void enableClientRelatedFields() {
        accessTokenTextField.setEnabled(true);
        generateConfig.setEnabled(true);
        updateTranslationsCheckBox.setEnabled(true);
        projectsComboBox.setEnabled(true);
        defaultLocaleComboBox.setEnabled(true);
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

        if (! API.validateClient(clientPathFormattedTextField.getText().trim(), project.getBasePath())){
            JOptionPane.showMessageDialog(rootPanel, "PhraseApp Client validation failed. Please make sure it is installed correctly.");
            return;
        }

        if (projectId == null || localeId == null){
            JOptionPane.showMessageDialog(rootPanel, "Please verify that you have entered a valida access token and selected a project and locale.");
            return;
        }

        PropertiesRepository.getInstance().setClientPath(clientPathFormattedTextField.getText().trim());

        if (configPanel.isVisible() && !alreadyGeneratedConfig) {
            PropertiesRepository.getInstance().setAccessToken(accessTokenTextField.getText().trim());

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
                alreadyGeneratedConfig = true;
                PhraseAppConfiguration configuration = new PhraseAppConfiguration(getProject());
                configuration.generateConfig(getConfigMap());
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

    private Project getProject() {
        DataContext dataContext = DataManager.getInstance().getDataContext();
        Project project = (Project) dataContext.getData(DataConstants.PROJECT);
        return project;
    }

    private String getAccessToken() {
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

    private void resetProjectSelect() {
        projectId = null;
        projects = new APIResourceListModel();
        projectsComboBox.setModel(projects);
        projectsComboBox.setEnabled(false);
    }

    private void updateProjectSelect() {
        API api = new API(clientPathFormattedTextField.getText().trim(), accessTokenTextField.getText().trim(), project.getBasePath());
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
                }
            }
            projectsComboBox.setModel(projects);
            projectsComboBox.setEnabled(true);

            if(projects.isEmpty()) {
                resetLocaleSelect();
            } else {
                projectsComboBox.setSelectedIndex(0);
            }

        } else {
            resetLocaleSelect();
            projectsComboBox.setEnabled(false);
            JOptionPane.showMessageDialog(rootPanel, "The access_token is not valid. Please generate a APIv2 token at: https://phraseapp.com/settings/oauth_access_tokens");
        }
    }

    private void resetLocaleSelect() {
        localeId = null;
        locales = new APIResourceListModel();
        defaultLocaleComboBox.setModel(locales);
        defaultLocaleComboBox.setEnabled(false);
    }

    private void updateLocaleSelect() {
        API api = new API(clientPathFormattedTextField.getText().trim(), accessTokenTextField.getText().trim(), project.getBasePath());

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
                defaultLocaleComboBox.setModel(locales);
                defaultLocaleComboBox.setSelectedIndex(0);
                defaultLocaleComboBox.setEnabled(true);
            } else {
                defaultLocaleComboBox.setModel(locales);
                defaultLocaleComboBox.setSelectedIndex(0);
                defaultLocaleComboBox.setEnabled(true);
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
        Map<String, Boolean> pullParams = new HashMap<String, Boolean>();
        Map<String, String> pushParams = new HashMap<String, String>();


        if (updateTranslationsCheckBox.isSelected()) {
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
        root.put("access_token", accessTokenTextField.getText().trim());
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
}


