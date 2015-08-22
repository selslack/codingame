package me.selslack.codingame.compiler;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Main {
    static private OptionParser _options = new OptionParser();

    static {
        _options.accepts("player", "Player FQCN").withRequiredArg();
        _options.accepts("out", "Compilation output path").withRequiredArg().ofType(File.class);
        _options.accepts("help", "Show this help message").forHelp();
        _options.nonOptions("Paths with sources").ofType(File.class);
    }

    public static void main(String[] args) throws IOException {
        OptionSet parsed = _options.parse(args);

        String     fqcn  = (String) parsed.valueOf("player");
        File       out   = (File) parsed.valueOf("out");
        List<File> paths = (List<File>) parsed.nonOptionArguments();

        if (parsed.has("help")) {
            try {
                _options.printHelpOn(System.out);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return ;
        }

        if (fqcn == null) {
            throw new RuntimeException("--player option is required");
        }

        if (out == null) {
            throw new RuntimeException("--out option is required");
        }

        if (paths.isEmpty()) {
            throw new RuntimeException("No class paths specified");
        }

        try (FileWriter writer = new FileWriter(out)) {
            writer.write(new Compiler(fqcn, paths).compile());
            writer.flush();
        }
    }
}
