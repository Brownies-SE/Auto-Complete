import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.event.*;

/* Got some Gui tips from: http://www.toves.org/books/java/ch24-swing/index.html */
public class Assignment2 {

    public static void main(final String[] args) {
        final Assignment2 ass2 = new Assignment2();
        final Trie dictionary = ass2.new Trie();
        List<String> words = new ArrayList<>();
        dictionary.formTree(words);
        words = loadWords();
        dictionary.formTree(words);

        final Gui gui = ass2.new Gui(dictionary);

        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.pack();
        gui.setBounds(100, 100, 480, 640);
        gui.setVisible(true);
    }

    class Gui extends JFrame {

        private static final long serialVersionUID = 1337L;
        JTextField textInput = new JTextField(10);
        /*
         * Updating Jlist vector directly is bad practice. Using a list model is better
         * form. This was based off of an example found here:
         * https://stackoverflow.com/a/4262716
         */
        DefaultListModel<String> wordList = new DefaultListModel<>();
        JList<String> wordListView = new JList<>(wordList);
        private Trie dictionary;

        public Gui(final Trie dictionary) {
            this.dictionary = dictionary;
            this.getContentPane().add(textInput, BorderLayout.NORTH);
            textInput.addKeyListener(new TrieAdapter(this));
            this.getContentPane().add(wordListView, BorderLayout.CENTER);
        }
    }

    class TrieAdapter extends KeyAdapter {

        private Gui gui;

        public TrieAdapter(Gui gui) {
            this.gui = gui;
        }

        public void keyTyped(KeyEvent evt) {
            String currentWord = gui.textInput.getText();
            Character keyTyped = evt.getKeyChar();
            String nextWord = currentWord == null ? keyTyped.toString() : currentWord + keyTyped;
            gui.wordList.clear();
            gui.wordList.addAll(gui.dictionary.fromPrefix(nextWord));
        }
    }

    class TreeNode {

        public boolean isEndOfWord;
        public HashMap<Character, TreeNode> children;

        public TreeNode() {
            this.isEndOfWord = false;
            this.children = new HashMap<>();
        }

        public List<String> firstWords(final String prefix, int count) {
            final List<String> words = new ArrayList<>();
            if (count <= 0) {
                return words;
            }
            if (this.isEndOfWord) {
                words.add(prefix);
                count--;
            }
            for (final Entry<Character, TreeNode> entry : children.entrySet()) {
                final List<String> nextWords = entry.getValue().firstWords(prefix + entry.getKey(), count);
                count -= nextWords.size();
                words.addAll(nextWords);
                if (count <= 0) {
                    break;
                }
            }
            return words;

        }
    }

    class Trie {

        public TreeNode root;

        public Trie() {
            this.root = new TreeNode();
        }

        public void formTree(final List<String> words) {
            for (final String word : words) {
                insert(word);

            }
        }

        private void insert(final String word) {
            TreeNode current = this.root;
            for (final Character c : word.toCharArray()) {
                if (!current.children.containsKey(c)) {
                    current.children.put(c, new TreeNode());
                }
                current = current.children.get(c);

            }
            current.isEndOfWord = true;
        }

        public List<String> fromPrefix(final String prefix) {
            TreeNode current = this.root;
            for (final Character c : prefix.toCharArray()) {
                if (!current.children.containsKey(c)) {
                    return new ArrayList<>();
                }
                current = current.children.get(c);
            }
            return current.firstWords(prefix, 10);
        }
    }

    public static ArrayList<String> loadWords() {

        final ArrayList<String> listOfLines = new ArrayList<>();
        final Scanner in = new Scanner(System.in);
        System.out.println("Enter word File");
        final String filename = in.nextLine();
        in.close();

        try {
            final BufferedReader br = new BufferedReader(new FileReader(filename));
            String line = br.readLine();
            while (line != null) {
                listOfLines.add(line);
                line = br.readLine();
            }
            br.close();
        } catch (final IOException e) {
        }
        return listOfLines;
    }
}