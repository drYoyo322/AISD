import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BStarTree {
    private static final int DEFAULT_ORDER = 4;
    private final int order;
    private Node root;

    // Статистика
    private long insertOperations;
    private long searchOperations;
    private long deleteOperations;

    public BStarTree() {
        this(DEFAULT_ORDER);
    }

    public BStarTree(int order) {
        if (order < 3) {
            throw new IllegalArgumentException("Order must be at least 3");
        }
        this.order = order;
        this.root = new Node(true);
    }

    // Внутренний класс для узла
    private class Node {
        List<Integer> keys;
        List<Node> children;
        boolean isLeaf;

        Node(boolean isLeaf) {
            this.keys = new ArrayList<>();
            this.children = new ArrayList<>();
            this.isLeaf = isLeaf;
        }
    }

    // Метод поиска
    public boolean contains(int key) {
        searchOperations = 0;
        return contains(root, key);
    }

    private boolean contains(Node node, int key) {
        searchOperations++;
        int i = 0;
        while (i < node.keys.size() && key > node.keys.get(i)) {
            i++;
            searchOperations++;
        }

        if (i < node.keys.size() && key == node.keys.get(i)) {
            return true;
        }

        if (node.isLeaf) {
            return false;
        }

        return contains(node.children.get(i), key);
    }

    // Метод вставки
    public void insert(int key) {
        insertOperations = 0;
        Node root = this.root;
        insertOperations++;

        if (root.keys.size() == 2 * order - 1) {
            Node newRoot = new Node(false);
            newRoot.children.add(this.root);
            this.root = newRoot;
            splitChild(newRoot, 0);
            insertOperations += 3;
        }

        insertNonFull(this.root, key);
    }

    private void insertNonFull(Node node, int key) {
        insertOperations++;
        int i = node.keys.size() - 1;

        if (node.isLeaf) {
            while (i >= 0 && key < node.keys.get(i)) {
                i--;
                insertOperations++;
            }
            node.keys.add(i + 1, key);
            insertOperations++;
        } else {
            while (i >= 0 && key < node.keys.get(i)) {
                i--;
                insertOperations++;
            }
            i++;

            if (node.children.get(i).keys.size() == 2 * order - 1) {
                splitChild(node, i);
                insertOperations += 3;
                if (key > node.keys.get(i)) {
                    i++;
                    insertOperations++;
                }
            }

            insertNonFull(node.children.get(i), key);
        }
    }

    private void splitChild(Node parent, int childIndex) {
        insertOperations += 2;
        Node child = parent.children.get(childIndex);
        Node newChild = new Node(child.isLeaf);

        // Перенос (order-1) ключей в новый узел
        for (int i = 0; i < order - 1; i++) {
            newChild.keys.add(child.keys.remove(order));
            insertOperations += 2;
        }

        if (!child.isLeaf) {
            for (int i = 0; i < order; i++) {
                newChild.children.add(child.children.remove(order));
                insertOperations += 2;
            }
        }

        parent.keys.add(childIndex, child.keys.remove(order - 1));
        parent.children.add(childIndex + 1, newChild);
        insertOperations += 2;
    }

    // Метод удаления
    public void delete(int key) {
        deleteOperations = 0;
        delete(root, key);
        deleteOperations++;

        if (root.keys.isEmpty() && !root.isLeaf) {
            root = root.children.get(0);
            deleteOperations++;
        }
    }

    private void delete(Node node, int key) {
        deleteOperations++;
        int idx = findKey(node, key);

        if (idx < node.keys.size() && node.keys.get(idx) == key) {
            if (node.isLeaf) {
                deleteFromLeaf(node, idx);
            } else {
                deleteFromNonLeaf(node, idx);
            }
        } else {
            if (node.isLeaf) {
                return;
            }

            boolean flag = (idx == node.keys.size());

            if (node.children.get(idx).keys.size() < order) {
                fill(node, idx);
                deleteOperations += 3;
            }

            if (flag && idx > node.keys.size()) {
                delete(node.children.get(idx - 1), key);
            } else {
                delete(node.children.get(idx), key);
            }
        }
    }

    private int findKey(Node node, int key) {
        deleteOperations++;
        int idx = 0;
        while (idx < node.keys.size() && node.keys.get(idx) < key) {
            idx++;
            deleteOperations++;
        }
        return idx;
    }

    private void deleteFromLeaf(Node node, int idx) {
        node.keys.remove(idx);
        deleteOperations++;
    }

    private void deleteFromNonLeaf(Node node, int idx) {
        deleteOperations += 2;
        int key = node.keys.get(idx);

        if (node.children.get(idx).keys.size() >= order) {
            int pred = getPredecessor(node, idx);
            node.keys.set(idx, pred);
            deleteOperations += 2;
            delete(node.children.get(idx), pred);
        } else if (node.children.get(idx + 1).keys.size() >= order) {
            int succ = getSuccessor(node, idx);
            node.keys.set(idx, succ);
            deleteOperations += 2;
            delete(node.children.get(idx + 1), succ);
        } else {
            merge(node, idx);
            deleteOperations++;
            delete(node.children.get(idx), key);
        }
    }

    private int getPredecessor(Node node, int idx) {
        deleteOperations++;
        Node curr = node.children.get(idx);
        while (!curr.isLeaf) {
            curr = curr.children.get(curr.keys.size());
            deleteOperations++;
        }
        return curr.keys.get(curr.keys.size() - 1);
    }

    private int getSuccessor(Node node, int idx) {
        deleteOperations++;
        Node curr = node.children.get(idx + 1);
        while (!curr.isLeaf) {
            curr = curr.children.get(0);
            deleteOperations++;
        }
        return curr.keys.get(0);
    }

    private void fill(Node node, int idx) {
        deleteOperations++;
        if (idx != 0 && node.children.get(idx - 1).keys.size() >= order) {
            borrowFromPrev(node, idx);
        } else if (idx != node.keys.size() && node.children.get(idx + 1).keys.size() >= order) {
            borrowFromNext(node, idx);
        } else {
            if (idx != node.keys.size()) {
                merge(node, idx);
            } else {
                merge(node, idx - 1);
            }
        }
    }

    private void borrowFromPrev(Node node, int idx) {
        deleteOperations += 4;
        Node child = node.children.get(idx);
        Node sibling = node.children.get(idx - 1);

        child.keys.add(0, node.keys.get(idx - 1));

        if (!child.isLeaf) {
            child.children.add(0, sibling.children.remove(sibling.children.size() - 1));
        }

        node.keys.set(idx - 1, sibling.keys.remove(sibling.keys.size() - 1));
    }

    private void borrowFromNext(Node node, int idx) {
        deleteOperations += 4;
        Node child = node.children.get(idx);
        Node sibling = node.children.get(idx + 1);

        child.keys.add(node.keys.get(idx));

        if (!child.isLeaf) {
            child.children.add(sibling.children.remove(0));
        }

        node.keys.set(idx, sibling.keys.remove(0));
    }

    private void merge(Node node, int idx) {
        deleteOperations += 5;
        Node child = node.children.get(idx);
        Node sibling = node.children.get(idx + 1);

        child.keys.add(node.keys.remove(idx));

        child.keys.addAll(sibling.keys);

        if (!child.isLeaf) {
            child.children.addAll(sibling.children);
        }

        node.children.remove(idx + 1);
    }

    // Геттеры для статистики
    public long getInsertOperations() {
        return insertOperations;
    }

    public long getSearchOperations() {
        return searchOperations;
    }

    public long getDeleteOperations() {
        return deleteOperations;
    }

    // Метод для тестирования
    public static void main(String[] args) {
        // 1. Создаем B*-дерево
        BStarTree tree = new BStarTree(4);

        // 2. Генерируем массив из 10000 случайных чисел
        int[] numbers = new int[10000];
        Random random = new Random();
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = random.nextInt(100000);
        }

        // 3. Добавляем элементы и собираем статистику
        long[] insertTimes = new long[numbers.length];
        long[] insertOps = new long[numbers.length];

        for (int i = 0; i < numbers.length; i++) {
            long startTime = System.nanoTime();
            tree.insert(numbers[i]);
            long endTime = System.nanoTime();

            insertTimes[i] = endTime - startTime;
            insertOps[i] = tree.getInsertOperations();
            System.out.println("Время в нс:" + (endTime - startTime) + "   Количество операций:  " + tree.getInsertOperations());
        }

        // 4. Выбираем 100 случайных элементов для поиска
        long[] searchTimes = new long[100];
        long[] searchOps = new long[100];

        for (int i = 0; i < 100; i++) {
            int randomIndex = random.nextInt(numbers.length);
            int numToSearch = numbers[randomIndex];

            long startTime = System.nanoTime();
            boolean found = tree.contains(numToSearch);
            long endTime = System.nanoTime();

            searchTimes[i] = endTime - startTime;
            searchOps[i] = tree.getSearchOperations();
        }

        // 5. Выбираем 1000 случайных элементов для удаления
        long[] deleteTimes = new long[1000];
        long[] deleteOps = new long[1000];

        // Перемешиваем массив, чтобы удалять случайные элементы
        List<Integer> numbersList = new ArrayList<>();
        for (int num : numbers) {
            numbersList.add(num);
        }
        Collections.shuffle(numbersList);

        for (int i = 0; i < 1000; i++) {
            int numToDelete = numbersList.get(i);

            long startTime = System.nanoTime();
            tree.delete(numToDelete);
            long endTime = System.nanoTime();

            deleteTimes[i] = endTime - startTime;
            deleteOps[i] = tree.getDeleteOperations();
        }

        // 6. Вычисляем средние значения
        double avgInsertTime = calculateAverage(insertTimes);
        double avgInsertOps = calculateAverage(insertOps);

        double avgSearchTime = calculateAverage(searchTimes);
        double avgSearchOps = calculateAverage(searchOps);

        double avgDeleteTime = calculateAverage(deleteTimes);
        double avgDeleteOps = calculateAverage(deleteOps);

        // Вывод результатов
        System.out.println("\n\nСреднее время вставки (нс) для всего массива: " + avgInsertTime);
        System.out.println("Среднее количество операций вставки для всего массива: " + avgInsertOps);

        System.out.println("Среднее время поиска (нс) для 100: " + avgSearchTime);
        System.out.println("Среднее количество операций поиска для 100: " + avgSearchOps);

        System.out.println("Среднее время удаления (нс) для 1000: " + avgDeleteTime);
        System.out.println("Среднее количество операций удаления для 1000: " + avgDeleteOps);
    }

    private static double calculateAverage(long[] array) {
        long sum = 0;
        for (long value : array) {
            sum += value;
        }
        return (double) sum / array.length;
    }
}