package pt.iscte.errorcompass

import kotlin.test.Test
import kotlin.test.assertEquals

class MethodReturnTest {
    @Test fun `Method with return but incorrect type`() {
        val javaCode = """
            public class Bar {
                String foo() {
                     return 1;
                }
            }
        """

        var errorCompasss = ErrorCompass()
        errorCompasss.run(javaCode).onSuccess {
            assertEquals(1, it.errors.count())
            assertEquals("In the method foo, a value of type int is being returned, but a value of type String was expected.", it.errors.first().description)
        }

    }

    @Test fun `Method without return but need to return`() {
        val javaCode = """
            public class Bar {
                String foo() {
                     
                }
            }
        """

        var errorCompasss = ErrorCompass()
        errorCompasss.run(javaCode).onSuccess {
            assertEquals(1, it.errors.count())
            assertEquals("In the method foo, a return value is expected, but no value is returned.", it.errors.first().description)
        }

    }

    @Test fun `Method without return in a if case`() {
        val javaCode = """
            public class Bar {
                String foo() {
                     if(true) {
                        //Nothing
                     } else {
                        return "";
                     }
                }
            }
        """

        var errorCompasss = ErrorCompass()
        errorCompasss.run(javaCode).onSuccess {
            print(it.errors)
            assertEquals(1, it.errors.count())
            assertEquals("In the method foo, a return value is expected, but no value is returned.", it.errors.first().description)
        }

    }
}