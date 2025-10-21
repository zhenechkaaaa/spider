package ru.nsu.odnostorontseva.spider;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        String url = "http://localhost:8080";

        Spider spider = new Spider(url);
        spider.start("/");
        spider.printer();
    }
}