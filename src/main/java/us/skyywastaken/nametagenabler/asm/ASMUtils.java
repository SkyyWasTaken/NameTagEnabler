package us.skyywastaken.nametagenabler.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public class ASMUtils {
    public static ClassNode getClassNode(byte[] passedClassBytes) {
        ClassReader classReader = new ClassReader(passedClassBytes);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);
        return classNode;
    }

    public static byte[] getByteArrayFromClassNode(ClassNode passedClassNode) {
        ClassWriter classWriter = new ClassWriter(0);
        passedClassNode.accept(classWriter);
        return classWriter.toByteArray();
    }
}
