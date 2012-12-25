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

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.AbstractAction;
import javax.swing.Action;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;

import de.jufickel.utilities.l10n.L10n;

/**
 * @author Juergen Fickel
 * @version 24.12.2012
 */
@NotThreadSafe
final class AboutAction extends AbstractAction {

    private static final long serialVersionUID = -2472317310149421867L;

    private final L10n l10n;
    private final Frame parent;
    private Showable aboutDialogue;

    public AboutAction(final L10n theL10n, final Frame theParent) {
        super();
        Validate.notNull(theL10n);
        Validate.notNull(theParent);
        l10n = theL10n;
        parent = theParent;
        aboutDialogue = null;
        putValues();
    }

    private void putValues() {
        putValue(Action.NAME, l10n.t("AboutAction.name"));
        putValue(Action.SHORT_DESCRIPTION, l10n.t("AboutAction.short_desc"));
        putValue(Action.MNEMONIC_KEY, l10n.getMnemonicForResourceBundleKey("AboutAction.mnemonic"));
        final IconFactory iconFactory = IconFactory.getInstance();
        putValue(Action.SMALL_ICON, iconFactory.smallAboutIcon());
        putValue(Action.LARGE_ICON_KEY, iconFactory.bigAboutIcon());
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        synchronized (this) {
            if (null == aboutDialogue) {
                aboutDialogue = new AboutDialogue(parent, true, l10n);
            }
        }
        aboutDialogue.show();
    }

    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("l10n", l10n).append("parent", parent).append("aboutDialogue", aboutDialogue);
        return builder.toString();
    }

}
