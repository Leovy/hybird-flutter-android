package com.nikeorever.gradle

import com.android.SdkConstants
import com.android.build.api.transform.*
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.internal.pipeline.IntermediateFolderUtils
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.objectweb.asm.*
import org.objectweb.asm.ClassReader.EXPAND_FRAMES
import org.objectweb.asm.ClassWriter.COMPUTE_MAXS
import java.io.File
import java.io.FileOutputStream

class NikeoTransformerPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.all {
            when (it) {
                is AppPlugin -> {
                    (it.extension as AppExtension).registerTransform(NikeoTransformer())
                }
                is LibraryPlugin -> {
                    (it.extension as LibraryExtension).registerTransform(NikeoTransformer())
                }
            }
        }
    }
}

private class NikeoTransformer: Transform() {
    override fun getName(): String = NikeoTransformer::class.simpleName.toString()

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun isIncremental(): Boolean = false

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun transform(invocation: TransformInvocation?) {
        if (invocation == null) {
            return
        }
        val context = invocation.context
        val inputs = invocation.inputs
        val outputProvider = invocation.outputProvider
        val referencedInputs = invocation.referencedInputs
        val secondaryInputs = invocation.secondaryInputs

        println("\n------------------------------------------------------\n")

        print("""
            context: $context,
            inputs(size: ${inputs.size}): $inputs,
            outputProvider: $outputProvider,
            referencedInputs: $referencedInputs,
            secondaryInputs: $secondaryInputs,
        """.trimIndent())

        println("\n------------------------------------------------------\n")

        inputs.forEach { input ->
            val jarInputs = input.jarInputs
            val directoryInputs = input.directoryInputs

//            println("jarInputs: \n" + jarInputs.joinToString("\n") { "${it.name} (${it.file})" })
//            println("directoryInputs: \n" + directoryInputs.joinToString("\n") { "${it.name} (${it.file})" })

            jarInputs.forEach {
                var jarName = it.name
                val md5Name = DigestUtils.md5Hex(it.file.absolutePath)
                if (jarName.endsWith(SdkConstants.DOT_JAR)) {
                   jarName = jarName.substring(0, jarName.length -4)
                }
                val dest = outputProvider.getContentLocation(
                    jarName + md5Name,
                    it.contentTypes,
                    it.scopes,
                    Format.JAR
                )
                FileUtils.copyFile(it.file, dest)
            }

            directoryInputs.forEach {

                childFileOf(it.file) { classFile ->
                    if (classFile.name == "Processor.class") {
                        println("Found Processor in DirectoryInput: $classFile")
                        val classReader = ClassReader(classFile.inputStream())
                        val classWriter = ClassWriter(COMPUTE_MAXS)

                        classReader.accept(MyClassVisitor(classWriter), EXPAND_FRAMES)
                        val byteArr = classWriter.toByteArray()
                        val fileOutputStream = FileOutputStream(classFile)
                        fileOutputStream.write(byteArr)
                        fileOutputStream.flush()
                        fileOutputStream.close()
                    }
                }

                val dest = outputProvider.getContentLocation(
                    it.name,
                    it.contentTypes,
                    it.scopes,
                    Format.DIRECTORY
                )

                FileUtils.copyDirectory(it.file, dest)
            }
        }

        println("\n------------------------------------------------------\n")

        val forName =
            Class.forName("com.android.build.gradle.internal.pipeline.TransformOutputProviderImpl")
        val declaredField = forName.getDeclaredField("folderUtils")
        declaredField.isAccessible = true
        val intermediateFolderUtils = declaredField.get(outputProvider) as IntermediateFolderUtils
        println("""
            TransformOutputProvider: 
            rootFolder: ${intermediateFolderUtils.rootFolder}
        """.trimIndent())

        println("\n------------------------------------------------------\n")

    }

    private class MyClassVisitor(classWriter: ClassWriter): ClassVisitor(Opcodes.ASM6, classWriter) {
        override fun visitMethod(
            access: Int,
            name: String?,
            desc: String?,
            signature: String?,
            exceptions: Array<out String>?
        ): MethodVisitor {
            val mv = super.visitMethod(access, name, desc, signature, exceptions)
            if (name == "<init>") {
                return mv
            }
            return MyMethodVisitor(mv)
        }

        private class MyMethodVisitor(mv: MethodVisitor): MethodVisitor(Opcodes.ASM6, mv) {
            override fun visitCode() {
                super.visitCode()
                mv.visitLdcInsn("FlutterDroid")
                mv.visitLdcInsn("[From Transformer]start")
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "i", "(Ljava/lang/String;Ljava/lang/String;)I", false)
            }

            override fun visitInsn(opcode: Int) {
                if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) || opcode == Opcodes.ATHROW) {
                    mv.visitLdcInsn("FlutterDroid")
                    mv.visitLdcInsn("[From Transformer]end")
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "i", "(Ljava/lang/String;Ljava/lang/String;)I", false)
                }
                mv.visitInsn(opcode)
            }
        }
    }

    private fun childFileOf(file: File, consumer: (File) -> Unit) {
        if (file.isDirectory) {
            file.listFiles()?.forEach {
                childFileOf(it, consumer)
            }
        } else if (file.isFile) {
            consumer(file)
        }
    }
}