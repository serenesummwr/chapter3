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
                                    return currentList;
                                },
                                currentList -> {
                                    currentList.sort(new Comparator<FileFreq>() {
                                        @Override
                                        public int compare(FileFreq o1, FileFreq o2) {
                                            return Integer.compare(o2.getFreq(), o1.getFreq());
                                        }
                                    });
                                    return currentList;
                                }
                        )
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(new Comparator<List<FileFreq>>() {
                    @Override
                    public int compare(List<FileFreq> o1, List<FileFreq> o2) {
                        int sum1 = o1.stream().mapToInt(FileFreq::getFreq).sum();
                        int sum2 = o2.stream().mapToInt(FileFreq::getFreq).sum();
                        return sum2-sum1;
                    }
                }))
                .collect(Collectors.toMap(e->e.getKey(), e->e.getValue(),
                        (v1, v2)->v1, ()->new LinkedHashMap<>()));
        return uniqueSets;
    }
}
