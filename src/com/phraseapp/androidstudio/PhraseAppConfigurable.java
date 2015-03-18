package com.phraseapp.androidstudio;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;


public class PhraseAppConfigurable implements Configurable {
    private JComponent settingsUI;
    private JTextField authTokenField;

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
        authTokenField = new JTextField(50);
        authTokenField.setText(TokenRepository.getInstance().getToken());
        authTokenField.setPreferredSize(new Dimension(200, 20));

        JLabel authTokenLabel = new JLabel();
        authTokenLabel.setText("PhraseApp Project Auth Token");

        settingsUI = new JPanel();
        settingsUI.setPreferredSize(new Dimension(400, 600));
        settingsUI.add(authTokenLabel);
        settingsUI.add(authTokenField);

        return settingsUI;
    }

    @Override
    public boolean isModified() {
        String text = authTokenField.getText();
        String token = TokenRepository.getInstance().getToken();
        return text == null ? token != null : !text.trim().equals(token);
    }

    @Override
    public void apply() throws ConfigurationException {
        TokenRepository.getInstance().setToken(authTokenField.getText().trim());
    }

    @Override
    public void reset() {
        authTokenField.setText(TokenRepository.getInstance().getToken());
    }

    @Override
    public void disposeUIResources() {
        if (settingsUI != null) {
            settingsUI.removeAll();
            settingsUI = null;
        }
    }
}