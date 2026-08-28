package cl.thoorcito.nebulastore;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@Tag(name = "Health", description = "Verifica que la API este funcionando")
public class HealthController {

    @GetMapping("/healthcheck")
    public String healthcheck() {
        return "NebulaStore API is running";
    }
}