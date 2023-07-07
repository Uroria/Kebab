package de.verklickt.cargowrapper;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputFiles;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CargoTask extends DefaultTask {

    private String cargoCommand;
    private List<String> args;
    private Map<String, String> environment;

    private File workingDir;

    private List<File> outputFiles;

    public void configure(CargoExtension config) {
        Project project = getProject();

        if (config.cargoCommand == null || config.cargoCommand.isEmpty()) config.cargoCommand = "cargo";

        this.cargoCommand = config.cargoCommand;

        this.args = new ArrayList<>();

        if (config.toolchain != null) {
            String toolchain = config.toolchain.startsWith("+") ? config.toolchain.substring(1) : config.toolchain;
            if (toolchain.isEmpty()) throw new GradleException("Toolchain cannot be empty");
            this.args.add("+" + toolchain);
        }

        this.args.add("build");

        if (!"debug".equals(config.profile)) {
            this.args.add("--release");
        }

        this.args.addAll(config.commandArguments);

        this.environment = new ConcurrentHashMap<>(config.environment);

        this.workingDir = config.crate != null ? project.file(config.crate) : project.getProjectDir();

        File buildTargetDirectory = new File(this.workingDir, "target");

        this.outputFiles = new ArrayList<>();

        config.outputs.forEach((target, build) -> {
            File buildFile = new File(buildTargetDirectory, File.separator + config.profile + File.separator + target);
            File targetFile = new File(build, target);

            if (!buildFile.exists()) return;

            this.outputFiles.add(buildFile);

            if (targetFile.exists()) {
                if (!targetFile.delete()) throw new GradleException("Cannot delete old target file " + targetFile.getPath());
            }

            try {
                Files.copy(buildFile.toPath(), targetFile.toPath());
            } catch (Exception exception) {
                throw new GradleException("Cannot copy build file to target", exception);
            }
        });
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
