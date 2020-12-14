package me.piggypiglet.docdex.documentation.index.data.population.implementations.web.components.method;

import me.piggypiglet.docdex.documentation.index.data.population.implementations.web.utils.DeserializationUtils;
import me.piggypiglet.docdex.documentation.objects.method.DocumentedMethodBuilder;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.piggypiglet.docdex.documentation.index.data.population.implementations.web.components.method.MethodDeserializer.LIST_DELIMITER;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class OldSignatureDeserializer {
    private static final Pattern ANNOTATION_PATTERN = Pattern.compile("@.+?(?=[ \\n])");
    private static final Pattern SPACE_DELIMITER = Pattern.compile(" ");

    private OldSignatureDeserializer() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    static void deserialize(@NotNull final Element details, @NotNull final DocumentedMethodBuilder builder,
                            @NotNull final String name) {
        final Element pre = details.selectFirst("pre");
        final Matcher annotationMatcher = ANNOTATION_PATTERN.matcher(pre.text());

        final Set<String> annotations = new HashSet<>();
        while (annotationMatcher.find()) {
            annotations.add(annotationMatcher.group());
        }

        final AtomicReference<String> preTextReference = new AtomicReference<>(pre.text());

        pre.select("a").stream()
                .filter(anchor -> annotations.contains(anchor.text()))
                .peek(annotation -> preTextReference.set(preTextReference.get().replace(annotation.text(), "")))
                .map(annotation -> annotation.text(annotation.text().substring(1)))
                .map(DeserializationUtils::generateFqn)
                .forEach(annotation -> builder.annotations('@' + annotation));

        final String preText = preTextReference.get().trim();
        final String[] preSplit = SPACE_DELIMITER.split(preText);

        int lastModifierIndex = 0;
        for (int i = 0; i < preSplit.length; ++i) {
            if (preSplit[i].toLowerCase().startsWith(name)) {
                builder.returns(preSplit[i - 1]);
                lastModifierIndex = i - 2;
                break;
            }
        }

        for (int i = 0; i < lastModifierIndex; ++i) {
            builder.modifiers(preSplit[i]);
        }

        Arrays.stream(LIST_DELIMITER.split(preText.substring(preText.indexOf('(') + 1, preText.indexOf(')')).replace("\n", " ")))
                .map(String::trim)
                .forEach(builder::parameters);
    }
}
