/**
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <${email}> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and
 * you think this stuff is worth it, you can buy me a beer in return.
 * ${author}.
 *
 * ---------------------------------------------------------------------
 *
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <${email}> schrieb diese Datei. Solange Sie diesen Vermerk nicht
 * entfernen, können Sie mit dem Material machen, was Sie möchten. Wenn
 * wir uns eines Tages treffen und Sie denken, das Material ist es wert,
 * können Sie mir dafür ein Bier ausgeben.
 * ${author}.
 */
package de.jufickel.guiff.swing;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.annotation.concurrent.ThreadSafe;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.commons.lang3.Validate;

import de.jufickel.utilities.l10n.L10n;

/**
 * @author Juergen Fickel
 * @version 24.12.2012
 */
@ThreadSafe
final class ExceptionDialogue implements Showable, Closable {

    private final L10n l10n;
    private final Throwable exception;
    private final JDialog dialogue;
    private final JScrollPane scrollableStackTraceTextArea;
    private final JButton closeButton;

    /**
     * @param theL10n
     * @param theException
     * @param theOptionPane
     * @param scrollableStackTraceTextArea
     * @param closeButton
     */
    private ExceptionDialogue(final L10n theL10n, final Throwable theException) {
        super();
        Validate.notNull(theL10n);
        Validate.notNull(theException);
        l10n = theL10n;
        exception = theException;
        scrollableStackTraceTextArea = createScrollableStackTraceTextArea();
        closeButton = createCloseButton();
        dialogue = createDialogue();
    }

    public static ExceptionDialogue newInstance(final L10n l10n, final Throwable exception) {
        return new ExceptionDialogue(l10n, exception);
    }

    private JScrollPane createScrollableStackTraceTextArea() {
        final short resultWidth = 350;
        final short resultHeight = 150;
        final JTextArea stackTraceTextArea = createStackTraceTextArea();
        final JScrollPane result = new JScrollPane(stackTraceTextArea);
        result.setPreferredSize(new Dimension(resultWidth, resultHeight));
        return result;
    }

    private JTextArea createStackTraceTextArea() {
        final byte stackTraceFontSize = 10;
        final JTextArea result = new JTextArea();
        result.setFont(new Font("Sans-Serif", Font.PLAIN, stackTraceFontSize));
        result.setEditable(false);
        final Writer writer = new StringWriter();
        exception.printStackTrace(new PrintWriter(writer));
        result.setText(writer.toString());
        return result;
    }

    private JButton createCloseButton() {
        final JButton result = new JButton(l10n.t("ExceptionDialogue.button_close.label"));
        result.setMnemonic(l10n.getMnemonicForResourceBundleKey("ExceptionDialogue.button_close.mnemonic"));
        result.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                close();
            }
        });
        return result;
    }

    private JDialog createDialogue() {
        final JOptionPane exceptionOptionPane = new JOptionPane();
        exceptionOptionPane.setMessage(new Object[] { exception.getMessage(), scrollableStackTraceTextArea });
        exceptionOptionPane.setMessageType(JOptionPane.ERROR_MESSAGE);
        exceptionOptionPane.setOptions(new JButton[] { closeButton });
        final JDialog result = exceptionOptionPane.createDialog(l10n.t("ExceptionDialogue.title"));
        result.setResizable(true);
        return result;
    }

    @Override
    public void close() {
        dialogue.setVisible(false);
        dialogue.dispose();
    }

    @Override
    public void show() {
        dialogue.setVisible(true);
    }

    @Override
    public boolean isShowing() {
        return dialogue.isVisible();
    }

}
