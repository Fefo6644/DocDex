package me.piggypiglet.docdex.documentation.index.data.population.implementations.web.components;

import me.piggypiglet.docdex.documentation.index.data.population.implementations.web.utils.DeserializationUtils;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.DocumentedTypes;
import me.piggypiglet.docdex.documentation.objects.type.DocumentedTypeBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Element;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class TypeDeserializer {
    private static final Pattern LINE_DELIMITER = Pattern.compile("\n");
    private static final Pattern SPACE_DELIMITER = Pattern.compile(" ");

    @SuppressWarnings("DuplicatedCode")
    @NotNull
    public static DocumentedObject deserialize(@NotNull final Element description, @Nullable final Element packaj) {
        final DocumentedTypeBuilder builder = new DocumentedTypeBuilder();

        Optional.ofNullable(packaj).ifPresent(packageElement ->
                builder.packaj(packageElement.text().replace("Package ", "")));

        final Element pre = description.selectFirst("pre");
        final List<String> declaration = Arrays.stream(LINE_DELIMITER.split(pre.text()))
                .filter(line -> !line.startsWith("@"))
                .collect(Collectors.toList());
        final List<String> declarationAnchors = Optional.ofNullable(pre.selectFirst("span"))
                .map(Element::nextElementSiblings)
                .map(elements -> elements.select("a"))
                .stream()
                .flatMap(Collection::stream)
                .map(DeserializationUtils::generateFqn)
                .collect(Collectors.toList());

        Optional.ofNullable(pre.selectFirst("span"))
                .map(Element::previousElementSiblings)
                .map(elements -> elements.select("a"))
                .stream()
                .flatMap(Collection::stream)
                .map(element -> element.text(element.text().substring(1)))
                .map(element -> '@' + DeserializationUtils.generateFqn(element))
                .forEach(builder::annotations);

        DocumentedTypes type = DocumentedTypes.UNKNOWN;

        int j = 0;
        for (int i = 0; i < declaration.size(); ++i) {
            final List<String> parts = Arrays.asList(SPACE_DELIMITER.split(declaration.get(i)));

            switch (i) {
                case 0:
                    type = DocumentedTypes.fromCode(parts.get(parts.size() - 2));
                    builder.type(type)
                            .name(parts.get(parts.size() - 1))
                            .modifiers(parts.subList(0, parts.size() - 2));
                    break;

                case 1:
                    if (type == DocumentedTypes.INTERFACE) {
                        for (; j < parts.size() - 1; j++) {
                            builder.extensions(declarationAnchors.get(j));
                        }
                    } else {
                        builder.extensions(declarationAnchors.get(j++));
                    }
                    break;

                case 2:
                    if (parts.isEmpty()) break;

                    for (int k = j; j < (k + parts.size()) - 1; j++) {
                        builder.extensions(declarationAnchors.get(j));
                    }
                    break;
            }
        }

        Optional.ofNullable(description.selectFirst(".block")).ifPresent(descriptionBlock ->
                builder.description(descriptionBlock.text()));

        description.select("dl").forEach(meta -> {
            final String header = meta.selectFirst("dt").text();
            final Set<String> items = meta.select("code > a").stream()
                    .map(DeserializationUtils::generateFqn)
                    .collect(Collectors.toSet());

            if (header.equalsIgnoreCase("all implemented interfaces:")) {
                builder.allImplementations(items);
            }

            if (header.equalsIgnoreCase("all superinterfaces:")) {
                builder.superInterfaces(items);
            }

            if (header.equalsIgnoreCase("all known subinterfaces:")) {
                builder.subInterfaces(items);
            }

            if (header.equalsIgnoreCase("direct known subclasses:")) {
                builder.subClasses(items);
            }

            if (header.equalsIgnoreCase("all known implementing classes:")) {
                builder.implementingClasses(items);
            }
        });

        Optional.ofNullable(description.selectFirst(".deprecationBlock")).ifPresent(deprecationBlock -> {
            builder.deprecated(true);

            Optional.ofNullable(deprecationBlock.selectFirst(".deprecationComment")).ifPresent(deprecationComment -> {
                builder.deprecationMessage(deprecationComment.text());
            });
        });

        return builder.build();
    }
}