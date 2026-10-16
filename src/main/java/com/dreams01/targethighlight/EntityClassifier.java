package com.dreams01.targethighlight;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.Tags;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Decides whether an entity is blacklisted and which colour tier it belongs to.
 * Config entries are entity ids such as "minecraft:villager", or "modid:*" for every entity of a mod.
 */
public final class EntityClassifier {

    private static final CachedMatcher BLACKLIST = new CachedMatcher(THConfig.BLACKLIST::get);
    private static final CachedMatcher BOSSES = new CachedMatcher(THConfig.BOSS_IDS::get);
    private static final CachedMatcher CUSTOM = new CachedMatcher(THConfig.CUSTOM_IDS::get);

    private EntityClassifier() {
    }

    public static boolean isBlacklisted(LivingEntity e) {
        return BLACKLIST.matches(EntityType.getKey(e.getType()));
    }

    /** Boss wins over custom, custom wins over normal. */
    public static Tier classify(LivingEntity e) {
        EntityType<?> type = e.getType();
        ResourceLocation id = EntityType.getKey(type);

        if (BOSSES.matches(id) || (THConfig.BOSS_FROM_TAG.get() && type.is(Tags.EntityTypes.BOSSES))) {
            return Tier.BOSS;
        }
        if (CUSTOM.matches(id)) {
            return Tier.CUSTOM;
        }
        return Tier.NORMAL;
    }

    /** Parses a config list once and re-parses only when the list changes (e.g. after a config reload). */
    private static final class CachedMatcher {
        private final Supplier<List<? extends String>> source;
        private List<? extends String> last;
        private final Set<ResourceLocation> exact = new HashSet<>();
        private final Set<String> namespaces = new HashSet<>();

        CachedMatcher(Supplier<List<? extends String>> source) {
            this.source = source;
        }

        boolean matches(ResourceLocation id) {
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
}
