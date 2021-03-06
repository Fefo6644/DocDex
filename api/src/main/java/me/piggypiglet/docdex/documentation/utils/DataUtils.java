package me.piggypiglet.docdex.documentation.utils;

import me.piggypiglet.docdex.config.Javadoc;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.documentation.objects.detail.DetailMetadata;
import org.jetbrains.annotations.NotNull;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class DataUtils {
    private DataUtils() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    @NotNull
    public static String getName(@NotNull final DocumentedObject object) {
        String prefix = object.getMetadata() instanceof DetailMetadata ? ((DetailMetadata) object.getMetadata()).getOwner() : "";

        switch (object.getType()) {
            case CONSTRUCTOR:
            case METHOD:
                prefix += '#';
                break;
            case FIELD:
                prefix += '%';
                break;
        }

        return prefix + object.getName();
    }

    @NotNull
    public static String getFqn(@NotNull final DocumentedObject object) {
        return object.getPackage() + '.' + getName(object);
    }

    @NotNull
    public static String getName(@NotNull final Javadoc javadoc) {
        return String.join("-", javadoc.getNames());
    }

    @NotNull
    public static String removeTypeParams(@NotNull final String type) {
        int lastIndex = type.lastIndexOf('<');
        lastIndex = lastIndex == -1 ? type.length() : lastIndex;

        return type.substring(0, lastIndex);
    }
}
