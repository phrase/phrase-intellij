package com.phraseapp.androidstudio;

import javax.swing.DefaultListModel;

/**
 * Created by kolja on 15.10.15.
 */
public class APIResourceListModel extends DefaultListModel {

    public Object getElementAt(int index) {
        APIResource project = (APIResource) super.getElementAt(index);
        return project.getName();
    }

    public APIResource getModelAt(int index) {
        return (APIResource) super.getElementAt(index);
    }

}
