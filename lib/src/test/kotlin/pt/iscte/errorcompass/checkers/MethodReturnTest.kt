package pt.iscte.errorcompass.checkers

import pt.iscte.errorcompass.ErrorCompass
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
            assertEquals("rtnTyp", it.errors.first().errorCode)
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
            assertEquals("noRtnSem", it.errors.first().errorCode)
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
            assertEquals("noRtnSem", it.errors.first().errorCode)
        }

    }
}