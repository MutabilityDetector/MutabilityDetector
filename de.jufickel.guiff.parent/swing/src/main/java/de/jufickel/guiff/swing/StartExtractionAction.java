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

import java.awt.event.ActionEvent;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingWorker;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;

import de.jufickel.utilities.l10n.L10n;

/**
 * @author Juergen Fickel
 * @version 24.12.2012
 */
@NotThreadSafe
final class StartExtractionAction extends AbstractAction {

    private static final long serialVersionUID = 3215334607955357025L;

    private final SwingWorker<Integer, Object> extractionTask;

    public StartExtractionAction(final L10n l10n, final SwingWorker<Integer, Object> theExtractionTask) {
        super();
        Validate.notNull(l10n);
        Validate.notNull(theExtractionTask);
        extractionTask = theExtractionTask;
        putValues(l10n);
    }

    private void putValues(final L10n l10n) {
        putValue(Action.NAME, l10n.t("StartExtractionAction.name"));
        putValue(Action.SHORT_DESCRIPTION, l10n.t("StartExtractionAction.short_desc"));
        putValue(Action.MNEMONIC_KEY, l10n.getMnemonicForResourceBundleKey("StartExtractionAction.mnemonic"));
        final IconFactory iconFactory = IconFactory.getInstance();
        putValue(Action.SMALL_ICON, iconFactory.smallStartHereIcon());
        putValue(Action.LARGE_ICON_KEY, iconFactory.bigStartHereIcon());
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        extractionTask.execute();
    }

    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("extractionTask", extractionTask);
        return builder.toString();
    }

}
