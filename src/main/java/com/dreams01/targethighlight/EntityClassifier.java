package com.dreams01.targethighlight;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.Tags;

/** Decides whether an entity is blacklisted and which colour tier it belongs to. */
public final class EntityClassifier {

    private static final IdListMatcher BLACKLIST = new IdListMatcher(THConfig.BLACKLIST::get);
    private static final IdListMatcher BOSSES = new IdListMatcher(THConfig.BOSS_IDS::get);
    private static final IdListMatcher CUSTOM = new IdListMatcher(THConfig.CUSTOM_IDS::get);

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
}
