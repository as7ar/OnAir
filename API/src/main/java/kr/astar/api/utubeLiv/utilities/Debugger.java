package kr.astar.api.utubeLiv.utilities;

public class Debugger {
    public boolean isDebug=false;
    public void log(String message) {
        if (!isDebug) return;
        System.out.printf("[ YoutubeLiv ] %s%n",message);
    }
}
