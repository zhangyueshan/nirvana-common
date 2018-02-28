package com.nirvana.common.dfa;

import org.apache.commons.collections.list.SetUniqueList;

import java.util.*;

/**
 * Created by 张阅山 on 2017/7/3.
 */
public class Node<T> {

    private Map<Character, Node> sons;

    private List<T> sources;

    private Class<T> sourceClass;

    @SuppressWarnings("unchecked")
    public Node(Class<T> sourceClass) {
        this.sourceClass = sourceClass;
        sons = new HashMap<>();
        sources = SetUniqueList.decorate(new ArrayList<>());
    }

    public Map<Character, Node> getSons() {
        return sons;
    }

    public List<T> getSources() {
        return sources;
    }

    public Class<T> getSourceClass() {
        return sourceClass;
    }

    @SuppressWarnings("unchecked")
    public void addSource(String word, T source) {
        this.sources.add(source);
        Node<T> currentNode = this;
        for (char c : word.toCharArray()) {
            Map<Character, Node> sons = currentNode.getSons();
            Node<T> node = sons.get(c);
            if (node == null) {
                node = new Node(getSourceClass());
                sons.put(c, node);
            }
            node.getSources().add(source);
            currentNode = node;
        }
    }


    @SuppressWarnings("unchecked")
    public void addSource(char[][] charArrays, T source) {
        getSources().add(source);
        if (charArrays.length == 0) {
            return;
        }
        char[] chars = charArrays[0];
        for (char c : chars) {
            c = Character.toLowerCase(c);
            Map<Character, Node> sons = getSons();
            Node<T> node = sons.get(c);
            if (node == null) {
                node = new Node(getSourceClass());
                sons.put(c, node);
            }
            char[][] temp;
            if (charArrays.length <= 1) {
                temp = new char[][]{};
            } else {
                temp = Arrays.copyOfRange(charArrays, 1, charArrays.length);
            }
            node.addSource(temp, source);
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> search(String word) {
        word = word.toLowerCase();
        Node<T> currentNode = this;
        for (char c : word.toCharArray()) {
            currentNode = currentNode.getSons().get(c);
            if (currentNode == null) {
                break;
            }
        }
        return currentNode == null ? (List<T>) Collections.emptyList() : currentNode.getSources();
    }
}
