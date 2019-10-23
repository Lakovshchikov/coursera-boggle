import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.TST;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class BoggleSolver {

    private ST<String, Integer> words;
    private TST<String> dictionary;
    private ST<Integer, Bag<Integer>> adjFull;
    private TST<String> allWords;
    private BoggleBoard b;

    private int size;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        this.dictionary = new TST<String>();
        for (String s : dictionary) {
            this.dictionary.put(s,s);
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        words = new ST<>();
        adjFull = new ST<>();
        allWords = new TST<>();
        b = board;
        size = board.cols();

        int [][] _board = new int[size][size];

        for (int row = 0; row < size; row++){
            for (int col = 0; col < size; col++ ){
                _board[row][col] = toNumber(row, col);
            }
        }

        for (int row = 0; row < size; row++ ){
            for (int col = 0; col < size; col++ ){
                Bag adj = new Bag();
                if (row > 0) {
                    adj.add(_board[row - 1][col]);
                    if (col > 0) {
                        adj.add(_board[row - 1][col - 1]);
                    }
                    if (col < size - 1) {
                        adj.add(_board[row - 1][col + 1]);
                    }
                }
                if (row < size - 1) {
                    adj.add(_board[row + 1][col]);
                    if (col > 0) {
                        adj.add(_board[row + 1][col - 1]);
                    }
                    if (col < size - 1) {
                        adj.add(_board[row + 1][col + 1]);
                    }
                }
                if (col + 1 < size) {
                    adj.add(_board[row][col + 1]);
                }
                if (col - 1 >= 0) {
                    adj.add(_board[row][col - 1]);
                }
                adjFull.put(_board[row][col], adj);
            }
        }



        for (int i = 0; i < size * size; i++) {
            boolean[] marked = new boolean[size * size];
            marked[i] = true;
            char c = board.getLetter(rowFromNumber(i), colFromNumber(i));
            if (c == 'Q') {
                adjDFS(i, c + "U", marked);
            } else {
                adjDFS(i, c + "", marked);
            }
        }

        for (String s : allWords.keys()) {
            if (dictionary.contains(s)){
                words.put(s, s.length());
            }
        }

        return words.keys();
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (words.contains(word)) {
            int length = words.get(word);
            if (length <= 4)
                return 1;
            if (length == 5)
                return 2;
            if (length == 6)
                return 3;
            if (length == 7)
                return 5;
            if (length >= 8)
                return 11;
            }
        return 0;
    }


    private void adjDFS (int num, String word, boolean[] marked) {
        Bag<Integer> bag = adjFull.get(num);
        for (Integer i : bag){
            if (!marked[i]) {
                int col = colFromNumber(i);
                int row = rowFromNumber(i);
                char c = b.getLetter(row, col);
                String newWord = word + c;
                if (c == 'Q') {
                    newWord += "U";
                }
                if (newWord.length() > 2) {
                    int a = 0;
                    for (String s : dictionary.keysWithPrefix(newWord)){
                        if (s.length() > 2) {
                            a++;
                            break;
                        }
                    }
                    if (a == 1) {
                        marked[i] = true;
                        allWords.put(newWord, newWord);
                        adjDFS(i, newWord,copyArray(marked));
                        marked[i] = false;
                    }
                } else {
                    marked[i] = true;
                    adjDFS(i, newWord, copyArray(marked));
                    marked[i] = false;
                }
            }
        }
    }

    private int toNumber (int row, int col) {
        return size * row + col;
    }
    private int colFromNumber (int num) {
        return num % size;
    }
    private int rowFromNumber (int num) {
        return num / size;
    }

    private boolean[] copyArray(boolean[] arr){
        boolean[] result = new boolean[arr.length];
        for(int i = 0; i < arr.length; i++) {
            result[i] = arr[i];
        }
        return result;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}