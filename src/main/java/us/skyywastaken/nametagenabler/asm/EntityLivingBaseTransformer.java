package us.skyywastaken.nametagenabler.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.HashMap;
import java.util.Iterator;

@IFMLLoadingPlugin.TransformerExclusions("us.skyywastaken.nametagenabler.asm")
@IFMLLoadingPlugin.MCVersion("1.12.2")
public class EntityLivingBaseTransformer implements IClassTransformer {
    private final HashMap<String, String> obfuscatedMappings;
    private final HashMap<String, String> deobfuscatedMappings;

    public EntityLivingBaseTransformer() {
        obfuscatedMappings = new HashMap<>();
        obfuscatedMappings.put("className", "vp");
        obfuscatedMappings.put("methodName", "bs");
        obfuscatedMappings.put("asmClassLocation", "vp");
        obfuscatedMappings.put("hasCustomNameMethodName", "n_");

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
            System.out.println("Found method!");
            Iterator<AbstractInsnNode> insnIterator = currentMethod.instructions.iterator();
            while(insnIterator.hasNext()) {
                AbstractInsnNode currentNode = insnIterator.next();
                if (currentNode.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                    ((MethodInsnNode) currentNode).name = mappings.get("hasCustomNameMethodName");
                }
            }
        }
    }
}
