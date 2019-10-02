public class Threads implements Runnable {
    private WordRecord word;
    private Controller controller;
    private int divisor = 10;
    public Threads(WordRecord word, Controller controller) {
        super();
        this.word = word;
        this.controller = controller;
    }

    public synchronized void reset() {
        word.resetWord();
    }
    @Override
    public void run() {
        while (!controller.ended()) {
            if (word.missed()) {
                controller.missedWord();
                word.resetWord();
                controller.setChanged();
            } else if (controller.isPaused()) {
                continue;
            } else {
                word.drop(1);
                controller.updateScoreLabels();
            }
            try {
                Thread.sleep(word.getSpeed() / divisor);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
