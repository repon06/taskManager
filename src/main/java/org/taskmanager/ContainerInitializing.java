package org.taskmanager;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.apache.commons.compress.utils.FileNameUtils;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class ContainerInitializing {
    private GenericContainer<?> todoAppContainer;

    public void initialize(String imagePath, int containerPort, int hostPort) throws IOException, InterruptedException {
        String imageName = loadDockerImage(imagePath);
        startContainer(imageName, containerPort, hostPort);
    }

    private String loadDockerImage(String imagePath) throws IOException, InterruptedException {
        File imageFile = new File(imagePath);
        if (!imageFile.exists() || !imageFile.isFile()) {
            throw new IllegalArgumentException("File not found or invalid: " + imagePath);
        }

        ProcessBuilder processBuilder = new ProcessBuilder("docker", "load", "--input", imagePath);
        Process process = processBuilder.start();

        boolean exitCode = process.waitFor(10, TimeUnit.SECONDS);
        if (!exitCode) {
            throw new RuntimeException("Failed to load Docker image: " + imagePath);
        }

        System.out.println("Docker image loaded successfully: " + imagePath);
        return FileNameUtils.getBaseName(imagePath);
    }

    private void startContainer(String imageName, int containerPort, int hostPort) {
        todoAppContainer = new GenericContainer<>(DockerImageName.parse(imageName))
                .withExposedPorts(containerPort)
                .withCreateContainerCmdModifier(cmd ->
                        cmd.getHostConfig().withPortBindings(
                                new PortBinding(Ports.Binding.bindPort(hostPort), new ExposedPort(containerPort))
                        )
                );

        todoAppContainer.start();
        System.out.println("Container started successfully on host port: " + hostPort);
    }

    public Integer getFirstMappedPort() {
        if (isRunning()) {
            return todoAppContainer.getFirstMappedPort();
        } else {
            throw new IllegalStateException("Container is not running or not initialized");
        }
    }

    public boolean isRunning() {
        return todoAppContainer != null && todoAppContainer.isRunning();
    }

    public void stopContainer() {
        if (todoAppContainer != null) {
            todoAppContainer.stop();
        }
    }
}