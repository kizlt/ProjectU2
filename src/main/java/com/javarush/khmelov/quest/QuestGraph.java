package com.javarush.khmelov.quest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QuestGraph {

    public static final String STOP = "stop";
    public static final String RIDE = "ride";
    public static final String MEETING = "meeting";
    public static final String CONVERSATION = "conversation";
    public static final String MEMORY = "memory";
    public static final String PARTING = "parting";
    public static final String ENDING_RECONNECT = "ending_reconnect";
    public static final String ENDING_GOODBYE = "ending_goodbye";
    public static final String ENDING_WALK = "ending_walk";

    private static final String CONVERSATION_WORK = "conversation_work";

    private static final Set<String> ENDINGS =
            Set.of(ENDING_RECONNECT, ENDING_GOODBYE, ENDING_WALK);

    private static final Map<String, Scene> SCENES = buildScenes();

    private QuestGraph() {
    }

    public static Scene find(String id) {
        Scene scene = SCENES.get(id);
        if (scene == null) {
            throw new IllegalArgumentException("Неизвестная сцена: " + id);
        }
        return scene;
    }

    public static boolean exists(String id) {
        return SCENES.containsKey(id);
    }

    public static boolean isEnding(String id) {
        return id != null && ENDINGS.contains(id);
    }

    public static List<Scene> all() {
        return List.copyOf(SCENES.values());
    }

    private static Map<String, Scene> buildScenes() {
        Map<String, Scene> scenes = new LinkedHashMap<>();

        scenes.put(STOP, Scene.of(STOP, """
                Вечером ты выходишь из магазина и идёшь к остановке. День был обычный и немного утомительный.
                В пакете продукты на ужин.

                Автобус приходит через несколько минут. Ты заходишь, находишь свободное место у окна и смотришь на улицы за стеклом.
                """,
                new Choice("Сесть у окна", RIDE, s -> s.setSatByWindow(true)),
                new Choice("Остаться у двери", RIDE, s -> s.setSatByWindow(false))));

        scenes.put(RIDE, new Scene(RIDE, state -> state.isSatByWindow() ? """
                Ты едешь и смотришь в окно. Дорога знакомая, вечер обычный.

                Через несколько остановок в салон заходит Лена.
                """ : """
                Ты стоишь у двери. Дорога знакомая, вечер обычный.

                Через несколько остановок в салон заходит Лена.
                """, List.of(Choice.go("Дальше", MEETING))));

        scenes.put(MEETING, Scene.of(MEETING, """
                Лена тоже присматривается, а потом улыбается.

                Лена: «Подожди… Это ты?»
                Ты: «Лена? Привет. Сколько лет».
                Лена: «Да уж. Ты далеко едешь?»
                Ты: «До своей остановки. А ты?»
                Лена: «Мне ещё пару остановок».

                Они немного удивлены встрече, но рады увидеться.
                """,
                Choice.go("Спросить, как у неё дела", CONVERSATION),
                Choice.go("Спросить, где она теперь живёт", CONVERSATION)));

        scenes.put(CONVERSATION, Scene.of(CONVERSATION, """
                Ты: «Ты всё ещё живёшь в том районе?»
                Лена: «Нет, переехала пару лет назад. Но иногда бываю там. А ты?»
                Ты: «Я тоже уже не так часто там появляюсь».
                Лена: «Странно. Кажется, совсем недавно мы после занятий постоянно там гуляли».
                Ты: «Ага. И каждый раз говорили, что в следующий раз пойдём куда-нибудь ещё».
                Лена: «Ну, иногда всё-таки ходили».
                """,
                Choice.go("Вспомнить один из тех вечеров", MEMORY),
                Choice.go("Спросить, чем она сейчас занимается", CONVERSATION_WORK),
                Choice.go("Посмотреть в окно и дать разговору идти своим чедом", MEMORY)));

        scenes.put(CONVERSATION_WORK, Scene.of(CONVERSATION_WORK, """
                Ты: «А ты чем сейчас занимаешься?»
                Лена: «Работаю в магазине. До этого три года была в офисе».
                Ты: «Нравится?»
                Лена: «Нормально. Смена недолгая, успеваешь выспаться».
                """,
                Choice.go("Дальше", MEMORY)));

        scenes.put(MEMORY, Scene.of(MEMORY, """
                Автобус проезжает знакомый поворот. За домами виден двор, где они раньше часто сидели.
                Обычный двор с деревьями, детской площадкой и скамейкой.

                — Я недавно проходила там, — говорит Лена. — Скамейку наконец поменяли.
                — Она совсем развалилась?
                — Почти. Но мы всё равно на ней сидели.
                — У нас был не самый большой выбор мест.
                — Зато рядом был магазин.
                """,
                new Choice("Предложить пройти через двор", PARTING, s -> s.setWalkTogether(true)),
                new Choice("Сказать, что было приятно вспомнить", PARTING, s -> s.setWalkTogether(false))));

        scenes.put(PARTING, new Scene(PARTING, state -> state.isWalkTogether() ? """
                Автобус подъезжает к твоей остановке. Лена встаёт.

                Лена: «Давай пройдёмся. Мне всё равно не к спеху».

                Вы выходите и идёте через двор. Разговариваете о работе, знакомых и планах на выходные.
                Иногда разговор ненадолго затихает, и это не мешает.
                """ : """
                Автобус подъезжает к твоей остановке. Лене выходить ещё рано.

                Лена: «Рада была тебя увидеть».
                Ты: «Я тоже. Правда».
                Лена: «Может, ещё пересечёмся».
                Ты: «Давай».
                """, state -> {
            List<Choice> choices = new ArrayList<>();
            choices.add(new Choice("Предложить обменяться контактами и как-нибудь встретиться",
                    ENDING_RECONNECT, s -> s.setEnding(ENDING_RECONNECT)));
            choices.add(new Choice("Попрощаться и пойти домой",
                    ENDING_GOODBYE, s -> s.setEnding(ENDING_GOODBYE)));
            if (state.isWalkTogether()) {
                choices.add(new Choice("Дойти вместе до перекрёстка",
                        ENDING_WALK, s -> s.setEnding(ENDING_WALK)));
            }
            return List.copyOf(choices);
        }));

        scenes.put(ENDING_RECONNECT, new Scene(ENDING_RECONNECT, state -> """
                Вы обмениваетесь контактами и договариваетесь написать друг другу на выходных.
                Точных планов пока нет, но вы решаете не откладывать встречу на неопределённое время.

                %s. По дороге проверяешь телефон: она уже прислала сообщение — «Рада была увидеться».

                Дома ты ставишь пакет на кухонный стол и начинаешь готовить ужин.
                Вечер продолжается, но теперь у тебя есть небольшой план на выходные.
                """.formatted(state.isWalkTogether()
                ? "Лена уходит своей дорогой, а ты идёшь домой"
                : "Лена садится в следующий автобус, а ты идёшь домой"),
                List.of(new Choice("Начать заново", STOP, QuestState::reset))));

        scenes.put(ENDING_GOODBYE, new Scene(ENDING_GOODBYE, state -> """
                Вы тепло прощаетесь. %s.

                По дороге ты ещё немного думаешь о разговоре. Ничего особенного не произошло —
                просто приятно было увидеть знакомого человека и узнать, что у неё всё в порядке.

                Дома ты разбираешь покупки, ставишь чайник и ненадолго открываешь окно.
                В комнате постепенно становится тихо.
                """.formatted(state.isWalkTogether()
                ? "Лена уходит своей дорогой, а ты идёшь домой"
                : "Лена едет дальше, а ты идёшь домой"),
                List.of(new Choice("Начать заново", STOP, QuestState::reset))));

        scenes.put(ENDING_WALK, Scene.of(ENDING_WALK, """
                Вы идёте через двор, не торопясь. Лена рассказывает о новой работе, ты — о том, чем занят в последнее время.
                Иногда разговор ненадолго затихает, и это не мешает.

                У перекрёстка вы останавливаетесь. Лене нужно идти в другую сторону.

                — Было здорово немного пройтись, — говорит она.
                — Да. Надо будет повторить.
                — Договорились.

                Вы прощаетесь. Ты идёшь домой знакомой дорогой.
                Впереди обычный вечер, ужин и, может быть, фильм.
                """, new Choice("Начать заново", STOP, QuestState::reset)));

        validate(scenes);
        return scenes;
    }

    private static void validate(Map<String, Scene> scenes) {
        QuestState byWindow = new QuestState();
        QuestState byDoor = new QuestState();
        byDoor.setSatByWindow(false);
        QuestState walked = new QuestState();
        walked.setWalkTogether(true);
        QuestState[] probes = {byWindow, byDoor, walked};

        for (Scene scene : scenes.values()) {
            for (QuestState probe : probes) {
                List<Choice> choices = scene.getChoices(probe);
                if (choices.isEmpty()) {
                    throw new IllegalStateException("Сцена без вариантов: " + scene.getId());
                }
                for (Choice choice : choices) {
                    if (!scenes.containsKey(choice.getTarget())) {
                        throw new IllegalStateException(
                                "Битый переход " + scene.getId() + " -> " + choice.getTarget());
                    }
                }
            }
        }
    }
}