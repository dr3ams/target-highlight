package com.dreams01.targethighlight;

import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Matches registry ids against a config list. Entries are ids such as "minecraft:villager",
 * or "modid:*" for everything of a mod. The list is parsed once and re-parsed only when it
 * changes (for example after a config reload).
 */
public final class IdListMatcher {
    private final Supplier<List<? extends String>> source;
    private List<? extends String> last;
    private final Set<ResourceLocation> exact = new HashSet<>();
    private final Set<String> namespaces = new HashSet<>();

    public IdListMatcher(Supplier<List<? extends String>> source) {
        this.source = source;
    }

    public boolean matches(ResourceLocation id) {
        List<? extends String> current = source.get();
        if (current != last && !current.equals(last)) {
            rebuild(current);
        }
        last = current;
        return exact.contains(id) || namespaces.contains(id.getNamespace());
    }

    private void rebuild(List<? extends String> entries) {
        exact.clear();
        namespaces.clear();
        for (String raw : entries) {
            String s = raw.trim().toLowerCase();
            if (s.isEmpty()) {
                continue;
            }
            if (s.endsWith(":*")) {
                namespaces.add(s.substring(0, s.length() - 2));
            } else {
                ResourceLocation rl = ResourceLocation.tryParse(s);
                if (rl != null) {
                    exact.add(rl);
                }
            }
        }
    }
}
