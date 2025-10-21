package ru.nsu.odnostorontseva.spider;

import lombok.AllArgsConstructor;

import java.lang.Thread;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
public class Spider {
    private final String url;
    private final List<String> messages = Collections.synchronizedList(new ArrayList<>());

    private final AtomicInteger counter = new AtomicInteger(0);
    private final Object lock = new Object();

    public void start(String path) throws InterruptedException {
        counter.incrementAndGet();
        spawn(path);

        synchronized (lock) {
            while (counter.get() > 0) {
                lock.wait();
            }
        }
    }

    private void spawn(String path) {
        Thread.ofVirtual()
                .name("Walker" + path)
                .start(() -> {
                    try {
                        String fullUrl = url + (path.startsWith("/") ? path : "/" + path);
                        Response message = Work.get(fullUrl);

                        if (message != null) {
                            messages.add(message.getMessage());

                            for (String child: message.getSuccessors()) {
                                counter.incrementAndGet();
                                spawn(child);
                            }
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());;
                    } finally {
                        int tmp = counter.decrementAndGet();
                        if (tmp == 0) {
                            synchronized (lock) {
                                lock.notifyAll();
                            }
                        }
                    }
                });
    }

    public void printer() {
        messages.stream().sorted().forEach(System.out::println);
        System.out.println(messages.size());
    }
}
