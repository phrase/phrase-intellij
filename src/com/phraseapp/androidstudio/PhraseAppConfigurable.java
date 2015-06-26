package com.phraseapp.androidstudio;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.HeaderTokenizer;
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