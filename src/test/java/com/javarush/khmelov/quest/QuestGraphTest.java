package com.javarush.khmelov.quest;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestGraphTest {

    private static final List<String> REQUIRED_SCENES = List.of(
            QuestGraph.STOP,
            QuestGraph.RIDE,
            QuestGraph.MEETING,
            QuestGraph.CONVERSATION,
            QuestGraph.MEMORY,
            QuestGraph.PARTING,
            QuestGraph.ENDING_RECONNECT,
            QuestGraph.ENDING_GOODBYE,
            QuestGraph.ENDING_WALK);

    @Test
    void requiredScenesExist() {
        for (String id : REQUIRED_SCENES) {
            assertTrue(QuestGraph.exists(id), "нет сцены " + id);
        }
    }

    @Test
    void everyChoiceTargetExistsInEveryBranch() {
        QuestState byDoor = new QuestState();
        byDoor.setSatByWindow(false);
        QuestState walked = new QuestState();
        walked.setWalkTogether(true);

        for (Scene scene : QuestGraph.all()) {
            for (QuestState probe : List.of(new QuestState(), byDoor, walked)) {
                List<Choice> choices = scene.getChoices(probe);
                assertFalse(choices.isEmpty(), "тупик без выбора: " + scene.getId());
                for (Choice choice : choices) {
                    assertTrue(QuestGraph.exists(choice.getTarget()),
                            "битый переход " + scene.getId() + " -> " + choice.getTarget());
                }
            }
        }
    }

    @Test
    void endingsAreTerminalAndRestart() {
        for (String id : List.of(QuestGraph.ENDING_RECONNECT, QuestGraph.ENDING_GOODBYE,
                QuestGraph.ENDING_WALK)) {
            Scene scene = QuestGraph.find(id);
            assertEquals(1, scene.getChoices(new QuestState()).size(),
                    "у концовки должен быть один переход: " + id);
            assertEquals(QuestGraph.STOP, scene.getChoices(new QuestState()).get(0).getTarget());
        }
    }

    @Test
    void allScenesReachableFromStop() {
        Set<String> reached = new HashSet<>();
        walk(new QuestState(), reached, new HashSet<>());
        for (Scene scene : QuestGraph.all()) {
            assertTrue(reached.contains(scene.getId()), "недостижимая сцена: " + scene.getId());
        }
    }

    @Test
    void walkEndingOfferedOnlyAfterWalk() {
        QuestState withoutWalk = new QuestState();
        QuestState withWalk = new QuestState();
        withWalk.setWalkTogether(true);

        assertFalse(hasChoiceTo(withoutWalk, QuestGraph.PARTING, QuestGraph.ENDING_WALK),
                "прогулка не состоялась — прогулки быть не должно");
        assertTrue(hasChoiceTo(withWalk, QuestGraph.PARTING, QuestGraph.ENDING_WALK),
                "после прогулки должен появиться вариант про перекрёсток");
        assertEquals(3, QuestGraph.find(QuestGraph.PARTING).getChoices(withWalk).size());
        assertEquals(2, QuestGraph.find(QuestGraph.PARTING).getChoices(withoutWalk).size());
    }

    @Test
    void everyEndingReachableWithAndWithoutWalk() {
        for (boolean walk : List.of(false, true)) {
            assertEquals(QuestGraph.ENDING_RECONNECT,
                    play(walk, "Предложить обменяться контактами и как-нибудь встретиться").getEnding());
            assertEquals(QuestGraph.ENDING_GOODBYE,
                    play(walk, "Попрощаться и пойти домой").getEnding());
        }
        assertEquals(QuestGraph.ENDING_WALK,
                play(true, "Дойти вместе до перекрёстка").getEnding());
    }

    @Test
    void conversationWorkBranchLeadsToMemory() {
        QuestState state = new QuestState();
        choose(state, QuestGraph.STOP, "Сесть у окна");
        choose(state, QuestGraph.RIDE, "Дальше");
        choose(state, QuestGraph.MEETING, "Спросить, как у неё дела");
        choose(state, QuestGraph.CONVERSATION, "Спросить, чем она сейчас занимается");
        assertEquals("conversation_work", state.getScene());
        choose(state, "conversation_work", "Дальше");
        assertEquals(QuestGraph.MEMORY, state.getScene());
    }

    @Test
    void seatChoiceChangesRideText() {
        QuestState byWindow = new QuestState();
        QuestState byDoor = new QuestState();
        byDoor.setSatByWindow(false);
        assertNotEquals(QuestGraph.find(QuestGraph.RIDE).render(byWindow),
                QuestGraph.find(QuestGraph.RIDE).render(byDoor));
    }

    @Test
    void walkTogetherChangesPartingText() {
        QuestState withoutWalk = new QuestState();
        QuestState withWalk = new QuestState();
        withWalk.setWalkTogether(true);
        assertNotEquals(QuestGraph.find(QuestGraph.PARTING).render(withoutWalk),
                QuestGraph.find(QuestGraph.PARTING).render(withWalk));
    }

    @Test
    void walkTogetherChangesEndingsText() {
        QuestState withoutWalk = new QuestState();
        QuestState withWalk = new QuestState();
        withWalk.setWalkTogether(true);
        for (String id : List.of(QuestGraph.ENDING_RECONNECT, QuestGraph.ENDING_GOODBYE)) {
            assertNotEquals(QuestGraph.find(id).render(withoutWalk),
                    QuestGraph.find(id).render(withWalk), "текст не зависит от прогулки: " + id);
        }
    }

    @Test
    void restartResetsState() {
        QuestState state = play(true, "Предложить обменяться контактами и как-нибудь встретиться");
        Scene ending = QuestEngine.current(state);
        assertTrue(QuestEngine.step(state, ending.getId(), 0));
        assertEquals(QuestGraph.STOP, state.getScene());
        assertTrue(state.isSatByWindow());
        assertFalse(state.isWalkTogether());
        assertNull(state.getEnding());
    }

    @Test
    void repeatedSubmitDoesNotAdvanceScene() {
        QuestState state = new QuestState();
        Scene stop = QuestEngine.current(state);
        assertTrue(QuestEngine.step(state, stop.getId(), 0));
        assertEquals(QuestGraph.RIDE, state.getScene());

        assertFalse(QuestEngine.step(state, stop.getId(), 0));
        assertEquals(QuestGraph.RIDE, state.getScene());
    }

    @Test
    void invalidChoiceIsRejected() {
        QuestState state = new QuestState();
        assertFalse(QuestEngine.step(state, QuestGraph.STOP, -1));
        assertFalse(QuestEngine.step(state, QuestGraph.STOP, 999));
        assertFalse(QuestEngine.step(state, null, 0));
        assertEquals(QuestGraph.STOP, state.getScene());
    }

    @Test
    void staleFormFromOtherBranchIsRejected() {
        QuestState state = new QuestState();
        assertTrue(QuestEngine.step(state, QuestGraph.STOP, 0));
        assertEquals(QuestGraph.RIDE, state.getScene());
        assertFalse(QuestEngine.step(state, QuestGraph.STOP, 1));
        assertEquals(QuestGraph.RIDE, state.getScene());
    }

    @Test
    void sceneFromPreviousBuildStartsGameOver() {
        QuestState state = new QuestState();
        state.setScene("scene_removed_in_next_version");
        assertEquals(QuestGraph.STOP, QuestEngine.current(state).getId());
    }

    @Test
    void onlyRealEndingsAreEndings() {
        assertTrue(QuestGraph.isEnding(QuestGraph.ENDING_RECONNECT));
        assertTrue(QuestGraph.isEnding(QuestGraph.ENDING_WALK));
        assertFalse(QuestGraph.isEnding(QuestGraph.PARTING));
        assertFalse(QuestGraph.isEnding("ending_typo"));
        assertFalse(QuestGraph.isEnding(null));
    }

    private QuestState play(boolean walkTogether, String lastChoiceLabel) {
        QuestState state = new QuestState();
        choose(state, QuestGraph.STOP, "Сесть у окна");
        choose(state, QuestGraph.RIDE, "Дальше");
        choose(state, QuestGraph.MEETING, "Спросить, где она теперь живёт");
        choose(state, QuestGraph.CONVERSATION, "Посмотреть в окно и дать разговору идти своим чедом");
        choose(state, QuestGraph.MEMORY,
                walkTogether ? "Предложить пройти через двор" : "Сказать, что было приятно вспомнить");
        choose(state, QuestGraph.PARTING, lastChoiceLabel);
        return state;
    }

    private void choose(QuestState state, String sceneId, String label) {
        Scene scene = QuestGraph.find(sceneId);
        List<Choice> choices = scene.getChoices(state);
        int index = IntStream.range(0, choices.size())
                .filter(i -> choices.get(i).getLabel().equals(label))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "на сцене " + sceneId + " нет варианта «" + label + "»"));
        assertTrue(QuestEngine.step(state, sceneId, index), "переход не выполнен на сцене " + sceneId);
    }

    private boolean hasChoiceTo(QuestState state, String sceneId, String target) {
        return QuestGraph.find(sceneId).getChoices(state).stream()
                .anyMatch(choice -> target.equals(choice.getTarget()));
    }

    private void walk(QuestState state, Set<String> reached, Set<String> seenStates) {
        if (!seenStates.add(key(state))) {
            return;
        }
        reached.add(state.getScene());
        for (Choice choice : QuestGraph.find(state.getScene()).getChoices(state)) {
            QuestState next = copyOf(state);
            choice.apply(next);
            next.setScene(choice.getTarget());
            walk(next, reached, seenStates);
        }
    }

    private QuestState copyOf(QuestState state) {
        QuestState copy = new QuestState();
        copy.setScene(state.getScene());
        copy.setSatByWindow(state.isSatByWindow());
        copy.setWalkTogether(state.isWalkTogether());
        copy.setEnding(state.getEnding());
        return copy;
    }

    private String key(QuestState state) {
        return state.getScene() + "|" + state.isSatByWindow() + "|" + state.isWalkTogether()
                + "|" + state.getEnding();
    }
}