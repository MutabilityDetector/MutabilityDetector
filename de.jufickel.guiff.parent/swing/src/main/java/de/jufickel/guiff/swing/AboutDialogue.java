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
import java.awt.Frame;
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
final class AboutDialogue implements Showable {

    private final L10n l10n;
    private JButton buttonLizenz;
    private JButton buttonSchliessen;
    private JLabel labelLogo;
    private JLabel labelTitel;
    private JLabel labelUrheberrecht;
    private JPanel panelButtons;
    private JPanel panelMitte;
    private JScrollPane scrollPane1Beschreibung;
    private JTextArea textAreaBeschreibung;
    private final JDialog dialogue;
    private final LicenceDialogue dialogLizenz;

    public AboutDialogue(final Frame parent, final boolean modal, final L10n theL10n) {
        Validate.notNull(theL10n);
        l10n = theL10n;
        dialogue = new JDialog(parent, true);
        dialogLizenz = new LicenceDialogue(dialogue, false, l10n);
        initComponents();
    }

    private void initComponents() {
        panelMitte = new JPanel();
        labelTitel = new JLabel();
        labelLogo = new JLabel();
        scrollPane1Beschreibung = new JScrollPane();
        textAreaBeschreibung = new JTextArea();
        labelUrheberrecht = new JLabel();
        panelButtons = new JPanel();
        buttonLizenz = new JButton();
        buttonSchliessen = new JButton();

        dialogue.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialogue.setTitle(l10n.t("AboutDialogue.title"));
        dialogue.setMinimumSize(new java.awt.Dimension(450, 350));

        panelMitte.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        panelMitte.setAlignmentX(0.5F);
        panelMitte.setMinimumSize(new Dimension(350, 220));
        panelMitte.setPreferredSize(new Dimension(350, 220));
        panelMitte.setLayout(new BoxLayout(panelMitte, BoxLayout.Y_AXIS));

        labelTitel.setFont(new java.awt.Font("DejaVu Sans", 1, 18));
        labelTitel.setHorizontalAlignment(SwingConstants.CENTER);
        labelTitel.setText(l10n.t("AboutDialogue.version", "1.0.0"));
        labelTitel.setAlignmentX(0.5F);
        panelMitte.add(labelTitel);

        labelLogo.setHorizontalAlignment(SwingConstants.CENTER);
        labelLogo.setAlignmentX(0.5F);
        panelMitte.add(labelLogo);

        scrollPane1Beschreibung.setMinimumSize(null);

        textAreaBeschreibung.setColumns(20);
        textAreaBeschreibung.setEditable(false);
        textAreaBeschreibung.setLineWrap(true);
        textAreaBeschreibung.setRows(5);
        textAreaBeschreibung.setText(l10n.t("AboutDialogue.desc"));
        textAreaBeschreibung.setWrapStyleWord(true);
        textAreaBeschreibung.setAutoscrolls(true);
        textAreaBeschreibung.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        textAreaBeschreibung.setPreferredSize(null);
        scrollPane1Beschreibung.setViewportView(textAreaBeschreibung);

        panelMitte.add(scrollPane1Beschreibung);

        labelUrheberrecht.setHorizontalAlignment(SwingConstants.CENTER);
        labelUrheberrecht.setText(l10n.t("AboutDialogue.copyright"));
        labelUrheberrecht.setAlignmentX(0.5F);
        panelMitte.add(labelUrheberrecht);

        dialogue.getContentPane().add(panelMitte, BorderLayout.CENTER);

        panelButtons.setBorder(BorderFactory.createEmptyBorder(5, 1, 5, 1));

        buttonLizenz.setMnemonic(l10n.getMnemonicForResourceBundleKey("AboutDialogue.button_licence.mnemonic"));
        buttonLizenz.setText(l10n.t("AboutDialogue.button_licence.label"));
        buttonLizenz.setToolTipText(l10n.t("AboutDialogue.button_licence.short_desc"));
        buttonLizenz.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                buttonLizenzActionPerformed(evt);
            }
        });
        panelButtons.add(buttonLizenz);

        buttonSchliessen.setMnemonic(l10n.getMnemonicForResourceBundleKey("AboutDialogue.button_close.mnemonic"));
        buttonSchliessen.setText(l10n.t("AboutDialogue.button_close.label"));
        buttonSchliessen.setToolTipText(l10n.t("AboutDialogue.button_close.short_desc"));
        buttonSchliessen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                SchliessenHandler(evt);
            }
        });
        panelButtons.add(buttonSchliessen);

        dialogue.getContentPane().add(panelButtons, BorderLayout.PAGE_END);
    }

    private void SchliessenHandler(final ActionEvent evt) {
        dialogue.pack();
        dialogue.setVisible(false);
        dialogue.dispose();
    }

    private void buttonLizenzActionPerformed(final ActionEvent evt) {
        dialogLizenz.show();
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
