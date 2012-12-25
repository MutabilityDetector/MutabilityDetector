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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;

import de.jufickel.utilities.l10n.L10n;

/**
 * @author Juergen Fickel
 * @version 23.12.2012
 */
@NotThreadSafe
final class OpenInputFileAction extends AbstractAction {

    private static final long serialVersionUID = -2777194871223341715L;

    private final JFileChooser inputFileChooser;
    private final Component parentOfFileChooser;
    private final EventHandler<File, Boolean> eventHandler;

    /**
     * Creates a new instance of this class.
     * 
     * @param closable
     *            a reference on an instance of {@link Closable}. This
     *            action works on this reference.
     * @param l10n
     *            a reference to the localisation facility to use
     */
    public OpenInputFileAction(final L10n l10n,
            final Component theParentOfFileChooser,
            final EventHandler<File, Boolean> theEventHandler) {
        super();
        Validate.notNull(l10n);
        Validate.notNull(theParentOfFileChooser);
        Validate.notNull(theEventHandler);
        parentOfFileChooser = theParentOfFileChooser;
        eventHandler = theEventHandler;
        inputFileChooser = createInputFileChooser(l10n);
        putValues(l10n);
    }

    private static JFileChooser createInputFileChooser(final L10n l10n) {
        final JFileChooser result = new JFileChooser();
        final FileNameExtensionFilter filter = new FileNameExtensionFilter(l10n.t("OpenInputFileAction.filter_desc"),
                "flv", "mp3", "mp4", "ogg");
        result.setFileFilter(filter);
        return result;
    }

    private void putValues(final L10n l10n) {
        putValue(Action.NAME, l10n.t("OpenInputFileAction.name"));
        putValue(Action.SHORT_DESCRIPTION, l10n.t("OpenInputFileAction.short_desc"));
        putValue(Action.MNEMONIC_KEY, l10n.getMnemonicForResourceBundleKey("OpenInputFileAction.mnemonic"));
        final IconFactory iconFactory = IconFactory.getInstance();
        putValue(Action.SMALL_ICON, iconFactory.smallOpenDocumentIcon());
        putValue(Action.LARGE_ICON_KEY, iconFactory.bigOpenDocumentIcon());
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final int returnValue = inputFileChooser.showOpenDialog(parentOfFileChooser);
        if (isApproved(returnValue)) {
            eventHandler.handleEvent(inputFileChooser.getSelectedFile());
        }
    }

    private static boolean isApproved(final int returnValue) {
        return JFileChooser.APPROVE_OPTION == returnValue;
    }

    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("inputFileChooser", inputFileChooser).append("parentOfFileChooser", parentOfFileChooser);
        builder.append("eventHandler", eventHandler);
        return builder.toString();
    }

}
