package com.rainbowpunch.jtdg.core.limiters.primitive;

import com.rainbowpunch.jtdg.core.limiters.Limiter;
import com.rainbowpunch.jtdg.core.limiters.ObjectLimiter;
import com.rainbowpunch.jtdg.util.ReadableCharList;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 */
public class StringLimiter extends ObjectLimiter<String> implements Limiter<String> {

    private final Integer length;

    public StringLimiter() {
        length = 30;
    }

    public StringLimiter(int length) {
        this(ReadableCharList.LIST_OF_ALL_CHAR, length);
    }

    public StringLimiter(List<Character> charList) {
        this(charList, 30);
    }

    public StringLimiter(List<Character> charList, int length) {
        this.length = length;
        List<String> stringList = charListToStringList(charList);
        this.updateObjectList(stringList);
    }

    private List<String> charListToStringList(List<Character> charList) {
        return charList.stream()
                .map(ch -> Character.toString(ch))
                .collect(Collectors.toList());
    }

    @Override
    protected List<String> configureObjectList() {
        return charListToStringList(ReadableCharList.LIST_OF_ALL_CHAR);
    }

    @Override
    public Supplier<String> generateSupplier(Random random) {
        return () -> IntStream.range(0, length)
                .sequential()
                .mapToObj((i) -> super.generateSupplier(random))
                .map(Supplier::get)
                .collect(Collectors.joining());
    }
}
