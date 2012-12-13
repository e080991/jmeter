/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 */

/*
 * Created on May 21, 2004
 */
package ie.klatt.jmeter.testbeans.gui;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyEditorSupport;

public class TextAreaEditor extends PropertyEditorSupport implements FocusListener {

    public RSyntaxTextArea textUI;

    private RTextScrollPane scroller;

    /**
     * {@inheritDoc}
     */
    public void focusGained(FocusEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void focusLost(FocusEvent e) {
        firePropertyChange();
    }

    private void init() {// called from ctor, so must not be overridable
        textUI = new RSyntaxTextArea();
        textUI.addFocusListener(this);
        textUI.setSyntaxEditingStyle("text/java");
        scroller = new RTextScrollPane(textUI);
    }

    /**
     *
     */
    public TextAreaEditor() {
        super();
        init();
    }

    /**
     * @param source
     */
    public TextAreaEditor(Object source) {
        super(source);
        init();
        setValue(source);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAsText() {
        return textUI.getText();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Component getCustomEditor() {
        return scroller;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        textUI.setText(text);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(Object value) {
        if (value != null) {
            textUI.setText(value.toString());
        } else {
            textUI.setText("");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue() {
        return textUI.getText();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supportsCustomEditor() {
        return true;
    }
}
