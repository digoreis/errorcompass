package main

import org.junit.jupiter.api.assertThrows
import pt.iscte.errorcompass.ErrorCompass
import pt.iscte.errorcompass.errors.ParseError
import kotlin.test.Test
import kotlin.test.assertEquals

class ErrorCompassTest {
    @Test
    fun `Test structure if statement`() {
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
            assertEquals(
                "crtStr",
                it.errors.first().errorCode
            )
        }

    }

    @Test
    fun `Test error in JavaParser`() {
        val javaCode = """
            public class Main {
                public static void main(String[] args) {
                     if(true) {
                    // No close } 
                }
            }
        """

        var errorCompasss = ErrorCompass()
        errorCompasss.run(javaCode).onSuccess {
            assertEquals(1, it.errors.count())
            assertEquals(
                "javCmp",
                it.errors.first().errorCode
            )
        }
    }
}