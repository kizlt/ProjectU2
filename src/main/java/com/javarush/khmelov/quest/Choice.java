package com.javarush.khmelov.quest;

import java.util.Objects;
import java.util.function.Consumer;

public class Choice {

    private final String label;
    private final String target;
    private final Consumer<QuestState> effect;

    public Choice(String label, String target, Consumer<QuestState> effect) {
        this.label = Objects.requireNonNull(label, "label");
        this.target = Objects.requireNonNull(target, "target");
        this.effect = effect != null ? effect : state -> {
        };
    }

    public static Choice go(String label, String target) {
        return new Choice(label, target, null);
    }

    public String getLabel() {
        return label;
    }

    public String getTarget() {
        return target;
    }

    public void apply(QuestState state) {
        effect.accept(state);
    }
}
