package pt.iscte.errorcompass.checkers

import pt.iscte.errorcompass.ErrorCompass
import kotlin.test.Test
import kotlin.test.assertEquals

class ControStructureTest {
    @Test fun `Test structure if statement`() {
        val javaCode = """
            public class Main {
                public static void main(String[] args) {
                     if(true); {
                    
                    }
                }
            }
        """

        var errorCompasss = ErrorCompass()
        errorCompasss.run(javaCode).onSuccess {
            assertEquals(1, it.errors.count())
            assertEquals("In the control structure if statement, a ; is present instead of a {. After the condition, the start of a code block is expected.", it.errors.first().description)
        }

    }

    @Test fun `Test structure while statement`() {
        val javaCode = """
            public class Main {
                public static void main(String[] args) {
                     while(true); {
                    
                    }
                }
            }
        """

        var errorCompasss = ErrorCompass()
        errorCompasss.run(javaCode).onSuccess {
            assertEquals(1, it.errors.count())
            assertEquals("In the control structure while statement, a ; is present instead of a {. After the condition, the start of a code block is expected.", it.errors.first().description)
        }

    }

    @Test fun `Test structure for statement`() {
        val javaCode = """
            public class Main {
                public static void main(String[] args) {
                     for(int i = 0; i < 10; i++); {
                    
                    }
                }
            }
        """

        var errorCompasss = ErrorCompass()
        errorCompasss.run(javaCode).onSuccess {
            assertEquals(1, it.errors.count())
            assertEquals("In the control structure for statement, a ; is present instead of a {. After the condition, the start of a code block is expected.", it.errors.first().description)
        }

    }
}