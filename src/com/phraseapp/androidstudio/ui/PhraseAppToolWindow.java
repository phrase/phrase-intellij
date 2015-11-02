package com.phraseapp.androidstudio.ui;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.phraseapp.androidstudio.ConfigAction;

/**
 * Created by kolja on 12.10.15.
 */
public class PhraseAppToolWindow implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        toolWindow.setToHideOnEmptyContent(true);

        ToolWindowPane panel = new ToolWindowPane(true, true);

        final Content content = ContentFactory.SERVICE.getInstance().createContent(panel, "", false);
        content.setCloseable(true);

        ActionToolbar actionToolbar = createToolbar();
        panel.setToolbar(actionToolbar.getComponent());

        ColorTextPane textArea = new ColorTextPane();
        panel.setOutputTextArea(textArea);

        toolWindow.getContentManager().addContent(content);
        toolWindow.activate(null);
    }

    private ActionToolbar createToolbar() {
        DefaultActionGroup group = new DefaultActionGroup();

        PushButton pushB = new PushButton("Push", "", IconLoader.getIcon("/cloud-upload.png"));
        group.add(pushB);

        PullButton pullB = new PullButton("Pull", "", IconLoader.getIcon("/cloud-download.png"));
        group.add(pullB);

        group.addSeparator();

        ConfigAction configB = new ConfigAction("Create Config", "", IconLoader.getIcon("/cog.png"));
        group.add(configB);

        group.addSeparator();

        HelpButton helpB = new HelpButton("Help", "", IconLoader.getIcon("/question.png"));
        group.add(helpB);

        return ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, group, true);
    }
}
