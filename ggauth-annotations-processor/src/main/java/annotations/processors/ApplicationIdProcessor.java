package annotations.processors;

import annotations.RequiresApplicationId;
import com.google.auto.service.AutoService;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.Set;

import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.NOTE;

@AutoService(Processor.class)
@SupportedAnnotationTypes("annotations.RequiresApplicationId")
@SupportedSourceVersion(SourceVersion.RELEASE_22)
public class ApplicationIdProcessor extends AbstractProcessor {

    private static final String APPLICATION_ID = "application-id";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        processingEnv.getMessager().printMessage(NOTE, "Starting Application Id processor");

        for (Element element : roundEnv.getElementsAnnotatedWith(RequiresApplicationId.class)) {

            if (!(element instanceof ExecutableElement executableElement)) continue;

            boolean found = false;

            for (VariableElement parameter : executableElement.getParameters()) {
                RequestHeader header = parameter.getAnnotation(RequestHeader.class);
                if (header != null && APPLICATION_ID.equalsIgnoreCase(header.value())) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                String message =
                        "Method " + element.getSimpleName() + " annotated with @RequiresApplicationId must have"
                                + " a parameter annotated with @RequestHeader(value = \"" + APPLICATION_ID + "\")";
                processingEnv.getMessager().printMessage(ERROR, message, element);
            }
        }
        processingEnv.getMessager().printMessage(NOTE, "Application Id processor Done!");
        return true;
    }
}