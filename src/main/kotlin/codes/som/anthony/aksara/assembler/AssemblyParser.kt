package codes.som.anthony.aksara.assembler

import codes.som.anthony.aksara.assembler.conversion.toAST
import codes.som.anthony.aksara.assembler.parser.AksaraLexer
import codes.som.anthony.aksara.assembler.parser.AksaraParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.objectweb.asm.ClassWriter
import java.nio.file.Files
import java.nio.file.Paths

fun main() {
    val lexer = AksaraLexer(CharStreams.fromString("""
        import java.lang.System
        import java.lang.String
        import java.io.PrintStream
        
        public com/example/HelloWorld {
            private static message String
        
            public static main void(String[]) {
                new Random
                dup
                invokespecial Random <init> void()
                invokevirtual Random nextBoolean bool()
                ifeq L0
                getstatic java.lang.System out PrintStream
                ldc "You got lucky!"
                invokevirtual PrintStream println void(String)

              L0:
                getstatic java.lang.System out PrintStream
                ldc "Hello, world!"
                invokevirtual PrintStream println void(String)
                return
            }
        }
    """.trimIndent()))

    val parser = AksaraParser(CommonTokenStream(lexer))
    val file = parser.aksaraFile()
    val prog = file.toAST()

    for (node in assembleAST(prog)) {
        val classBuffer = ClassWriter(ClassWriter.COMPUTE_FRAMES).let {
            node.accept(it)
            it.toByteArray()
        }

        val path = Paths.get("out/${node.name}.class")
        Files.createDirectories(path.parent)
        Files.write(path, classBuffer)
    }
}
