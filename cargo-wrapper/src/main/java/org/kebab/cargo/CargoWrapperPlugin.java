package org.kebab.cargo;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;

@SuppressWarnings("unused")
public class CargoWrapperPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getConfigurations().create("cargo-wrapper");
        CargoExtension extension = project.getExtensions().create("cargo", CargoExtension.class);
        TaskProvider<CargoTask> buildTask = project.getTasks().register("cargoBuild", CargoTask.class);
        project.afterEvaluate(project2 -> {
            buildTask.get().configure(extension);
            project2.getArtifacts().add("cargo-wrapper", buildTask);
        });
    }
}
