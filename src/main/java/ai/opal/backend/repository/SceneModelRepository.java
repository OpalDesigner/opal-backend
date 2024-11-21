package ai.opal.backend.repository;

import ai.opal.backend.model.SceneModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SceneModelRepository extends JpaRepository<SceneModel, Long> {
    SceneModel findByName(String name);
}