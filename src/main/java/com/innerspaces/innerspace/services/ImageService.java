package com.innerspaces.innerspace.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class ImageService {

    @Autowired
    private ResourceLoader resourceLoader;

    public byte[] getImage(String imageName) throws IOException {
        // Load resource using the resource loader
        Resource resource = resourceLoader.getResource("classpath:static/images/" + imageName);

        // Read the file content into a byte array and return
        Path imagePath = resource.getFile().toPath();
        return Files.readAllBytes(imagePath);
    }
}
