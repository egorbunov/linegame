package org.spbstu;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import org.spbstu.linegame.logic.LogicListener;

/**
 * Created by Egor Gorbunov on 12.04.2015.
 * Email: egor-mailbox@ya.ru
 * Github username: egorbunov
 *
 * That is a delegating class. I use it to avoid touching the UI from not UI-thread.
 * Because I want for LineGameFragment to listen to the GameLogic, but some stuff in GameLogic is
 * happen in a separated thred (is a thinning thread in {@link org.spbstu.linegame.logic.LineGameLogic}).
 */
public class LogicEventToUiSender implements LogicListener {

    private enum CallbackName {
        ON_GAME_END,
        ON_GAME_PAUSED,
        ON_GAME_STARTED,
        ON_SCORE_CHANGED,
        ON_GAME_CONTINUED,
        ON_GAME_INITIALIZED
    }

    static class GameMsgHandler extends Handler {

        LogicListener listener = null;

        public void setListener(LogicListener logicListener) {
            listener = logicListener;
        }

        public void handleMessage(Message msg) {
            if (msg == null)
                return;

            if (msg.obj == null || ! (msg.obj instanceof CallbackName)) {
                Log.d(this.getClass().getCanonicalName(), "Strange message!");
                return;
            }

            if (listener != null) {
                switch ((CallbackName) msg.obj) {
                    case ON_GAME_CONTINUED:
                        listener.onGameContinued();
                        break;
                    case ON_GAME_END:
                        listener.onGameEnd();
                        break;
                    case ON_GAME_INITIALIZED:
                        listener.onGameInitialized();
                        break;
                    case ON_GAME_PAUSED:
                        listener.onGamePaused();
                        break;
                    case ON_GAME_STARTED:
                        listener.onGameStarted();
                        break;
                    case ON_SCORE_CHANGED:
                        listener.onDistanceChanged(msg.arg1);
                        break;
                }
            }
        }
    }

    private final GameMsgHandler handler = new GameMsgHandler();

    public LogicEventToUiSender() {}

    public void setHandlerLogicListener(LogicListener listener) {
        handler.setListener(listener);
    }

    @Override
    public void onGameEnd() {
        Message msg = new Message();
        msg.obj = CallbackName.ON_GAME_END;
        handler.sendMessage(msg);
    }

    @Override
    public void onGamePaused() {
        Message msg = new Message();
        msg.obj = CallbackName.ON_GAME_PAUSED;
        handler.sendMessage(msg);
    }

    @Override
    public void onGameStarted() {
        Message msg = new Message();
        msg.obj = CallbackName.ON_GAME_STARTED;
        handler.sendMessage(msg);
    }

    @Override
    public void onDistanceChanged(int newDistance) {
        Message msg = new Message();
        msg.obj = CallbackName.ON_SCORE_CHANGED;
        msg.arg1 = newDistance;
        handler.sendMessage(msg);
    }

    @Override
    public void onGameContinued() {
        Message msg = new Message();
        msg.obj = CallbackName.ON_GAME_CONTINUED;
        handler.sendMessage(msg);
    }

    @Override
    public void onGameInitialized() {
        Message msg = new Message();
        msg.obj = CallbackName.ON_GAME_INITIALIZED;
        handler.sendMessage(msg);
    }
}


