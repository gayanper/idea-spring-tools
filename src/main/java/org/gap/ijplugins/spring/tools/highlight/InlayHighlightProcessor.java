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

package org.gap.ijplugins.spring.tools.highlight;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.intellij.codeInsight.daemon.impl.HintRenderer;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.Inlay;
import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.Range;

import java.util.Collection;

public class InlayHighlightProcessor implements HighlightProcessor {

    private Multimap<String, Inlay<HintRenderer>> inlays = ArrayListMultimap.create();

    @Override
    public void preProcess(String documentUri, Editor editor) {
        final Collection<Inlay<HintRenderer>> rangeHighlighters = inlays.get(documentUri);
        if (rangeHighlighters != null) {
            rangeHighlighters.forEach(Inlay::dispose);
        }
    }

    @Override
    public void process(String documentUri, CodeLens codeLens, Editor editor) {
        if (codeLens.getData() == null) {
            return;
        }

        final Document document = editor.getDocument();
        Range range = codeLens.getRange();
        int endOffset =
            document.getLineStartOffset(range.getEnd().getLine()) + range.getEnd().getCharacter();

        HintRenderer renderer = new HintRenderer(codeLens.getData().toString());
        Inlay<HintRenderer> inlay = editor.getInlayModel().addInlineElement(endOffset, renderer);
        inlays.put(documentUri, inlay);
    }
}
