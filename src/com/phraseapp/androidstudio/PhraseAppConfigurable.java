package com.phraseapp.androidstudio;

import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ide.util.PropertiesComponent;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.HeaderTokenizer;
import org.jdesktop.swingx.JXErrorPane;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;


public class PhraseAppConfigurable implements Configurable {
    private JComponent settingsUI;
    private JTextField accessTokenField;
    private JTextField projectIdField;

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
        accessTokenField = new JTextField(64);
        accessTokenField.setText(TokenRepository.getInstance().getAccessToken());
        accessTokenField.setSize(new Dimension(120, 20));
        JLabel accessTokenLabel = new JLabel();
        accessTokenLabel.setText("PhraseApp Access Token");

        projectIdField = new JTextField(32);
        projectIdField.setText(TokenRepository.getInstance().getProjectId());
        projectIdField.setSize(new Dimension(120, 20));
        JLabel projectIdLabel = new JLabel();
        projectIdLabel.setText("PhraseApp Project ID");

        settingsUI = new JPanel();
        settingsUI.setPreferredSize(new Dimension(600, 400));
        settingsUI.add(accessTokenLabel);
        settingsUI.add(accessTokenField);
        settingsUI.add(projectIdLabel);
        settingsUI.add(projectIdField);

        return settingsUI;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        if(accessTokenField.getText().length() > 20 && accessTokenField.getText().length() < 40){
            JOptionPane.showMessageDialog(settingsUI, "This looks like a APIv1 token. Please generate a APIv2 token at: https://phraseapp.com/settings/oauth_access_tokens");
        }
        TokenRepository.getInstance().setAccessToken(accessTokenField.getText().trim());
        TokenRepository.getInstance().setProjectId(projectIdField.getText().trim());
    }

    @Override
    public void reset() {
        accessTokenField.setText(TokenRepository.getInstance().getAccessToken());
        projectIdField.setText(TokenRepository.getInstance().getProjectId());
    }

    @Override
    public void disposeUIResources() {
        if (settingsUI != null) {
            settingsUI.removeAll();
            settingsUI = null;
        }
    }
}