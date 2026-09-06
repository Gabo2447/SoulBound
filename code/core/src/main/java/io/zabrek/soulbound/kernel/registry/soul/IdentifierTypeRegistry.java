package io.zabrek.soulbound.kernel.registry.soul;

import io.zabrek.soulbound.api.SoulBoundException;
import io.zabrek.soulbound.api.identifier.Identifier;
import io.zabrek.soulbound.api.identifier.IdentifierFactory;
import io.zabrek.soulbound.api.logger.SoulBoundLogger;
import io.zabrek.soulbound.api.service.identifier.Identifiers;

import java.util.HashMap;
import java.util.Map;

/**
 * A default implementation of the {@link Identifiers} interface.
 */
public class IdentifierTypeRegistry implements Identifiers {

    /**
     * Custom {@link SoulBoundLogger} instance for this class.
     */
    private final SoulBoundLogger log;

    /**
     * Map of registered factories.
     */
    private final Map<Class<?>, IdentifierFactory<?>> types;

    /**
     * Create a new type registry.
     *
     * @param log the logger that will be used for logging
     */
    public IdentifierTypeRegistry(final SoulBoundLogger log) {
        this.log = log;
        this.types = new HashMap<>();
    }

    @Override
    public <I extends Identifier> void register(final Class<I> identifierClazz, final IdentifierFactory<I> factory) {
        log.debug("Registering identifier factory '%s' for '%s' type".formatted(factory.getClass().getSimpleName(), identifierClazz.getSimpleName()));
        types.put(identifierClazz, factory);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <I extends Identifier> IdentifierFactory<I> getFactory(final Class<I> clazz) throws SoulBoundException {
        final IdentifierFactory<I> factory = (IdentifierFactory<I>) types.get(clazz);
        if (factory == null) {
            throw new SoulBoundException("No registered identifier factory found for '%s' type".formatted(clazz.getSimpleName()));
        }
        return factory;
    }
}
