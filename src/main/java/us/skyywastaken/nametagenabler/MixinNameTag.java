package us.skyywastaken.nametagenabler;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;


@Mixin(Entity.class)
public abstract class MixinNameTag {
    @Shadow
    public abstract boolean hasCustomName();

    /**
     * @author SkyyWasTaken
     * @reason Making name tags always render if the entity has a custom name
     */
    @Overwrite
    public boolean isCustomNameVisible() {
        return this.hasCustomName();
    }
}