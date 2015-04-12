package org.spbstu.linegame.model.curve;

public abstract  class TapableObject {
    boolean isTapped;

    public TapableObject() {
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
