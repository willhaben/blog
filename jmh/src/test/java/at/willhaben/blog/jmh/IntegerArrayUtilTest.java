package at.willhaben.blog.jmh;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.fail;

public class IntegerArrayUtilTest {


    private static Random random = new Random();

    private static List<Integer> getAllIds;
    private static List<Integer> getIdsToSubtract;


    @BeforeClass
    public static void initLists() {
        getAllIds = IntStream.range(0, 500).boxed().collect(Collectors.toList());
        getIdsToSubtract = IntStream.range(0, 250).map(i -> random.nextInt(500)).boxed().collect(Collectors.toList());
        Collections.sort(getIdsToSubtract);
    }


    @Test
    public void testSubtract() throws Exception {
        List<Integer> result = IntegerArrayUtil.subtractWithDefaultLambda(getAllIds,getIdsToSubtract);
        testResult(result);
    }

    @Test
    public void testSubtractArrayListBinarySearch() throws Exception {
        List<Integer> result = IntegerArrayUtil.subtractArrayListBinarySearch(getAllIds,getIdsToSubtract);
        testResult(result);
    }

    @Test
    public void testSubtractArrayList() throws Exception {
        List<Integer> result = IntegerArrayUtil.subtractArrayListWithLoop(getAllIds,getIdsToSubtract);
        testResult(result);
    }

    @Test
    public void testSubtractLinkedList() throws Exception {
        List<Integer> result = IntegerArrayUtil.subtractLinkedList(getAllIds,getIdsToSubtract);
        testResult(result);
    }

    @Test
    public void testSubtractLinkedListBinarySearch() throws Exception {
        List<Integer> result = IntegerArrayUtil.subtractLinkedListBinarySearch(getAllIds,getIdsToSubtract);
        testResult(result);
    }

    private void testResult(List<Integer> list) {
        list.stream().filter(i -> getIdsToSubtract.contains(i)).forEach(i -> fail("Should not contain " + i));
    }
}