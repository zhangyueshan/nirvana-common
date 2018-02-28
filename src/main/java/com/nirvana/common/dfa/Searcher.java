package com.nirvana.common.dfa;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by 张阅山 on 2017/7/3.
 * <p>
 * 1,目前只支持String型的field
 * 2,支持多音字
 *
 * @author Yueshan
 */
public class Searcher<T> {

    private Node<T> node;

    private String[] fieldNames;

    private Map<String, Field> fieldMap = new HashMap<>();

    public Searcher(Class<T> tClass, String... fieldNames) {
        for (String fieldName : fieldNames) {
            try {
                Field field = tClass.getDeclaredField(fieldName);
                if (field.getType() != String.class) {
                    throw new RuntimeException("目前只支持String型的字段。");
                }
                field.setAccessible(true);
                fieldMap.put(fieldName, field);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(tClass.getName() + "类中不存在名称为：" + fieldName + "的字段。");
            }
        }
        this.fieldNames = fieldNames;
        node = new Node<>(tClass);
    }

    public void source(Collection<T> source) {
        for (T t : source) {
            source(t);
        }
    }

    public void source(T source) {
        for (String fieldName : fieldNames) {
            addSource(source, fieldName);
        }
    }

    public List<T> search(String word) {
        return node.search(word);
    }


    public List<T> search(String word, int page, int size) {
        if (page < 0 || size <= 0) {
            return Collections.emptyList();
        }
        int startIndex = (page-1) * size;
        int endIndex = startIndex + size;
        List<T> list = search(word);
        if (startIndex > list.size() - 1) {
            return Collections.emptyList();
        }
        endIndex = endIndex > list.size() - 1 ? list.size() : endIndex;
        return search(word).subList(startIndex, endIndex);
    }

    private void addSource(T t, String fieldName) {
        Field field = fieldMap.get(fieldName);
        try {
            String value = (String) field.get(t);
            if (value != null && !Objects.equals(value, "")) {
                char[][] charArrays = processChineseCharsIfNecessary(value);
                node.addSource(charArrays, t);
            }
        } catch (IllegalAccessException ignored) {
            //field已经设置为 access = true
        }
    }

    private char[][] processChineseCharsIfNecessary(String word) {
        char[] wordChars = word.toCharArray();
        char[][] charArrays = new char[word.length()][];
        for (int i = 0; i < wordChars.length; i++) {
            char[] chars;
            String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(wordChars[i]);
            if (pinyins == null) {
                chars = new char[]{wordChars[i]};
            } else {
                chars = new char[pinyins.length];
                for (int j = 0; j < pinyins.length; j++) {
                    chars[j] = pinyins[j].charAt(0);
                }
            }
            charArrays[i] = chars;
        }
        return charArrays;
    }

}
