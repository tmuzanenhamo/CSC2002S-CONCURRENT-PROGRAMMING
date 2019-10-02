import javax.swing.*;
import java.util.Arrays;
import java.util.Comparator;
public class Controller {
    private Threads[] thread;
    private volatile boolean ended;
    private volatile boolean paused;
    private volatile boolean changed;
    private volatile boolean running;

    private final Object labelLock = new Object();

    public Controller() {
        super();
        this.thread = new Threads[WordApp.words.length];
        ended = true;
        paused = false;
        running = false;
    }

    public void updateScoreLabels() {
        synchronized (labelLock) {
            WordApp.labels[0].setText("Caught: " + WordApp.score.getCaught() + "    ");
            WordApp.labels[1].setText("Missed:" + WordApp.score.getMissed() + "    ");
            WordApp.labels[2].setText("Score:" + WordApp.score.getScore() + "    ");
            WordApp.labels[3].setText(
                    "Wrong Entries:" + WordApp.score.getIncorrectWords() + "    ");
            setChanged();
        }
    }
    public synchronized boolean checkWord(String text) {
        Arrays.sort(WordApp.words, new Comparator<WordRecord>() {
            @Override
            public int compare(WordRecord o1, WordRecord o2) {
                if (o1.equals(o2)) {
                    return 0;
                }
                if (o1.getY() > o2.getY()) {
                    return -1;
                }
                return 1;
            }
        });

        for (WordRecord word : WordApp.words) {
            if (word.matchWord(text)) {
                WordApp.score.caughtWord(text.length());
                updateScoreLabels();
                if (WordApp.score.getCaught() >= WordApp.totalWords) {
                    winGame();
                }
                return true;
            }
        }
        return false;
    }

    public void startWords() {
        ended = false;
        running = true;
        int index = 0;
        for (WordRecord word : WordApp.words) {
            thread[index] = new Threads(word, this);
            new Thread(thread[index]).start();
            index++;
        }
    }
    public synchronized void missedWord() {
        WordApp.score.missedWord();
        updateScoreLabels();
        if (WordApp.score.getMissed() >= 10) {
            stopGame();
            setChanged();
            JOptionPane.showMessageDialog(WordApp.w, "You Lost!\n" +
                    "You Scored: " + WordApp.score.getScore() +
                    "\nYou caught " + WordApp.score.getCaught() + " word(s)." +
                    "\nYou missed " + WordApp.score.getMissed() + " word(s)." +
                    "\nYou type " + WordApp.score.getIncorrectWords() +
                    " wrong word(s)");

            resetScore();
            WordApp.w.repaintOnce();
        }
    }

    public void resetScore() {
        WordApp.score.resetScore();
        updateScoreLabels();
    }

    public void stopGame() {
        for (Threads thread : thread) {
            thread.reset();
        }
        ended = true;
        running = false;
    }

    public void endGame() {
        stopGame();
        resetScore();
        WordApp.w.repaintOnce();
    }

    public boolean ended() {
        return ended;
    }

    public void winGame() {
        stopGame();
        setChanged();
        JOptionPane.showMessageDialog(WordApp.w, "Game Won Welldone!\n" +
                "You Scored: " + WordApp.score.getScore() +
                "\nYou caught " + WordApp.score.getCaught() + " word(s)." +
                "\nYou missed " + WordApp.score.getMissed() + " word(s)." +
                "\nYou typed " + WordApp.score.getIncorrectWords() +
                " wrong word(s)");
        resetScore();
        WordApp.w.repaintOnce();
    }

    public void setPaused() {
        paused = !paused;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isChanged() {
        return changed;
    }

    public synchronized void setUnchanged() {
        changed = false;
    }
    public synchronized void setChanged() {
        changed = true;
    }

    public boolean isRunning() {
        return running;
    }
}
