package net.pillowmc.shearapi.attachment;

import net.pillowmc.shearapi.runtime.ShearAPIRuntime;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import com.chocohead.mm.api.ClassTinkerers;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.Objects;

public class ShearAPIAttachmentEarlyRiser implements Runnable {
    public static final ResourceKey<Registry<AttachmentType<?>>> ATTACHMENT_TYPES_KEY = ResourceKey.createRegistryKey(new ResourceLocation(ShearAPIRuntime.MOD_ID, "attachment_types"));
    public static final Registry<AttachmentType<?>> ATTACHMENT_TYPES = new RegistryBuilder<>(ATTACHMENT_TYPES_KEY).create();

    private static void addAttachmentHolder(ClassNode target) {
        // Sorry Fabric, but I have to do this.
        target.superName = "net/neoforged/neoforge/attachment/AttachmentHolder";
        target.interfaces.add("net/pillowmc/shearapi/attachment/IntoAttachmentHolder");
    }

    private static void itemStackAddConstructors(ClassNode itemStackClass) {
        var con1 = itemStackClass.methods.stream().filter(c -> c.name.equals("<init>") && c.desc.equals("(Lnet/minecraft/core/Holder;ILjava/util/Optional;)V")).findFirst().get();
        var con2 = new MethodNode(); // public ItemStack(ItemLike p_312081_, int p_41605_, Optional<CompoundTag> p_41606_);
        con1.accept(con2); // copy
        con2.desc = "(Lnet/minecraft/world/level/ItemLike;ILjava/util/Optional;)V";
        con2.instructions.forEach(i -> {
            if (i instanceof MethodInsnNode m && Objects.equals(m.name, "<init>")) {
                m.desc = "(Lnet/minecraft/world/level/ItemLike;I)V";
            }
        });
        itemStackClass.methods.add(con2);

        // Constructing constructor...
        var con3 = new MethodNode(
                Opcodes.ASM9,
                Opcodes.ACC_PUBLIC,
                "<init>",
                "(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/nbt/CompoundTag;)V",
                null,
                new String[0]
        ); // public ItemStack(ItemLike item, int count, @Nullable CompoundTag attachmentsNbt);
        var l0 = new LabelNode();
        con3.instructions.add(l0);
        con3.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        con3.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
        con3.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
        con3.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/world/item/ItemStack", "<init>", "(Lnet/minecraft/world/level/ItemLike;I)V"));
        con3.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        con3.instructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
        con3.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/pillowmc/shearapi/attachment/ItemStackNewConstructors", "setAttachment", "(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/nbt/CompoundTag;)V"));
        con3.instructions.add(new LabelNode()); // L1
        con3.instructions.add(new InsnNode(Opcodes.RETURN));
        var l2 = new LabelNode();
        con3.instructions.add(l2);
        con3.localVariables.add(new LocalVariableNode("this", "Lnet/minecraft/world/item/ItemStack;", null, l0, l2, 0));
        con3.localVariables.add(new LocalVariableNode("item", "Lnet/minecraft/world/level/ItemLike;", null, l0, l2, 1));
        con3.localVariables.add(new LocalVariableNode("count", "I", null, l0, l2, 2));
        con3.localVariables.add(new LocalVariableNode("tag", "Lnet/minecraft/nbt/CompoundTag;", null, l0, l2, 3));
        con3.maxLocals = 3;
        con3.maxStack = 3;
        itemStackClass.methods.add(con3);
    }

    @Override
    public void run() {
        ClassTinkerers.addTransformation("net/minecraft/world/entity/Entity", ShearAPIAttachmentEarlyRiser::addAttachmentHolder);
        ClassTinkerers.addTransformation("net/minecraft/world/item/ItemStack", ShearAPIAttachmentEarlyRiser::addAttachmentHolder);
        ClassTinkerers.addTransformation("net/minecraft/world/item/ItemStack", ShearAPIAttachmentEarlyRiser::itemStackAddConstructors);
        ClassTinkerers.addTransformation("net/minecraft/world/level/Level", ShearAPIAttachmentEarlyRiser::addAttachmentHolder);
        ClassTinkerers.addTransformation("net/minecraft/world/level/block/entity/BlockEntity", ShearAPIAttachmentEarlyRiser::addAttachmentHolder);
    }
}
