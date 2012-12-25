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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.*;

import org.apache.commons.lang3.Validate;

import de.jufickel.utilities.l10n.L10n;

/**
 * @author Juergen Fickel
 * @version 24.12.2012
 */
@NotThreadSafe
public class LicenceDialogue implements Showable, Closable {

    private final L10n l10n;
    private JButton buttonSchliessen;
    private JScrollPane jScrollPane1;
    private JPanel panelSchliessen;
    private JTextArea textAreaLizenz;
    private final JDialog dialogue;

    public LicenceDialogue(final JDialog owner, final boolean modal, final L10n theL10n) {
        Validate.notNull(theL10n);
        l10n = theL10n;
        dialogue = new JDialog(owner, modal);
        initComponents();
    }

    private void initComponents() {
        jScrollPane1 = new JScrollPane();
        textAreaLizenz = new JTextArea();
        panelSchliessen = new JPanel();
        buttonSchliessen = new JButton();

        dialogue.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialogue.setTitle(l10n.t("LicenceDialogue.title"));
        dialogue.setMinimumSize(new Dimension(400, 320));

        jScrollPane1.setBorder(BorderFactory.createEmptyBorder(10, 10, 1, 10));
        jScrollPane1.setAutoscrolls(true);

        textAreaLizenz.setColumns(20);
        textAreaLizenz.setEditable(false);
        textAreaLizenz.setLineWrap(true);
        textAreaLizenz.setRows(5);
        textAreaLizenz.setText(l10n.t("LicenceDialogue.licence_text")); // NOI18N
        textAreaLizenz.setWrapStyleWord(true);
        textAreaLizenz.setMinimumSize(new Dimension(200, 200));
        jScrollPane1.setViewportView(textAreaLizenz);

        dialogue.getContentPane().add(jScrollPane1, BorderLayout.CENTER);

        panelSchliessen.setBorder(BorderFactory.createEmptyBorder(5, 1, 5, 1));

        buttonSchliessen.setMnemonic('c');
        buttonSchliessen.setText("Schließen");
        buttonSchliessen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                buttonSchliessenActionPerformed(evt);
            }
        });
        panelSchliessen.add(buttonSchliessen);

        dialogue.getContentPane().add(panelSchliessen, BorderLayout.PAGE_END);
        dialogue.pack();
    }

    private void buttonSchliessenActionPerformed(final ActionEvent evt) {
        close();
    }

    @Override
    public void close() {
        dialogue.setVisible(false);
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
