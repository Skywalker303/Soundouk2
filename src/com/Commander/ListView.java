package com.Commander;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;

import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListView extends javafx.scene.control.ListView<String> {

    private File Dir;
    private TextField TField;
    private ObservableList<String> ChList;

    private WatchServiceAssist WSHelper;

    public ListView(String path) {
        super();
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        Dir = new File(path);

        ChList = FXCollections.observableArrayList();
        setItems(ChList);

        TField = new TextField();
        TField.setStyle("-fx-font-size: 15px;");

        setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case ENTER:
                    if (isFocused()) navigate(getSelectionModel().getSelectedItem());
                    break;
                case BACK_SPACE:
                    back();
                    break;
            }
        });

        setOnMouseClicked(m -> {
            if (m.getButton().equals(MouseButton.PRIMARY) && m.getClickCount() == 2)
                navigate(getSelectionModel().getSelectedItem());
        });

        WSHelper = new WatchServiceAssist(this);
        refresh();
    }

    public void refresh() {
        showList(getCurrentFilesList());
        TField.setText(Dir.getAbsolutePath());
        WSHelper.changeObservableDirectory(Dir.toPath());
    }

    public TextField getTextField() {
        return TField;
    }

    public List<Path> getSelection() {
        List<Path> selection = new ArrayList<>();
        for (String item : getSelectionModel().getSelectedItems()) {
            selection.add(Dir.toPath().resolve(item));
        }
        return selection;
    }

    public Path getDirectory() {
        return Dir.toPath();
    }

    public void select(String regex) {
        if (regex.startsWith("*")) regex = "." + regex;
        getSelectionModel().clearSelection();
        for (int i = 0; i < ChList.size(); ++i) {
            String item = ChList.get(i);
            if (item.matches(regex) || StringAssist.containsWord(item, regex)) {
                getSelectionModel().select(i);
            }
        }
    }

    private String[] getCurrentFilesList() {
        File[] listFiles = Dir.listFiles(file -> !file.isHidden());

        if (listFiles == null) {
            listFiles = new File[0];
        }

        Arrays.sort(listFiles, (f1, f2) -> {
            if ((f1.isDirectory() && f2.isDirectory()) || (f1.isFile() && f2.isFile())) {
                return f1.compareTo(f2);
            }
            return f1.isDirectory() ? -1 : 1;
        });

        String[] list = new String[listFiles.length];
        for (int i = 0; i < list.length; ++i) {
            list[i] = listFiles[i].getName();
        }

        return list;
    }

    private void showList(String[] list) {
        if (list != null) {
            ChList.clear();
            ChList.addAll(list);
        } else {
            ChList.clear();
        }
    }

    public void openFile(File file) {
        if (!file.exists()) {
            refresh();
            return;
        }
        if (file.isDirectory()) {
            Dir = file;
            refresh();
        } else if (file.isFile()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (Exception e) {
                ConversAssist.showException(e);
            }
        }
    }

    private void navigate(String name) {
        String selectedPath = Dir.getAbsolutePath() + File.separator + name;
        File selectedFile = new File(selectedPath);
        if (selectedFile.isDirectory()) {
            Dir = selectedFile;
            refresh();
        } else {
            try {
                Desktop.getDesktop().open(selectedFile);
            } catch (Exception e) {
                ConversAssist.showException(e);
            }
        }
    }

    private void back() {
        File parent = Dir.getParentFile();
        if (parent != null) {
            Dir = parent;
            if (Dir.exists()) {
                refresh();
            } else {
                back();
            }
        }
    }

}
