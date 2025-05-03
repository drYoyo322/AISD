import java.io.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        CombSort ob = new CombSort();
        String path = "random_numbers/numbers_%s.txt";
        long duration;
        for (int i = 1; i <= 60; i++) {
            ReadNumbersToList nToList = new ReadNumbersToList();
            nToList.read(String.format(path, i));
            List<Integer> list = nToList.getList();
            long startTime = System.nanoTime();
            ob.sort(list);
            long endTime = System.nanoTime();
            duration = (endTime - startTime);
            double simplified = (double) duration / 1000000;
            System.out.printf("Количество элементов: %s Время затраченое на сортировку: %s ~~ %s Milisec && [%s] Iterations", nToList.getSize(), duration, simplified, ob.getIterations());
            System.out.println();
        }
    }
}