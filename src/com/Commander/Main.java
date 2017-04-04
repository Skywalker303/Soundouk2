package com.Commander;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main extends Application {


    private static final KeyCombination NEW_FILE = new KeyCodeCombination(KeyCode.N,
            KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination NEW_DIRECTORY = new KeyCodeCombination(KeyCode.N,
            KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN);
    private static final KeyCombination COPY = new KeyCodeCombination(KeyCode.F2);
    private static final KeyCombination MOVE = new KeyCodeCombination(KeyCode.F3);
    private static final KeyCombination DELETE = new KeyCodeCombination(KeyCode.DELETE);
    private static final KeyCombination RENAME = new KeyCodeCombination(KeyCode.F4);
    private static final KeyCombination FOCUS_TEXT_FIELD = new KeyCodeCombination(KeyCode.D, KeyCombination.SHIFT_DOWN);

    private FileView FView;


    private MenuBar getMenuBar() {
        Menu fileMenu = new Menu("File");

        // Create file menu
        MenuItem newFile = new MenuItem("New File");
        newFile.setOnAction(e -> FView.createFile());
        newFile.setAccelerator(NEW_FILE);

        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(e -> FView.delete());
        deleteItem.setAccelerator(DELETE);

        MenuItem newFolder = new MenuItem("New Folder ");
        newFolder.setOnAction(e -> FView.createDirectory());
        newFolder.setAccelerator(NEW_DIRECTORY);

        MenuItem renameItem = new MenuItem("Rename");
        renameItem.setOnAction(e -> FView.rename());
        renameItem.setAccelerator(RENAME);

        fileMenu.getItems().addAll(newFile, newFolder, renameItem, deleteItem);


        return new MenuBar(fileMenu);
    }

    private ToolBar getToolBar() {

        Label labelCopy = new Label("F2 Copy");
        labelCopy.setOnMouseClicked(e -> FView.copy());

        Label labelMove = new Label("F3 Move");
        labelMove.setOnMouseClicked(e -> FView.move());

        return new ToolBar(labelCopy, new Separator(), labelMove);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox root = new VBox();

        FView = new FileView();

        VBox.setVgrow(FView, Priority.ALWAYS);

        root.getChildren().addAll(getMenuBar(), FView, getToolBar());

        Scene scene = new Scene(root, 900, 650);

        scene.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
            if (DELETE.match(e)) {
                FView.delete();
            } else if (NEW_FILE.match(e)) {
                FView.createFile();
            } else if (NEW_DIRECTORY.match(e)) {
                FView.createDirectory();
            } else if (RENAME.match(e)) {
                FView.rename();
            } else if (COPY.match(e)) {
                FView.copy();
            } else if (MOVE.match(e)) {
                FView.move();
            } else if (FOCUS_TEXT_FIELD.match(e)) {
                FView.focusTextField();
            }
        });
        primaryStage.setTitle("Commander");
        primaryStage.setScene(scene);

        primaryStage.show();
    }
}
