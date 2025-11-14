package kr.astar.api.soopliv.utilities;

public class Debugger {
    public boolean isDebug=false;
    public void log(String message) {
        if (!isDebug) return;
        System.out.printf("[ SoopLiv ] %s%n",message);
    }
}
