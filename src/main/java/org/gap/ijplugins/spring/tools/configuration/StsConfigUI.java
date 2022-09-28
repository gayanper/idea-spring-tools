/*
 *  Copyright (c) 2020 Gayan Perera
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Contributors:
 *     Gayan Perera <gayanper@gmail.com> - initial API and implementation
 */
package org.gap.ijplugins.spring.tools.configuration;

import com.intellij.ui.components.fields.ExpandableTextField;

import javax.swing.*;

public class StsConfigUI {

    private ExpandableTextField txtJvmArgs;
    private JPanel view;

    public void reset(StsSettings settings) {
        txtJvmArgs.setText(settings.getJvmArgs());
    }

    public void apply(StsSettings settings) {
        settings.setJvmArgs(txtJvmArgs.getText().trim());
    }

    public boolean isModified(StsSettings settings) {
        return (!txtJvmArgs.getText().equals(settings.getJvmArgs()));
    }

    public JPanel getView() {
        return view;
    }
}