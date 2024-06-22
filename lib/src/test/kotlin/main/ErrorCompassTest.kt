package main

import pt.iscte.errorcompass.ErrorCompass
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
}