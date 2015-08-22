package me.selslack.codingame.compiler;

public class ClassNotFoundException extends Exception {
    public ClassNotFoundException(String clazz) {
        super("Class " + clazz + " wasn't found in provided sources");
    }
}
