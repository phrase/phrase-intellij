package com.phrase.intellij;

import com.intellij.openapi.util.text.StringUtil;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by kolja on 15.10.15.
 */
public class APIResourceListModel extends DefaultComboBoxModel {

    private ArrayList<String> errors = new ArrayList<String>();

    public Object getElementAt(int index) {
        APIResource project = (APIResource) super.getElementAt(index);
        return project.getName();
    }

    public APIResource getModelAt(int index) {
        return (APIResource) super.getElementAt(index);
    }

    public boolean isEmpty() { return getSize() == 0; }

    public void addError(String error){
        errors.add(error);
    }

    public boolean isValid(){
        return errors.isEmpty();
    }

    public String getErrors() {
        return StringUtil.join(errors, ". ");
    }
}
