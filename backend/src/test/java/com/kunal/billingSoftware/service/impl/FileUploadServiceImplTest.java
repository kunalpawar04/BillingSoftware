package com.kunal.billingSoftware.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileUploadServiceImplTest {

    @TempDir
    Path tempDir;

    FileUploadServiceImpl svc;

    @Mock
    MultipartFile file;

    @BeforeEach
    void setup() {
        svc = new FileUploadServiceImpl(tempDir.toString());
    }

    @Test
    void constructor_createsUploadDirectory_whenNotExists() {
        Path nested = tempDir.resolve("uploads/subdir");
        assertFalse(Files.exists(nested));

        // Constructor check (using a new instance)
        FileUploadServiceImpl newSvc = new FileUploadServiceImpl(nested.toString());

        assertTrue(Files.exists(nested));
        assertTrue(Files.isDirectory(nested));
    }

    @Test
    void uploadFile_savesFileAndReturnsFilename() throws Exception {
        when(file.getOriginalFilename()).thenReturn("photo.png");

        doAnswer(invocation -> {
            File dest = invocation.getArgument(0, File.class);
            Files.createFile(dest.toPath());
            return null;
        }).when(file).transferTo(any(File.class));

        String returned = svc.uploadFile(file);
        assertNotNull(returned);
        assertTrue(returned.endsWith(".png"));

        verify(file, times(1)).transferTo(any(File.class));
        assertTrue(Files.exists(tempDir.resolve(returned)));
    }

    @Test
    void uploadFile_throwsRuntimeException_whenTransferFails() throws Exception {
        when(file.getOriginalFilename()).thenReturn("error.txt");
        doThrow(new IOException("disk error")).when(file).transferTo(any(File.class));

        assertThrows(RuntimeException.class, () -> svc.uploadFile(file));
    }

    @Test
    void deleteFile_deletesExistingFileAndReturnsTrue() throws Exception {
        Path f = tempDir.resolve("toDelete.jpg");
        Files.createFile(f);

        boolean result = svc.deleteFile("toDelete.jpg");
        assertTrue(result);
        assertFalse(Files.exists(f));
    }

    @Test
    void deleteFile_returnsFalseWhenFileMissing() {
        boolean result = svc.deleteFile("missing.png");
        assertFalse(result);
    }
}