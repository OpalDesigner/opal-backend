package ai.opal.backend.api;

import ai.opal.backend.model.SceneModel;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;

@Service
public class ModelService {
    HashMap<String, SceneModel> models = new HashMap<>();

    public Collection<SceneModel> getModels() {
        return models.values();
    }
}
