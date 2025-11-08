package kr.apo2073.api.toonLiv.utilities;

public class Debugger {
    private boolean isDebug=false;
    public Debugger(boolean isDebug) {
        this.isDebug=isDebug;
    }
    public static void debug(String message) {
        System.out.println(message);
    }

    public boolean isDebug() {
        return isDebug;
    }
}
