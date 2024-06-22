package pt.iscte.errorcompass.checkers

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import pt.iscte.errorcompass.ErrorCompass
import pt.iscte.errorcompass.model.Location
import kotlin.test.assertEquals

class ControlStructureTest {
    @ParameterizedTest(name = "Test structure statement like if/while/for")
    @ValueSource(strings = [Companion.ifJavaCode, Companion.whileJavaCode, Companion.forJavaCode])
    fun `Test structure statement`(code: String) {

        var errorCompasss = ErrorCompass()
        errorCompasss.run(code).onSuccess {
            assertEquals(1, it.errors.count())
            assertEquals("crtStr", it.errors.first().errorCode)
            assertEquals(3, it.errors.first().errorLocation.line)
        }

    }

    companion object {
        const val ifJavaCode = """
                public class Main {
                    public static void main(String[] args) {
                         if(true); {
                        
                        }
                    }
                }
            """
        const val whileJavaCode = """
            public class Main {
                public static void main(String[] args) {
                     while(true); {
                    
                    }
                }
            }
        """

        const val forJavaCode = """
            public class Main {
                public static void main(String[] args) {
                     for(int i = 0; i < 10; i++); {
                    
                    }
                }
            }
        """
    }
}