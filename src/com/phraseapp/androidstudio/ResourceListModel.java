package com.phraseapp.androidstudio;

import javax.swing.DefaultListModel;

/**
 * Created by kolja on 15.10.15.
 */
public class ResourceListModel extends DefaultListModel {

    public Object getElementAt(int index) {
        PhraseResource project = (PhraseResource) super.getElementAt(index);
        return project.getName();
    }

    public PhraseResource getModelAt(int index) {
        return (PhraseResource) super.getElementAt(index);
    }

}
