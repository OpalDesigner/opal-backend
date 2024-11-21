package ai.opal.backend.api;

import ai.opal.backend.model.SceneModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ModelsController {
    private final ModelService modelService;
    private final R2DownloadService r2DownloadService;

    public ModelsController(ModelService modelService, R2DownloadService r2DownloadService) {
        this.modelService = modelService;
        this.r2DownloadService = r2DownloadService;
    }

    @GetMapping("/v1/models/#{id}")
    public SceneModel getProperty(@PathVariable String id) {
        SceneModel model = modelService.models.get(id);

        if (model == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return model;
    };

    @GetMapping("/v1/models")
    public String getModels() {
        return "hello world";
    }
}
