package rusbik.mixins;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PhantomEntity.class)
public abstract class PhantomEntityMixin extends FlyingEntity {
    protected PhantomEntityMixin(EntityType<? extends FlyingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void dropLoot(DamageSource source, boolean causedByPlayer) {
        super.dropLoot(source, causedByPlayer);
        if (source.getAttacker() instanceof ShulkerEntity && this.random.nextDouble() < 0.4) {
            this.dropStack(new ItemStack(Items.ELYTRA));
        }
    }
}
