package pt.iscte.errorcompass.checkers

import pt.iscte.errorcompass.ErrorCompass
import kotlin.test.Test
import kotlin.test.assertEquals

class UninitializedVariableTest {
    @Test fun `UninitializedVariable test`() {
        val javaCode = """
            public class Main {
                public static void main(String[] args) {
                    String s;
                    
                    System.out.println(s);
                }
            }
        """

        var errorCompasss = ErrorCompass()
        errorCompasss.run(javaCode).onSuccess {
            assertEquals(1, it.errors.count())
            assertEquals(it.errors.first().description, "The variable s of type java.lang.string is declared but never used.")
        }
    }
}