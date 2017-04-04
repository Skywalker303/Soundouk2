package com.Commander;

import javafx.application.Platform;

import java.io.IOException;
import java.nio.file.*;

public class WatchServiceAssist {

    private WatchService WService;
    private WatchKey WKey;
    private volatile Thread WThread;

    private ListView LView;
    private Path CurDir;

    public WatchServiceAssist(ListView listView) {
        LView = listView;
        try {
            WService = FileSystems.getDefault().newWatchService();
            WKey = LView.getDirectory().register(WService, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
            CurDir = LView.getDirectory();
        } catch (IOException e) {
            ConversAssist.showException(e);
        }
        WThread = new Thread(() -> {
            while (true) {
                try {
                    WatchKey watchKey = WService.take();
                    watchKey.pollEvents();
                    updateUI();
                    watchKey.reset();
                } catch (InterruptedException e) {
                    ConversAssist.showException(e);
                }
            }
        });
        WThread.start();
    }
    public void changeObservableDirectory(Path newDirectory) {
        if (CurDir.equals(newDirectory)) return;
        WKey.cancel();
        try {
            WKey = newDirectory.register(WService, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
            CurDir = newDirectory;
        } catch (IOException e) {
            ConversAssist.showException(e);
        }
    }
    private void updateUI() {
        Platform.runLater(() -> LView.refresh());
    }
}
