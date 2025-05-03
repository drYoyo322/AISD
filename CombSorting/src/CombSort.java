import java.util.List;

public class CombSort {
    // To find gap between elements
    int getNextGap(int gap) {
        // Shrink gap by Shrink factor
        gap = (gap * 10) / 13;
        if (gap < 1)
            return 1;
        return gap;
    }
    private int iterations;

    // Function to sort arr[] using Comb Sort
    void sort(List<Integer> list) {
        iterations = 0;
        int n = list.size();

        // initialize gap
        int gap = n;

        // Initialize swapped as true to make sure that
        // loop runs
        boolean swapped = true;

        // Keep running while gap is more than 1 and last
        // iteration caused a swap
        while (gap != 1 || swapped) {
            iterations++;
            // Find next gap
            gap = getNextGap(gap);

            // Initialize swapped as false so that we can
            // check if swap happened or not
            swapped = false;

            // Compare all elements with current gap
            for (int i = 0; i < n - gap; i++) {
                iterations++;
                if (list.get(i) > list.get(i + gap)) {
                    // Swap arr[i] and arr[i+gap]
                    int temp = list.get(i);
                    list.set(i, list.get(i + gap));
                    list.set(i + gap, temp);

                    // Set swapped
                    swapped = true;
                }
            }
        }

    }
    public int getIterations(){
        return iterations;
    }
}