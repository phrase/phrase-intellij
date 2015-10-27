package com.phraseapp.androidstudio;

import java.awt.*;

/**
 * Created by kolja on 27.10.15.
 */
public interface ToolWindowOutputHandler {
    public void writeOutput(Color color, String text);

    public void writeColoredOutput(String text);
}
