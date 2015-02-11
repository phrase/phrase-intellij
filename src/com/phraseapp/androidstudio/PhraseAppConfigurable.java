package com.phraseapp.androidstudio;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.wm.ToolWindow;
import com.jgoodies.forms.layout.FormLayout;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;


public class PhraseAppConfigurable implements Configurable {

    private JComponent settingsUI;
    private JTextField authTokenField;
    private JCheckBox forceUpdateCheckbox;

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

        String authTokenValue = PropertiesComponent.getInstance().getValue(PhraseAppAPI.PHRASE_AUTH_TOKEN_KEY);

        JPanel settingsUI = new JPanel();
        settingsUI.setPreferredSize(new Dimension(400, 600));

        JLabel authTokenLabel = new JLabel();
        authTokenLabel.setText("PhraseApp Project Auth Token");

        JTextField authTokenField = new JTextField(50);
        authTokenField.setText(authTokenValue);
        authTokenField.setPreferredSize(new Dimension(200, 20));

        JLabel forceUpdateLabel = new JLabel();
        forceUpdateLabel.setText("Force update of keys (override)?");
        forceUpdateCheckbox = new JCheckBox();


        settingsUI.add(authTokenLabel);
        settingsUI.add(authTokenField);
        settingsUI.add(forceUpdateLabel);
        settingsUI.add(forceUpdateCheckbox);
        return settingsUI;

    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        if (authTokenField != null && !authTokenField.getText().equals("")) {
            PropertiesComponent.getInstance().setValue(PhraseAppAPI.PHRASE_AUTH_TOKEN_KEY, authTokenField.getText().trim());
        }
        if(forceUpdateCheckbox.isSelected()){
            PropertiesComponent.getInstance().setValue(PhraseAppAPI.PHRASE_UPDATE_TRANSLATIONS_KEY, "1");
        }else{
            PropertiesComponent.getInstance().setValue(PhraseAppAPI.PHRASE_UPDATE_TRANSLATIONS_KEY, "0");
        }

    }

    @Override
    public void reset() {
        if (authTokenField != null) {
            String value = PropertiesComponent.getInstance().getValue(PhraseAppAPI.PHRASE_AUTH_TOKEN_KEY);
            authTokenField.setText(value != null ? value : "");
        }

    }

    @Override
    public void disposeUIResources() {
        if (settingsUI != null) {
            settingsUI.removeAll();
            settingsUI = null;
        }
    }
}