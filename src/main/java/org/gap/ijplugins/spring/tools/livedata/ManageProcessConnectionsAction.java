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

package org.gap.ijplugins.spring.tools.livedata;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.popup.*;
import org.gap.ijplugins.spring.tools.ResourceBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ManageProcessConnectionsAction extends AnAction {
    private StsApplicationManager manager = new StsApplicationManager();


    public ManageProcessConnectionsAction() {
        super(ResourceBundle.getString("manage.live.spring.boot.process.connections"));
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(manager.canQuery(e.getProject()));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        List<ProcessCommandInfo> list = manager.listApplication(e.getProject());
        ListPopup popup = JBPopupFactory.getInstance().createActionGroupPopup(
                ResourceBundle.getString("manage.live.spring.boot.process.connections"),
                createActionGroupFor(list), e.getDataContext(),
                JBPopupFactory.ActionSelectionAid.SPEEDSEARCH, true);
        popup.showInBestPositionFor(e.getDataContext());
    }

    @Override
    public boolean isDumbAware() {
        return false;
    }

    private ActionGroup createActionGroupFor(final List<ProcessCommandInfo> list) {
        return new ActionGroup() {
            @NotNull
            @Override
            public AnAction[] getChildren(@Nullable AnActionEvent e) {
                return list.stream().map(p -> new ProcessAction(manager, p)).toArray(i -> new ProcessAction[i]);
            }
        };
    }
}
