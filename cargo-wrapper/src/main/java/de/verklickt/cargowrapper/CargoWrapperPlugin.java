package de.verklickt.cargowrapper;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;

public final class CargoWrapperPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getConfigurations().create("cargo-wrapper");
        CargoExtension extension = project.getExtensions().create(CargoExtension.EXTENSION_NAME, CargoExtension.class);
        TaskProvider<CargoTask> buildTask = project.getTasks().register(CargoExtension.BUILD_TASK_NAME, CargoTask.class);
        project.afterEvaluate(project1 -> {
            buildTask.get().configure(extension);
            project1.getArtifacts().add(CargoExtension.CONFIGURATION_NAME, buildTask);
        });
    }
}
