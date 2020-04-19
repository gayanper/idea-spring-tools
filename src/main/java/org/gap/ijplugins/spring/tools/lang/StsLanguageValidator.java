package org.gap.ijplugins.spring.tools.lang;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.gap.ijplugins.spring.tools.util.Throwables;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class StsLanguageValidator {
    private static final String NS = "http://www.springframework.org/schema/beans";

    private static final Logger LOGGER = Logger.getInstance(StsLanguageValidator.class);

    @Nullable
    public boolean isXmlSpringBeanFile(@NotNull VirtualFile virtualFile, @NotNull Project project) {
        if ("xml".equals(virtualFile.getExtension())) {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLStreamReader xmlStreamReader = null;
            try (InputStream inputStream = virtualFile.getInputStream()) {
                xmlStreamReader = inputFactory.createXMLStreamReader(inputStream);
                while (xmlStreamReader.hasNext()) {
                    int elementType = xmlStreamReader.next();
                    if (elementType == XMLStreamReader.START_ELEMENT) {
                        if (NS.equals(xmlStreamReader.getNamespaceURI())) {
                            return true;
                        }
                        break;
                    }
                }
            } catch (IOException | XMLStreamException e) {
                LOGGER.warn("Failed to process xml file", e);
            } finally {
                Optional.ofNullable(xmlStreamReader)
                        .ifPresent(Throwables.fromThrowable((r) -> r.close(), LOGGER::warn));
            }
        }
        return false;
    }
}
