package me.piggypiglet.docdex.documentation.index.data.storage.implementations;

import com.google.gson.Gson;
import me.piggypiglet.docdex.config.Javadoc;
import me.piggypiglet.docdex.documentation.index.data.storage.IndexStorage;
import me.piggypiglet.docdex.documentation.utils.DataUtils;
import me.piggypiglet.docdex.documentation.objects.DocumentedObject;
import me.piggypiglet.docdex.file.utils.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class FlatFileStorage implements IndexStorage {
    private static final Logger LOGGER = LoggerFactory.getLogger("FlatFileStorage");
    private static final Gson GSON = new Gson();

    @Override
    public void save(@NotNull final Javadoc javadoc, @NotNull final Map<String, DocumentedObject> objects) {
        final String fileName = DataUtils.getName(javadoc) + ".json";
        final File file = new File("docs", fileName);

        LOGGER.info("Attempting to save " + fileName);

        if (file.exists()) {
            LOGGER.info(fileName + " already exists, not saving. Delete the file manually and restart the app if you wish to update the index.");
            return;
        }

        if (!file.exists()) {
            file.getParentFile().mkdirs();

            try {
                file.createNewFile();
            } catch (IOException exception) {
                LOGGER.error("Something went wrong when creating " + fileName, exception);
                return;
            }
        }

        try {
            FileUtils.writeFile(file, GSON.toJson(objects));
        } catch (IOException exception) {
            LOGGER.error("Something went wrong when saving " + fileName, exception);
            return;
        }

        LOGGER.info("Saved " + fileName);
    }
}
