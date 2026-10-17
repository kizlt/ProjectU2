package com.javarush.khmelov.quest;

import java.util.List;

public class QuestEngine {

    private QuestEngine() {
    }

    public static Scene current(QuestState state) {
        try {
            return QuestGraph.find(state.getScene());
        } catch (IllegalArgumentException sceneGone) {
            state.reset();
            return QuestGraph.find(state.getScene());
        }
    }

    public static boolean step(QuestState state, String submittedScene, int choiceIndex) {
        Scene scene = current(state);
        if (!scene.getId().equals(submittedScene)) {
            return false;
        }
        List<Choice> choices = scene.getChoices(state);
        if (choiceIndex < 0 || choiceIndex >= choices.size()) {
            return false;
        }
        Choice choice = choices.get(choiceIndex);
        choice.apply(state);
        state.setScene(choice.getTarget());
        return true;
    }
}
