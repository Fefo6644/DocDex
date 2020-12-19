package me.piggypiglet.docdex.bootstrap.implementations;

import com.google.inject.Inject;
import me.piggypiglet.docdex.bootstrap.framework.Registerable;
import me.piggypiglet.docdex.documentation.index.data.population.registerables.IndexPopulationRegisterable;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class StartMessageRegisterable extends Registerable {
    private static final Logger LOGGER = LoggerFactory.getLogger("DocDex");

    private final IndexPopulationRegisterable indexPopulationRegisterable;

    @Inject
    public StartMessageRegisterable(@NotNull final IndexPopulationRegisterable indexPopulationRegisterable) {
        this.indexPopulationRegisterable = indexPopulationRegisterable;
    }

    @Override
    protected void execute() {
        indexPopulationRegisterable.getCompleted().whenComplete((v, t) -> LOGGER.info("DocDex initialization process complete."));
    }
}