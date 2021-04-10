package us.skyywastaken.nametagrenderer.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.HashMap;
import java.util.Iterator;

@IFMLLoadingPlugin.TransformerExclusions("us.skyywastaken.nametagrenderer.asm")
@IFMLLoadingPlugin.MCVersion("1.8.9")
public class EntityLivingBaseTransformer implements IClassTransformer {
    private final HashMap<String, String> obfuscatedMappings;
    private final HashMap<String, String> deobfuscatedMappings;
    private final TransformMode transformMode;

    public EntityLivingBaseTransformer() {
        transformMode = TransformMode.NAMED_ONLY;

        obfuscatedMappings = new HashMap<>();
        obfuscatedMappings.put("className", "pr");
        obfuscatedMappings.put("methodName", "aO");
        obfuscatedMappings.put("asmClassLocation", "pr");
        obfuscatedMappings.put("hasCustomNameMethodName", "l_");

        deobfuscatedMappings = new HashMap<>();
        deobfuscatedMappings.put("className", "net.minecraft.entity.EntityLivingBase");
        deobfuscatedMappings.put("methodName", "getAlwaysRenderNameTagForRender");
        deobfuscatedMappings.put("asmClassLocation", "net/minecraft/entity/EntityLivingBase");
        deobfuscatedMappings.put("hasCustomNameMethodName", "hasCustomName");


    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if(name.equals(deobfuscatedMappings.get("className"))) {
            return transformClass(basicClass, deobfuscatedMappings);
        } else if(name.equals(obfuscatedMappings.get("className"))) {
            return transformClass(basicClass, obfuscatedMappings);
        }
        return basicClass;
    }

    private byte[] transformClass(byte[] classToTransform, HashMap<String, String> passedMappingsMap) {
        try {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(classToTransform);
            classReader.accept(classNode, 0);
            Iterator<MethodNode> methodNodeIterator = classNode.methods.iterator();
            while(methodNodeIterator.hasNext()){
                MethodNode currentMethod = methodNodeIterator.next();
                if(!currentMethod.name.equals(passedMappingsMap.get("methodName"))){
                    continue;
                }
                InsnList newInsnList = getNewInstructions(passedMappingsMap);
                Iterator<AbstractInsnNode> insnIterator = currentMethod.instructions.iterator();
                while(insnIterator.hasNext()){
                    AbstractInsnNode currentNode = insnIterator.next();
                    if(currentNode.getOpcode() == Opcodes.ICONST_0 && newInsnList != null) {
                        currentMethod.instructions.insertBefore(currentNode, newInsnList);
                        currentMethod.instructions.remove(currentNode);
                    }
                }
            }
            ClassWriter classWriter = new ClassWriter(0);
            classNode.accept(classWriter);
            return classWriter.toByteArray();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return classToTransform;
    }

    private InsnList getNewInstructions(HashMap<String, String> mappings) {
        switch(transformMode) {
            case NAMED_ONLY: {
                return getNamedOnlyInstructions(mappings);
            }
            case ALL_ENTITIES: {
                return getAllEntityInstructions();
            }
        }
        return null;
    }

    private InsnList getNamedOnlyInstructions(HashMap<String, String> mappings) {
        String asmClassLocation = mappings.get("asmClassLocation");
        String hasCustomNameMethod = mappings.get("hasCustomNameMethodName");
        InsnList newInstructions = new InsnList();
        newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        newInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, asmClassLocation, hasCustomNameMethod,
                "()Z", false));
        return newInstructions;
    }

    private InsnList getAllEntityInstructions() {
        InsnList newInstructions = new InsnList();
        newInstructions.add(new InsnNode(Opcodes.ICONST_1));
        return newInstructions;
    }
}
