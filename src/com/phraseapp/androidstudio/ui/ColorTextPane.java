package com.phraseapp.androidstudio.ui;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.Color;

public class ColorTextPane extends JTextPane {
    static final Color D_Black   = Color.getHSBColor( 0.000f, 0.000f, 0.000f );
    static final Color D_Green   = Color.getHSBColor(0.333f, 1.000f, 0.502f);
    static final Color D_Red = Color.getHSBColor(0.000f, 1.000f, 0.502f);

    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_GREEN = "\u001B[0;32;1m";
    public static final String ANSI_STOP = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[0;31m";;
    static Color colorCurrent    = D_Black;
    String remaining = "";

    public void append(Color c, String s) {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
        int len = getDocument().getLength();
        setCaretPosition(len);
        setCharacterAttributes(aset, false);
        replaceSelection(s);
    }

    public void appendANSI(String s) {
        int aPos = 0;
        int aIndex = 0;
        int mIndex = 0;
        String tmpString = "";
        boolean stillSearching = true;
        String addString = remaining + s;
        remaining = "";

        if (addString.length() > 0) {
            aIndex = addString.indexOf("\u001B");
            if (aIndex == -1) {
                append(colorCurrent,addString);
                return;
            }

            if (aIndex > 0) {
                tmpString = addString.substring(0,aIndex);
                append(colorCurrent, tmpString);
                aPos = aIndex;
            }

            stillSearching = true;
            while (stillSearching) {
                mIndex = addString.indexOf("m",aPos);
                if (mIndex < 0) {
                    remaining = addString.substring(aPos,addString.length());

                    stillSearching = false;
                    continue;
                }
                else {
                    tmpString = addString.substring(aPos,mIndex+1);
                    colorCurrent = getANSIColor(tmpString);
                }
                aPos = mIndex + 1;

                aIndex = addString.indexOf("\u001B", aPos);


                if (aIndex == -1) {
                    tmpString = addString.substring(aPos,addString.length());
                    append(colorCurrent, tmpString);
                    stillSearching = false;
                    continue;
                }

                tmpString = addString.substring(aPos,aIndex);
                aPos = aIndex;
                append(colorCurrent, tmpString);
            }
        }
    }

    public Color getANSIColor(String ANSIColor) {
        if (ANSIColor.equals(ANSI_BLACK))        { return D_Black; }
        else if (ANSIColor.equals(ANSI_GREEN)) { return D_Green; }
        else if (ANSIColor.equals(ANSI_RED)) { return D_Red; }
        else if (ANSIColor.equals(ANSI_STOP))    { return D_Black; }
        else { return D_Black; }
    }
}