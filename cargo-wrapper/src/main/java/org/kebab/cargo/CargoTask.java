package org.kebab.cargo;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputFiles;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CargoTask extends DefaultTask {

    private String cargoCommand;
    private List<String> args;
    private Map<String, String> environment;

    private File workingDir;

    private List<File> outputFiles;

    public void configure(CargoExtension config) {
        Project project = getProject();

        if (config.cargoCommand != null && config.cargoCommand.isEmpty()) {
            throw new GradleException("Cargo command cannot be empty");
        }
        this.cargoCommand = config.cargoCommand == null ? "cargo" : config.cargoCommand;

        this.args = new ArrayList<>();
        if (config.toolchain != null) {
            String toolchain = config.toolchain.startsWith("+") ? config.toolchain.substring(1) : config.toolchain;
            if (toolchain.isEmpty()) {
                throw new GradleException("Toolchain cannot be empty");
            }
            this.args.add("+" + toolchain);
        }
        this.args.add("build");
        if (!"debug".equals(config.profile)) {
            this.args.add("--release");
        }
        this.args.addAll(config.arguments);

        this.environment = new ConcurrentHashMap<>(config.environment);

        this.workingDir = config.crate != null ? project.file(config.crate) : project.getProjectDir();
        File targetDir = new File(this.workingDir, "target");

        this.outputFiles = config.outputs.entrySet().stream().map(output ->
                new File(targetDir, (output.getKey().isEmpty() ? "" : output.getKey() + File.separator) + config.profile + File.separator + output.getValue())
        ).collect(Collectors.toList());

        this.outputFiles.forEach(file -> {
            String fileName = file.getName();
            if (!config.targets.containsKey(fileName)) return;
            File target = new File(config.targets.get(fileName), fileName);
            if (target.exists()) {
                if (!target.delete()) throw new GradleException("Cannot delete old resource file " + target.getPath());
            }
            try {
                Files.copy(file.toPath(), target.toPath());
            } catch (Exception exception) {
                throw new GradleException("Cannot copy to target directories", exception);
            }
        });

        if (this.outputFiles.isEmpty()) {
            throw new GradleException("At least one output must be specified.");
        }
    }

    @TaskAction
    public void cargoBuild() {
        Project project = getProject();
        project.exec(spec -> {
            spec.commandLine(this.cargoCommand);
            spec.args(args);
            spec.workingDir(workingDir);
            spec.environment(environment);
        }).assertNormalExitValue();
    }

    @InputDirectory
    public File getWorkingDir() {
        return this.workingDir;
    }

    @OutputFiles
    public List<File> getOutputFiles() {
        return this.outputFiles;
    }
}
