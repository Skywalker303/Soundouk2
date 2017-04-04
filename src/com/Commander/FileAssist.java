package com.Commander;

import javafx.scene.control.Alert;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileAssist {

    public static void createDirectory(Path address) {
        String title = address.toString();
        String name = ConversAssist.showTID(title, null, "New Directory", " Directory");
        if (name != null) {
            Path path = address.resolve(name);
            try {
                Files.createDirectory(path);
            } catch (FileAlreadyExistsException e) {
                ConversAssist.showAlert(Alert.AlertType.INFORMATION, title, "Directory already exists", path.toString());
            } catch (Exception e) {
                ConversAssist.showAlert(Alert.AlertType.INFORMATION, title, "Directory was not created", path.toString());
            }
        }
    }

    public static void createFile(Path address) {
        String title = address.toString();
        String name = ConversAssist.showTID(title, null, "New File", "Text File.txt");
        if (name != null) {
            Path path =address.resolve(name);
            try {
                Files.createFile(path);
            } catch (FileAlreadyExistsException e) {
                ConversAssist.showAlert(Alert.AlertType.INFORMATION, title, "File already exists", path.toString());
            } catch (Exception e) {
                ConversAssist.showAlert(Alert.AlertType.ERROR, title, "File was not created", path.toString());
            }
        }
    }

    public static void move(List<Path> source, Path TarDirectory) {
        List<Path> unmovable = new ArrayList<>();
        for (Path path : source) {
            try {
                FileUtils.moveToDirectory(path.toFile(), TarDirectory.toFile(), false);
            } catch (Exception e) {
                unmovable.add(path);
            }
        }
        if (unmovable.size() > 0) {
            String sourceDirectory = unmovable.get(0).getParent().toString();
            String content = "";
            for (Path path : unmovable) {
                content += path.toString() + System.lineSeparator();
            }
            String message = "Some files were not moved properly";
            ConversAssist.showAlert(Alert.AlertType.INFORMATION, sourceDirectory, message, content);
        }
    }

    public static void copy(List<Path> source, Path target) {
        List<Path> uncopiable = new ArrayList<>();
        for (Path path : source) {
            try {
                File sourceFile = path.toFile();
                if (sourceFile.isDirectory()) {
                    FileUtils.copyDirectoryToDirectory(sourceFile, target.toFile());
                } else {
                    FileUtils.copyFileToDirectory(sourceFile, target.toFile());
                }
            } catch (Exception e) {
                uncopiable.add(path);
            }
        }
        if (uncopiable.size() > 0) {
            String sourceDirectory = uncopiable.get(0).getParent().toString();
            String content = "";
            for (Path path : uncopiable) {
                content += path.toString() + System.lineSeparator();
            }
            String message = "Some files were not copied properly";
            ConversAssist.showAlert(Alert.AlertType.INFORMATION, sourceDirectory, message, content);
        }
    }



    public static void delete(List<Path> source) {
        String sourceDirectory = source.get(0).getParent().toString();

        String filesToDelete = "";
        for (Path path : source) filesToDelete += path.toString() + System.lineSeparator();
        boolean isConfirmed = ConversAssist.showExpConfDialog(sourceDirectory, "Delete",
                "Do you really want to delete selected files?", filesToDelete);

        if (isConfirmed) {
            List<Path> undeleted = new ArrayList<>();
            for (Path path : source) {
                try {
                    if (path.toFile().isDirectory()) {
                        FileUtils.deleteDirectory(path.toFile());
                    } else {
                        FileUtils.forceDelete(path.toFile());
                    }
                } catch (Exception e) {
                    undeleted.add(path);
                }
            }
            if (undeleted.size() > 0) {
                String content = "";
                for (Path path : undeleted) content += path.toString() + System.lineSeparator();
                String message = "Some files were not deleted";
                ConversAssist.showAlert(Alert.AlertType.INFORMATION, sourceDirectory, message, content);
            }
        }
    }



    public static void rename(Path source) {
        String title = "Rename";
        String name = ConversAssist.showTID(title, null, "Enter New Name", source.getFileName().toString());
        if (name != null) {
            Path target = source.getParent().resolve(name);
            try {
                FileUtils.moveToDirectory(source.toFile(), target.toFile(), true);
            } catch (Exception e) {
                ConversAssist.showAlert(Alert.AlertType.INFORMATION, source.getParent().toString(), "File was not renamed",
                        source.toString());
            }
        }
    }
}
