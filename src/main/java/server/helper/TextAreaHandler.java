package server.helper;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import javax.swing.JTextArea;

public class TextAreaHandler extends Handler {
    private JTextArea textArea;

    public TextAreaHandler(JTextArea textArea) {
        this.textArea = textArea;
        setFormatter(new SimpleFormatter()); // Thiết lập SimpleFormatter
    }

    @Override
    public void publish(LogRecord record) {
        if (textArea != null && isLoggable(record)) {
            String message = getFormatter().format(record);
            // Append log message to the JTextArea
            textArea.append(message);
        }
    }

    @Override
    public void flush() {
        // Not needed for JTextArea
    }

    @Override
    public void close() throws SecurityException {
        // Not needed for JTextArea
    }
}
