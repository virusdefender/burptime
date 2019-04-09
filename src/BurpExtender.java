package burp;

import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;

public class BurpExtender implements IBurpExtender, IProxyListener {
    private PrintWriter stdout;
    private ConcurrentHashMap<Integer, Long> timeMap = new ConcurrentHashMap<>();
    private IBurpExtenderCallbacks callbacks;

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        this.callbacks = callbacks;
        callbacks.setExtensionName("Burp Show Response Time");
        stdout = new PrintWriter(callbacks.getStdout(), true);
        callbacks.registerProxyListener(this);
    }

    @Override
    public void processProxyMessage(boolean messageIsRequest, IInterceptedProxyMessage message) {
        int messageRef = message.getMessageReference();
        long now = System.currentTimeMillis();
        if (messageIsRequest) {
            this.timeMap.put(messageRef, now);
        } else {
            long delta = now - this.timeMap.get(messageRef);
            this.timeMap.remove(messageRef);
            message.getMessageInfo().setComment(Long.toString(delta) + " ms");
        }
    }
}