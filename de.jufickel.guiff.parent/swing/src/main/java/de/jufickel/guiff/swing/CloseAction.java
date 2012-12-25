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

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;

import de.jufickel.utilities.l10n.L10n;

/**
 * @author Juergen Fickel
 * @version 24.12.2012
 */
final class CloseAction extends AbstractAction {

    private static final long serialVersionUID = 3340537962941698634L;

    private final Closable closable;

    /**
     * Creates a new instance of this class.
     * 
     * @param closable
     *            a reference on an instance of {@link Closable}. This
     *            action works on this reference.
     * @param l10n
     *            a reference to the localisation facility to use
     */
    public CloseAction(final Closable aClosable, final L10n l10n) {
        super();
        Validate.notNull(aClosable);
        Validate.notNull(l10n);
        closable = aClosable;
        putValue(Action.NAME, l10n.t("CloseAction.name"));
        putValue(Action.SHORT_DESCRIPTION, l10n.t("CloseAction.short_desc"));
        putValue(Action.MNEMONIC_KEY, l10n.getMnemonicForResourceBundleKey("CloseAction.mnemonic"));
        final IconFactory iconFactory = IconFactory.getInstance();
        putValue(Action.SMALL_ICON, iconFactory.smallCloseIcon());
        putValue(Action.LARGE_ICON_KEY, iconFactory.bigCloseIcon());
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        synchronized (closable) {
            closable.notify();
        }
        closable.close();
    }

    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("closable", closable);
        return builder.toString();
    }

}
