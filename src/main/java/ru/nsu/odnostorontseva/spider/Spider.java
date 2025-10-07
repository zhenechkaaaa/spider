package ru.nsu.odnostorontseva.spider;

import lombok.AllArgsConstructor;

import java.lang.Thread;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class Spider {
    private final String url;
    private final List<String> messages = Collections.synchronizedList(new ArrayList<>());

    public void start(String path) throws InterruptedException {
        Thread root = recursive(path);
        root.join();
    }

    private Thread recursive(String path) {
        return Thread.ofVirtual()
                .name("Walker" + path)
                .start(() -> {
                    try {
                        String fullUrl = url + (path.startsWith("/") ? path : "/" + path);
                        Response message = Work.get(fullUrl);

                        if (message != null) {
                            messages.add(message.getMessage());
                            List<Thread> child = new ArrayList<>();
                            for (String childPath : message.getSuccessors()) {
                                Thread childThread = recursive(childPath);
                                child.add(childThread);
                            }

                            for (Thread childThread : child) {
                                childThread.join();
                            }
                        }

                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public void printer() {
        messages.stream().sorted().forEach(System.out::println);
        System.out.println(messages.size());
    }
}
