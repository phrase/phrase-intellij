package com.phraseapp.androidstudio.ui;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.phraseapp.androidstudio.API;
import com.phraseapp.androidstudio.ClientDetection;
import com.phraseapp.androidstudio.PropertiesRepository;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by gfrey on 22/10/15.
 */
public class MyPhraseAppConfigurable implements SearchableConfigurable, Configurable.NoScroll {
    private JPanel rootPanel;
    private TextFieldWithBrowseButton clientPathFormattedTextField;
    private InfoPane infoPane;


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
        return "The PhraseApp plugin requires a installed PhraseApp CLI Client and a configuration file.";
    }

    @Override
    public void reset() {
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        initializeActions();
        detectAndSetClientPath();
        infoPane.setContent("<p>The PhraseApp plugin requires a installed <b>PhraseApp Client</b> and a configuration file. <a href=http://docs.phraseapp.com/developers/android_studio>Learn more</a>.</p>");

        clientPathFormattedTextField.setText(PropertiesRepository.getInstance().getClientPath());

        return rootPanel;
    }

    private void detectAndSetClientPath() {
        if (PropertiesRepository.getInstance().getClientPath() == null) {
            String detected = ClientDetection.findClientInstallation();
            if (detected != null) {
                PropertiesRepository.getInstance().setClientPath(detected);
                JOptionPane.showMessageDialog(rootPanel, "A PhraseApp client was found on your system: " + detected);
            }
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
               API.validateClient(getClientPath());
            }
        });

        infoPane.initializeActions();
    }


    @Nls
    @Override
    public String getDisplayName() {
        return "PhraseApp";
    }

    @Override
    public void apply() {
        if (clientPathFormattedTextField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(rootPanel, "Please select the PhraseApp Client");
            return;
        }

        PropertiesRepository.getInstance().setClientPath(getClientPath());
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

    @NotNull
    private String getClientPath() {
        return clientPathFormattedTextField.getText().trim();
    }
}


