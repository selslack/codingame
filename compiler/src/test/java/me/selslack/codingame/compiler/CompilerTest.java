package me.selslack.codingame.compiler;

import org.junit.Test;

import java.io.File;
import java.util.LinkedList;

import static org.junit.Assert.*;

public class CompilerTest {
    @Test
    public void testCompile() throws Exception {
        Compiler compiler = new Compiler(
            "me.selslack.codingame.test.Player",
            new File[] { new File(getClass().getResource("/player").toURI()) }
        );

//        assertEquals(
//            compiler.compile(),
//            ""
//        );
    }
}