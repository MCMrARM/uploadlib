package io.mrarm.uploadlib;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The class managing all FileUploadProvider-s. All providers must be created and registered with
 * FileUploadProviderManager.register.
 */
public class FileUploadProviderManager {

    private static Map<UUID, FileUploadProvider> providers = new HashMap<>();

    public static void add(FileUploadProvider provider) {
        UUID uuid = provider.getUUID();
        if (providers.containsKey(uuid))
            throw new RuntimeException("A provider with this UUID already exists");
        providers.put(uuid, provider);
    }

    public static FileUploadProvider findProviderWithUUID(UUID uuid) {
        return providers.get(uuid);
    }

    public static Collection<FileUploadProvider> getAll() {
        return providers.values();
    }

}
