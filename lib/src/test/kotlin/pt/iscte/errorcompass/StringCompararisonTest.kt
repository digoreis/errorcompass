package pt.iscte.errorcompass

import kotlin.test.Test
import kotlin.test.assertEquals

class StringCompararisonTest {
    @Test fun `Compare a object string with a literal string`() {
        val javaCode = """
            public class Main {
                public static void main(String[] args) {
                    String x = "Hello";
                    if(x == "Hello") {
                        System.out.println(x);
                    }
                }
            }
        """

        var errorCompasss = ErrorCompass()
        errorCompasss.run(javaCode).onSuccess {
            println(it.errors)
            assertEquals(it.errors.count(), 1)
            assertEquals(it.errors.first().description, "Potential string comparison with ==  detected")
        }

    }
}