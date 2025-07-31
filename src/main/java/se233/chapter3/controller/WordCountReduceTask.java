package se233.chapter3.controller;

import se233.chapter3.model.FileFreq;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class WordCountReduceTask implements Callable<LinkedHashMap<String, List<FileFreq>>> {
    private Map<String, FileFreq>[] wordMap;
    public WordCountReduceTask(Map<String, FileFreq>[] wordMap) {
        this.wordMap = wordMap;
    }

    @Override
    public LinkedHashMap<String, List<FileFreq>> call() throws Exception {
        LinkedHashMap<String, List<FileFreq>> uniqueSets;
        List<Map<String, FileFreq>> wordMapList = new ArrayList<>(Arrays.asList(wordMap));
        uniqueSets = wordMapList.stream()
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.groupingBy(
                        e -> e.getKey(),
                        Collector.of(
                                () -> new ArrayList<FileFreq>(),
                                (list, item) -> list.add(item.getValue()),
                                (currentList, newItems) -> {
                                    currentList.addAll(newItems);
                                    return currentList; })
                ))
                .entrySet()
                .stream()
                .sorted((entry1, entry2) -> {
            int totalFreq1 = entry1.getValue().stream()
                    .mapToInt(fileFreq -> getFrequency(fileFreq))
                    .sum();
            int totalFreq2 = entry2.getValue().stream()
                    .mapToInt(fileFreq -> getFrequency(fileFreq))
                    .sum();
            return Integer.compare(totalFreq2, totalFreq1);
        })
                .collect(Collectors.toMap(e->e.getKey(), e->e.getValue(), (v1,v2)-> v1, () -> new LinkedHashMap<>()));
        return uniqueSets;
    }
    private int getFrequency(FileFreq fileFreq) {
        return fileFreq.getFreq();
    }
}
