package com.javarush.khmelov.quest;

import java.util.List;
import java.util.function.Function;

public class Scene {

    private final String id;
    private final Function<QuestState, String> text;
    private final Function<QuestState, List<Choice>> choices;

    public Scene(String id, Function<QuestState, String> text,
                 Function<QuestState, List<Choice>> choices) {
        this.id = id;
        this.text = text;
        this.choices = choices;
    }

    public Scene(String id, Function<QuestState, String> text, List<Choice> choices) {
        this(id, text, state -> List.copyOf(choices));
    }

    public static Scene of(String id, String text, Choice... choices) {
        return new Scene(id, state -> text, List.of(choices));
    }

    public String getId() {
        return id;
    }

    public List<Choice> getChoices(QuestState state) {
        return choices.apply(state);
    }

    public String render(QuestState state) {
        return text.apply(state);
    }
}