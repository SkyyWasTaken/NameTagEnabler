package us.skyywastaken.nametagenabler.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.HashMap;
import java.util.Iterator;

@IFMLLoadingPlugin.TransformerExclusions("us.skyywastaken.nametagenabler.asm")
@IFMLLoadingPlugin.MCVersion("1.8.9")
public class EntityLivingBaseTransformer implements IClassTransformer {
    private final HashMap<String, String> obfuscatedMappings;
    private final HashMap<String, String> deobfuscatedMappings;

    public EntityLivingBaseTransformer() {
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
            return transformLivingBaseClass(basicClass, deobfuscatedMappings);
        } else if(name.equals(obfuscatedMappings.get("className"))) {
            return transformLivingBaseClass(basicClass, obfuscatedMappings);
        }
        return basicClass;
    }

    private byte[] transformLivingBaseClass(byte[] classToTransform, HashMap<String, String> mappings) {
        try {
            ClassNode classNode = ASMUtils.getClassNode(classToTransform);
            replaceNameTagMethodInsn(mappings, classNode);
            return ASMUtils.getByteArrayFromClassNode(classNode);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return classToTransform;
    }

    private void replaceNameTagMethodInsn(HashMap<String, String> mappings, ClassNode classNode) {
        Iterator<MethodNode> methodNodeIterator = classNode.methods.iterator();
        while(methodNodeIterator.hasNext()){
            MethodNode currentMethod = methodNodeIterator.next();
            if(!currentMethod.name.equals(mappings.get("methodName"))){
                continue;
            }
            InsnList newInsnList = getNewInstructions(mappings);
            Iterator<AbstractInsnNode> insnIterator = currentMethod.instructions.iterator();
            while(insnIterator.hasNext()){
                AbstractInsnNode currentNode = insnIterator.next();
                if(currentNode.getOpcode() == Opcodes.ICONST_0) {
                    currentMethod.instructions.insertBefore(currentNode, newInsnList);
                    currentMethod.instructions.remove(currentNode);
                }
            }
        }
    }

    private InsnList getNewInstructions(HashMap<String, String> mappings) {
        String asmClassLocation = mappings.get("asmClassLocation");
        String hasCustomNameMethod = mappings.get("hasCustomNameMethodName");
        InsnList newInstructions = new InsnList();
        newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        newInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, asmClassLocation, hasCustomNameMethod,
                "()Z", false));
        return newInstructions;
    }
}
