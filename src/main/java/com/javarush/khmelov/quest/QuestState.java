package com.javarush.khmelov.quest;

import java.io.Serializable;

public class QuestState implements Serializable {

    private static final long serialVersionUID = 1L;

    private String scene = QuestGraph.STOP;
    private boolean satByWindow = true;
    private boolean walkTogether;
    private String ending;

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public boolean isSatByWindow() {
        return satByWindow;
    }

    public void setSatByWindow(boolean satByWindow) {
        this.satByWindow = satByWindow;
    }

    public boolean isWalkTogether() {
        return walkTogether;
    }

    public void setWalkTogether(boolean walkTogether) {
        this.walkTogether = walkTogether;
    }

    public String getEnding() {
        return ending;
    }

    public void setEnding(String ending) {
        this.ending = ending;
    }

    public void reset() {
        scene = QuestGraph.STOP;
        satByWindow = true;
        walkTogether = false;
        ending = null;
    }
}