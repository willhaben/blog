package at.willhaben.blog.jmh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class IntegerArrayUtil {

    public static List<Integer> subtractWithDefaultLambda(List<Integer> list1, final List<Integer> list2) {
        return list1.stream().filter(i -> !list2.contains(i)).collect(Collectors.toList());
    }

    public static List<Integer> subtractArrayListBinarySearch(List<Integer> list1, final List<Integer> list2) {
        return list1.stream().filter(i -> Collections.binarySearch(list2, i) < 0).collect(Collectors.toList());
    }

    public static List<Integer> subtractArrayListWithLoop(List<Integer> list1, final List<Integer> list2) {
        List<Integer> result = new ArrayList<>();

        for (Integer i : list1) {
            if (!list2.contains(i)) {
                result.add(i);
            }
        }

        return result;
    }

    public static List<Integer> subtractLinkedList(List<Integer> list1, final List<Integer> list2) {
        return list1.stream().filter(i -> !list2.contains(i)).collect(Collectors.toCollection(LinkedList::new));
    }

    public static List<Integer> subtractLinkedListBinarySearch(List<Integer> list1, final List<Integer> list2) {
        return list1.stream().filter(i -> Collections.binarySearch(list2, i) < 0).collect(Collectors.toCollection(LinkedList::new));
    }


}
