package com.phraseapp.androidstudio;

import javax.swing.*;

/**
 * Created by kolja on 15.10.15.
 */
public class APIResourceListModel extends DefaultComboBoxModel {

    public Object getElementAt(int index) {
        APIResource project = (APIResource) super.getElementAt(index);
        return project.getName();
    }

    public APIResource getModelAt(int index) {
        return (APIResource) super.getElementAt(index);
    }

    public boolean isEmpty() { return getSize() == 0; }

}
