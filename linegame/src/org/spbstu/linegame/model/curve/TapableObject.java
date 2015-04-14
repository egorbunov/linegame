package org.spbstu.linegame.model.curve;

public abstract  class TapableObject {
    private boolean isTapped;

    TapableObject() {
        isTapped = false;
    }

    public void setTapped() {
        isTapped = true;
    }

    public void setNotTapped() {
        isTapped = false;
    }

    public boolean isTapped() {
        return isTapped;
    }
}
