package org.gap.ijplugins.spring.tools.highlight;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.*;
import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.Range;
import org.gap.ijplugins.spring.tools.SpringBootGutterIconRenderer;

import java.awt.*;
import java.util.Collection;

public class RangeHighlightProcessor implements HighlightProcessor {

    private Multimap<String, RangeHighlighter> ranges = ArrayListMultimap.create();
    private final static Color SPRING_BOOT_HINT_COLOR = new Color(0x32, 0xBA, 0x56);

    @Override
    public void preProcess(String documentUri, Editor editor) {
        final Collection<RangeHighlighter> rangeHighlighters = ranges.get(documentUri);
        if (rangeHighlighters != null) {
            rangeHighlighters.forEach(r -> {
                editor.getMarkupModel().removeHighlighter(r);
            });
        }
    }

    @Override
    public void process(String documentUri, CodeLens codeLens, Editor editor) {
        final Document document = editor.getDocument();
        Range range = codeLens.getRange();

        int startOffset = document.getLineStartOffset(range.getStart().getLine()) + range.getStart()
            .getCharacter();
        int endOffset =
            document.getLineStartOffset(range.getEnd().getLine()) + range.getEnd().getCharacter();

        TextAttributes attrs = new TextAttributes();
        attrs.setEffectType(EffectType.BOXED);
        attrs.setEffectColor(SPRING_BOOT_HINT_COLOR);
        attrs.setErrorStripeColor(SPRING_BOOT_HINT_COLOR);
        RangeHighlighter highlighter = editor.getMarkupModel()
            .addRangeHighlighter(startOffset, endOffset, HighlighterLayer.ERROR, attrs,
                HighlighterTargetArea.EXACT_RANGE);
        highlighter.setGutterIconRenderer(SpringBootGutterIconRenderer.INSTANCE);
        ranges.put(documentUri, highlighter);
    }
}
