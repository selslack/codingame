package me.selslack.codingame.rings;

import java.util.*;

// -[>+]+ - all A
class Player {
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        String magicPhrase = in.nextLine();
        List<Command> seq = new Player.ManagedMemoryPlanner().plan(magicPhrase);
        StringBuilder builder = new StringBuilder(seq.size() + 1);

        for (Command command : seq) {
            builder.append(command.op);
        }

        System.out.println(builder.toString());
    }

    enum Word {
        SPACE(' '),
        A('A'),
        B('B'),
        C('C'),
        D('D'),
        E('E'),
        F('F'),
        G('G'),
        H('H'),
        I('I'),
        J('J'),
        K('K'),
        L('L'),
        M('M'),
        N('N'),
        O('O'),
        P('P'),
        Q('Q'),
        R('R'),
        S('S'),
        T('T'),
        U('U'),
        V('V'),
        W('W'),
        X('X'),
        Y('Y'),
        Z('Z');

        static private int    _length;
        static private Word[] _values;

        static {
            _values = values();
            _length = values().length;
        }

        static public Word findByCharacter(char character) {
            for (Word s : Word.values()) {
                if (s._value == character) {
                    return s;
                }
            }

            throw new RuntimeException("No symbol found for character");
        }

        private final char _value;

        Word(char value) {
            _value = value;
        }

        public Word prev() {
            if (ordinal() == 0) {
                return _values[_length - 1];
            }
            else {
                return _values[ordinal() - 1];
            }
        }

        public Word next() {
            if (ordinal() == _length - 1) {
                return _values[0];
            }
            else {
                return _values[ordinal() + 1];
            }
        }
    }

    public enum Command {
        FORWARD(">"),
        BACKWARD("<"),
        INCREASE("+"),
        DECREASE("-"),
        FOR("["),
        ENDFOR("]"),
        COMMIT("."),
        SPECIAL("/");

        public final String op;

        Command(String op) {
            this.op = op;
        }
    }

    static public class Memory implements Cloneable {
        private int _rotation;
        private Word[] _state;
        private boolean[] _frozen;

        public Memory(int size) {
            _rotation = 0;
            _state = new Word[size];
            _frozen = new boolean[size];

            // Set default state
            Arrays.fill(_state, Word.SPACE);
        }

        public int getSize() {
            return _state.length;
        }

        public int getRotation() {
            return _rotation;
        }

        public void setRotation(int address) {
            _rotation = checkAddress(address);
        }

        public Word read(int address) {
            return _state[checkAddress(address)];
        }

        public void write(int address, Word word) {
            _state[checkAddress(address)] = word;
        }

        public void freeze(int address) {
            _frozen[checkAddress(address)] = true;
        }

        public void unfreeze(int address) {
            _frozen[checkAddress(address)] = false;
        }

        public boolean frozen(int address) {
            return _frozen[checkAddress(address)];
        }

        protected int checkAddress(int address) {
            if (address < 0 || address >= _state.length) {
                throw new RuntimeException("Invalid address = " + address);
            }

            return address;
        }

        @Override
        protected Memory clone() {
            Memory result;

            try {
                result = (Memory) super.clone();
            }
            catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }

            result._rotation = this._rotation;
            result._state = this._state.clone();
            result._frozen = this._frozen.clone();

            return result;
        }

        @Override
        public String toString() {
            return "Memory{_state=" + Arrays.toString(_state) + " _frozen=" + Arrays.toString(_frozen) + " _rotation=" + _rotation + "}";
        }
    }

    interface CommandSequence {
        public List<Command> get();
    }

    static public class WriteSequence implements CommandSequence {
        private final Word from;
        private final Word to;

        public WriteSequence(Word from, Word to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public List<Command> get() {
            List<Command> seq = new LinkedList<>();
            int moves = Utils.cyclicDistance(from.ordinal(), to.ordinal(), Word._length);

            if (Math.abs(moves) >= (4 + to.ordinal())) {
                seq.add(Command.FOR);
                seq.add(Command.DECREASE);
                seq.add(Command.ENDFOR);

                moves = Utils.cyclicDistance(0, to.ordinal(), Word._length);
            }

            for (int r = 0; r < Math.abs(moves); r++) {
                seq.add(moves < 0 ? Command.DECREASE : Command.INCREASE);
            }

            return seq;
        }
    }

    static public class MoveSequence implements CommandSequence {
        private final int to;
        private final Memory memory;

        public MoveSequence(Memory memory, int to) {
            this.to = to;
            this.memory = memory;
        }

        @Override
        public List<Command> get() {
            LinkedList<Command> seq = new LinkedList<>();
            LinkedList<Word> hacky = new LinkedList<>();
            Command forCommand = null;
            int moves = Utils.cyclicDistance(memory.getRotation(), to, memory.getSize());

            // space ahead
            for (int i = 0; i < memory.getSize(); i++) {
                if (memory.read(Utils.normalizeCyclicAddress(memory.getRotation() + i, memory.getSize())) == Word.SPACE) {
                    int newMoves = Utils.cyclicDistance(memory.getRotation() + i, to, memory.getSize());

                    if (Math.abs(moves) - Math.abs(newMoves) > 3) {
                        forCommand = Command.FORWARD;
                        moves = newMoves;
                    }

                    break;
                }
            }

            // space behind
            for (int i = 0; i < memory.getSize(); i++) {
                if (memory.read(Utils.normalizeCyclicAddress(memory.getRotation() - i, memory.getSize())) == Word.SPACE) {
                    int newMoves = Utils.cyclicDistance(memory.getRotation() - i, to, memory.getSize());

                    if (Math.abs(moves) - Math.abs(newMoves) > 3) {
                        forCommand = Command.BACKWARD;
                        moves = newMoves;
                    }

                    break;
                }
            }

            if (forCommand != null) {
                seq.add(Command.FOR);
                seq.add(forCommand);
                seq.add(Command.ENDFOR);
            }

            for (int r = 0; r < Math.abs(moves); r++) {
                seq.add(moves < 0 ? Command.BACKWARD : Command.FORWARD);
            }

            return seq;
        }
    }

    interface Planner {
        public List<Command> plan(String phrase);
    }

    static class NaivePlanner implements Planner {
        private Memory _memory;

        public NaivePlanner() {
            _memory = new Memory(30);
        }

        @Override
        public List<Command> plan(String phrase) {
            List<Command> plan = new LinkedList<>();

            for (int i = 0; i < phrase.length(); i++) {
                LinkedList<LinkedList<Integer>> predictions = new LinkedList<>();
                Map<LinkedList<Command>, Memory> cost = new HashMap<>();

                // Im suck with recursive functions
                getSearchPaths(
                    new LinkedList<>(Arrays.asList(_memory.getRotation())),
                    _memory.getSize(),
                    15,
                    Math.min(2, phrase.length() - i - 1),
                    predictions
                );

                for (LinkedList<Integer> prediction : predictions) {
                    LinkedList<Command> predictionPlan = new LinkedList<>();
                    Memory localMemory = _memory.clone();
                    int step = 0;

                    // First one is a current position
                    prediction.removeFirst();

                    for (Integer address : prediction) {
                        Word word = Word.findByCharacter(phrase.charAt(i + step));

                        predictionPlan.addAll(new MoveSequence(localMemory, address).get());
                        predictionPlan.addAll(new WriteSequence(localMemory.read(address), word).get());
                        predictionPlan.add(Command.COMMIT);

                        localMemory.setRotation(address);
                        localMemory.write(address, word);
                    }

                    cost.put(predictionPlan, localMemory);
                }

                Map.Entry<LinkedList<Command>, Memory> bestPlan = cost
                    .entrySet()
                    .stream()
                    .sorted(Comparator.comparingInt(v -> v.getKey().size()))
                    .findFirst()
                    .orElseThrow(RuntimeException::new);

                for (Command command : bestPlan.getKey()) {
                    plan.add(command);

                    if (command == Command.COMMIT) {
                        break;
                    }
                }

                _memory = bestPlan.getValue();
            }

            return plan;
        }

        public void getSearchPaths(LinkedList<Integer> prefix, int size, int width, int depth, LinkedList<LinkedList<Integer>> storage) {
            if (prefix == null) {
                prefix = new LinkedList<>();
            }

            for (int i = prefix.getLast() - width; i <= prefix.getLast() + width; i++) {
                LinkedList<Integer> internalList = new LinkedList<>();

                internalList.addAll(prefix);
                internalList.add(Utils.normalizeCyclicAddress(i, size));

                if (depth == 0) {
                    storage.add(internalList);
                }
                else {
                    getSearchPaths(internalList, size, width, depth - 1, storage);
                }
            }
        }
    }

    static public class ManagedMemoryPlanner implements Planner {
        private Memory _memory;

        public ManagedMemoryPlanner() {
            _memory = new Memory(30);
        }

        @Override
        public List<Command> plan(String phrase) {
            List<Command> plan = new LinkedList<>();
            Word[] hot = getHotWords(phrase);

            for (int i = 0; i < hot.length; i++) {
                plan.addAll(new WriteSequence(Word.SPACE, hot[i]).get());
                plan.add(Command.FORWARD);

                _memory.setRotation(i + 1);
                _memory.freeze(i);
                _memory.write(i, hot[i]);
            }

            for (int i = 0; i < phrase.length(); i++) {
                LinkedList<LinkedList<Integer>> predictions = new LinkedList<>();
                Map<LinkedList<Command>, Memory> cost = new HashMap<>();

                // Im suck with recursive functions
                getSearchPaths(
                    new LinkedList<>(Arrays.asList(_memory.getRotation())),
                    _memory.getSize(),
                    15,
                    Math.min(1, phrase.length() - i - 1),
                    predictions
                );

                for (LinkedList<Integer> prediction : predictions) {
                    LinkedList<Command> predictionPlan = new LinkedList<>();
                    Memory localMemory = _memory.clone();
                    int step = 0;

                    // First one is a current position
                    prediction.removeFirst();

                    for (Integer address : prediction) {
                        Word word = Word.findByCharacter(phrase.charAt(i + step));

                        predictionPlan.addAll(new MoveSequence(localMemory, address).get());
                        predictionPlan.addAll(new WriteSequence(localMemory.read(address), word).get());
                        predictionPlan.add(Command.COMMIT);

                        localMemory.setRotation(address);
                        localMemory.write(address, word);

                        if (localMemory.frozen(address)) {
                            predictionPlan.addAll(new WriteSequence(word, _memory.read(address)).get());
                            localMemory.write(address, _memory.read(address));
                        }

                        predictionPlan.add(Command.SPECIAL);
                    }

                    cost.put(predictionPlan, localMemory);
                }

                Map.Entry<LinkedList<Command>, Memory> bestPlan = cost
                    .entrySet()
                    .stream()
                    .sorted(Comparator.comparingInt(v -> v.getKey().size()))
                    .findFirst()
                    .orElseThrow(RuntimeException::new);

                for (Command command : bestPlan.getKey()) {
                    if (command == Command.SPECIAL) {
                        break;
                    }

                    plan.add(command);
                }

                _memory = bestPlan.getValue();
            }

            return plan;
        }

        public Word[] getHotWords(String phrase) {
            Map<Word, Integer> count = new HashMap<>();
            Map<Word, Map<Word, Integer>> followups = new HashMap<>();

            for (int i = 0; i < phrase.length(); i++) {
                Word word = Word.findByCharacter(phrase.charAt(i));

                count.compute(word, (k, v) -> v == null ? 1 : v + 1);
                followups.compute(word, (k, v) -> v == null ? new HashMap<>() : v);

                if ((i + 1) < phrase.length()) {
                    followups.get(word).compute(Word.findByCharacter(phrase.charAt(i + 1)), (k, v) -> v == null ? 1 : v + 1);
                }
            }

            Word[] hottest = count
                .entrySet()
                .stream()
                .filter(v -> v.getValue() > 10)
                .sorted(Comparator.comparingInt(v -> -v.getValue()))
                .limit(9)
                .map(v -> v.getKey())
                .toArray(Word[]::new);

            Arrays.sort(hottest, new Comparator<Word>() {
                @Override
                public int compare(Word o1, Word o2) {
                    return followups.get(o2).computeIfAbsent(o1, v -> 0) - followups.get(o1).computeIfAbsent(o2, v -> 0);
                }
            });

            return hottest;
        }

        public void getSearchPaths(LinkedList<Integer> prefix, int size, int width, int depth, LinkedList<LinkedList<Integer>> storage) {
            if (prefix == null) {
                prefix = new LinkedList<>();
            }

            for (int i = prefix.getLast() - width; i <= prefix.getLast() + width; i++) {
                LinkedList<Integer> internalList = new LinkedList<>();

                internalList.addAll(prefix);
                internalList.add(Utils.normalizeCyclicAddress(i, size));

                if (depth == 0) {
                    storage.add(internalList);
                }
                else {
                    getSearchPaths(internalList, size, width, depth - 1, storage);
                }
            }
        }

        public LinkedList<Command> rolloutLoops(LinkedList<Command> commands) {
            return commands;
        }
    }

    static public class Utils {
        static public int cyclicDistance(int current, int needed, int total) {
            int half     = total >> 1;
            int distance = Math.abs(current - needed);
            int result   = distance;

            if (distance > half) {
                result -= total;
            }

            if (current > needed) {
                result *= -1;
            }

            return result;
        }

        static public int normalizeCyclicAddress(int needed, int total) {
            if (needed >= 0) {
                return needed % total;
            }
            else {
                int mod = Math.abs(needed) % total;

                if (mod == 0) {
                    return 0;
                }
                else {
                    return total - mod;
                }
            }
        }
    }
}