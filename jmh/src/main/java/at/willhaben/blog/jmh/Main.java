package at.willhaben.blog.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    private static Random random = new Random();

    private static List<Integer> getAllIds;
    private static List<Integer> getIdsToSubtract;

    {
        getAllIds = IntStream.range(0, 500000).boxed().collect(Collectors.toList());
        getIdsToSubtract = IntStream.range(0, 10000).map(i -> random.nextInt(50000)).boxed().collect(Collectors.toList());
        Collections.sort(getIdsToSubtract);
    }

    public static void main(String... args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(Main.class.getSimpleName())
                .forks(1)
                .warmupIterations(3)
                .measurementIterations(3)
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    public static void subtractWithDefaultLambda(Blackhole hole) {
        hole.consume(IntegerArrayUtil.subtractWithDefaultLambda(getAllIds, getIdsToSubtract));
    }

    @Benchmark
    public static void subtractArrayList(Blackhole hole) {
        hole.consume(IntegerArrayUtil.subtractArrayListWithLoop(getAllIds, getIdsToSubtract));
    }

    @Benchmark
    public static void subtractArrayListBinarySearch(Blackhole hole) {
        hole.consume(IntegerArrayUtil.subtractArrayListBinarySearch(getAllIds, getIdsToSubtract));
    }

    @Benchmark
    public static void subtractLinkedList(Blackhole hole) {
        hole.consume(IntegerArrayUtil.subtractLinkedList(getAllIds, getIdsToSubtract));
    }

    @Benchmark
    public static void subtractLinkedListBinarySearch(Blackhole hole) {
        hole.consume(IntegerArrayUtil.subtractLinkedListBinarySearch(getAllIds, getIdsToSubtract));
    }

}
