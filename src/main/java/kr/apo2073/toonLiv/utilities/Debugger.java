package kr.apo2073.toonLiv.utilities;

public class Debugger {
    private boolean isDebug=false;
    public Debugger(boolean isDebug) {
        this.isDebug=isDebug;
    }
    public void debug(String message) {
        System.out.println(message);
    }

    public boolean isDebug() {
        return isDebug;
    }
}
