package annotations.processors;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.CompilationSubject.assertThat;

public class ApplicationIdProcessorTest {

    @Test
    public void shouldFailIfRequestHeaderMissing() {
        JavaFileObject source = JavaFileObjects.forSourceString(
                "com.example.FakeController",
                """
                package com.example;

                import annotations.RequiresApplicationId;
                import org.springframework.web.bind.annotation.GetMapping;

                public class FakeController {
                    @RequiresApplicationId
                    @GetMapping("/test")
                    public void testMethod() {
                    }
                }
                """);

        Compilation compilation =
                Compiler.javac().withProcessors(new ApplicationIdProcessor()).compile(source);

        assertThat(compilation).failed();
        assertThat(compilation)
                .hadErrorContaining(
                        "Method annotated with @RequiresApplicationId must have a parameter annotated with @RequestHeader(value = \"application-id\")");
    }

    @Test
    void shouldPassIfRequestHeaderIsPresent() {
        JavaFileObject source = JavaFileObjects.forSourceString(
                "com.example.ValidController",
                """
            package com.example;

            import annotations.RequiresApplicationId;
            import org.springframework.web.bind.annotation.GetMapping;
            import org.springframework.web.bind.annotation.RequestHeader;

            public class ValidController {
                @RequiresApplicationId
                @GetMapping("/ok")
                public void testMethod(@RequestHeader("application-id") String appId) {
                }
            }
            """);

        Compilation compilation =
                Compiler.javac().withProcessors(new ApplicationIdProcessor()).compile(source);

        assertThat(compilation).succeededWithoutWarnings();
    }
}
