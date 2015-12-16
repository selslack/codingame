package me.selslack.codingame.zombies.genetics;

import me.selslack.codingame.zombies.mcts.Config;
import me.selslack.codingame.zombies.server.GameStateBuilder;
import me.selslack.codingame.zombies.server.ParallelGameTask;
import org.jenetics.*;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.engine.EvolutionStatistics;
import org.jenetics.util.Factory;

import java.util.List;

public class Evolve {
    private static Integer eval(Genotype<IntegerGene> gt) {
        Config config = new Config();

//        config.playoutDepth = gt.getChromosome(0).getGene().intValue();
        config.firstFlexCircleCount = gt.getChromosome(0).getGene().intValue();
        config.firstFlexCircleDistance = gt.getChromosome(1).getGene().intValue();
        config.secondFlexCircleCount = gt.getChromosome(2).getGene().intValue();
        config.secondFlexCircleDistance = gt.getChromosome(3).getGene().intValue();

        List<Integer> results = new ParallelGameTask(config, GameStateBuilder.build("03-2_zombies_redux"), 20).fork().join();

        if (results.stream().anyMatch(v -> v == 0)) {
            return 0;
        }
        else {
            return (int) results.stream().mapToInt(v -> v).average().getAsDouble();
        }
    }

    public static void main(String[] args) {
        Factory<Genotype<IntegerGene>> gtf = Genotype.of(
//            IntegerChromosome.of(1, 50),
            IntegerChromosome.of(3, 12),
            IntegerChromosome.of(500, 1000),
            IntegerChromosome.of(3, 9),
            IntegerChromosome.of(100, 400)
        );

        // 3.) Create the execution environment.
        Engine<IntegerGene, Integer> engine = Engine
            .builder(Evolve::eval, gtf)
            .populationSize(100)
            .build();

        EvolutionStatistics<Integer, ?> stats = EvolutionStatistics.ofNumber();

        // 4.) Start the execution (evolution) and
        //     collect the result.
        Genotype<IntegerGene> result = engine.stream()
            .limit(2)
            .peek(stats)
            .collect(EvolutionResult.toBestGenotype());

        System.out.println(stats);
        System.out.println(result);
    }
}
