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

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.*;
import javax.swing.SwingWorker.StateValue;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import de.jufickel.guiff.core.Controller;
import de.jufickel.guiff.core.FfmpegController;
import de.jufickel.guiff.core.Quality;
import de.jufickel.utilities.l10n.L10n;

/**
 * @author Juergen Fickel
 * @version 23.12.2012
 */
@NotThreadSafe
public final class MainWindow implements Closable {

    @NotThreadSafe
    private static final class InputDataStructure {
        private File inputFile = null;
        private Quality selectedQuality = Quality.MP3.NORMAL;
    }

    @NotThreadSafe
    private static final class OpenInputFileTransferHandler extends TransferHandler {

        private static final long serialVersionUID = 1334829138453496593L;

        private final JButton openInputFileButton;
        private final EventHandler<File, Boolean> inputFileSelectedHandler;
        private final DataFlavor uriDataFlavor;

        public OpenInputFileTransferHandler(final JButton theOpenInputFileButton,
                final EventHandler<File, Boolean> theInputFileSelectedHandler) {
            Validate.notNull(theOpenInputFileButton);
            Validate.notNull(theInputFileSelectedHandler);
            openInputFileButton = theOpenInputFileButton;
            inputFileSelectedHandler = theInputFileSelectedHandler;
            uriDataFlavor = tryToCreateUriDataFlavor();
        }

        private static DataFlavor tryToCreateUriDataFlavor() {
            final String mimeType = "text/uri-list;class=java.lang.String";
            try {
                return createUriDataFlavor(mimeType);
            } catch (final ClassNotFoundException e) {
                final String msg = "Unable to create DataFlavor for mime type '%s'.";
                throw illegalStateException(String.format(msg, mimeType), e);
            }
        }

        private static DataFlavor createUriDataFlavor(final String mimeType) throws ClassNotFoundException {
            return new DataFlavor(mimeType);
        }

        private static RuntimeException illegalStateException(final String message, final Throwable cause) {
            final RuntimeException result = new IllegalStateException(message);
            result.initCause(cause);
            return result;
        }

        @Override
        public boolean canImport(final TransferSupport support) {
            if (openInputFileButton.equals(support.getComponent())) {
                return true;
            }
            return false;
        }

        @Override
        public boolean importData(final TransferSupport support) {
            // FIXME does not work under Windows (probably another DataFlavor is necessar.
            if (support.isDrop()) {
                final Transferable transferable = support.getTransferable();
                final String fileUriString = tryToGetTransferableDataAsString(transferable);
                return openFileWithUriIfPossible(fileUriString);
            }
            return false;
        }

        private String tryToGetTransferableDataAsString(final Transferable transferable) {
            try {
                return getTransferableDataAsString(transferable);
            } catch (final UnsupportedFlavorException e) {
                System.err.println(e);
            } catch (final IOException e) {
                System.err.println(e);
            }
            return null;
        }

        private String getTransferableDataAsString(final Transferable transferable) throws UnsupportedFlavorException,
                IOException {
            final String result = (String) transferable.getTransferData(uriDataFlavor);
            return StringUtils.chop(result);
        }

        private boolean openFileWithUriIfPossible(final String fileUriString) {
            if (gotFileUri(fileUriString)) {
                final URI fileUri = URI.create(fileUriString);
                final File inputFile = new File(fileUri);
                return inputFileSelectedHandler.handleEvent(inputFile);
            }
            return false;
        }

        private static boolean gotFileUri(final String fileUri) {
            return null != fileUri && !fileUri.isEmpty();
        }

    }

    @NotThreadSafe
    private final class InputFileSelectedHandler implements EventHandler<File, Boolean> {

        @Override
        public Boolean handleEvent(final File selectedFile) {
            final boolean result = null != selectedFile;
            if (result) {
                inputData.inputFile = selectedFile;
                removeComponentFromCenterOfMainFrame(openInputFileButton);
                addComponentToCenterOfMainFrame(qualitySlider);
                addComponentToSouthOfMainFrame(startButton);
            }
            return result;
        }

    }

    @NotThreadSafe
    private final class QualitySliderChangeHandler implements ChangeListener {

        @Override
        public void stateChanged(final ChangeEvent e) {
            final int selectedQualityValue = qualitySlider.getValue();
            inputData.selectedQuality = Quality.MP3.values()[selectedQualityValue];
        }

    }

    @NotThreadSafe
    private final class ExtractionTask extends SwingWorker<Integer, Object> {

        @Override
        protected Integer doInBackground() throws Exception {
            final int result;
            publish(new Object[] {});
            final Controller controller = FfmpegController.newInstance(inputData.inputFile, inputData.selectedQuality);
            if (controller.isMp3AudioStream()) {
                result = controller.copyMp3AudioStream();
            } else {
                result = controller.extractAudioStreamAndEncodeAsMp3();
            }
            return Integer.valueOf(result);
        }

        @Override
        protected void process(final List<Object> chunks) {
            removeToolbarFromMainFrame();
            removeComponentFromCenterOfMainFrame(qualitySlider);
            removeComponentFromCenterOfMainFrame(startButton);
            addCenteredLabelToMainFrame();
            setWorkingCursor();
        }

        private void addCenteredLabelToMainFrame() {
            final JPanel labelPanel = new JPanel();
            final GridBagLayout gridbag = new GridBagLayout();
            final GridBagConstraints constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.CENTER;
            gridbag.setConstraints(labelPanel, constraints);
            labelPanel.setLayout(gridbag);
            labelPanel.add(statusLabel);
            addComponentToCenterOfMainFrame(labelPanel);
        }

        @Override
        protected void done() {
            setDefaultCursor();
        }

    }

    private final class ExtractionTaskListener implements PropertyChangeListener {

        @Override
        public void propertyChange(final PropertyChangeEvent event) {
            final StateValue stateValue = (StateValue) event.getNewValue();
            if (isStateDone(stateValue)) {
                handleExtractionResult();
            }
        }

        private boolean isStateDone(final StateValue stateValue) {
            return StateValue.DONE == stateValue;
        }

        private void handleExtractionResult() {
            final int result = tryToGetExtractionResult();
            if (finishedCorrectly(result)) {
                setStatusLabelText(l10n.t("MainWindow.label.finished"));
            } else {
                setStatusLabelText(l10n.t("MainWindow.label.failed"));
            }
            addCloseButtonToMainFrame();
        }

        private int tryToGetExtractionResult() {
            try {
                return extractionTask.get();
            } catch (final InterruptedException e) {
                handleExtractionException(e);
            } catch (final ExecutionException e) {
                handleExtractionException(e);
            }
            return -1;
        }

        private boolean finishedCorrectly(final int ffmpegExitValue) {
            return 0 == ffmpegExitValue;
        }

        private void addCloseButtonToMainFrame() {
            final JButton closeButton = new JButton(closeAction);
            addComponentToSouthOfMainFrame(closeButton);
            closeButton.requestFocus();
        }

        private void handleExtractionException(final Throwable exception) {
            System.err.println(exception);
            setStatusLabelText("");
            addCloseButtonToMainFrame();
            showExceptionDialogue(exception);
        }

        private void showExceptionDialogue(final Throwable exception) {
            final Showable exceptionDialogue = ExceptionDialogue.newInstance(l10n, exception);
            exceptionDialogue.show();
        }
    }

    private final L10n l10n;
    private final JFrame mainFrame;
    private final InputDataStructure inputData;
    private final Action closeAction;
    private final JToolBar toolbar;
    private final JButton openInputFileButton;
    private final JSlider qualitySlider;
    private final JButton startButton;
    private final SwingWorker<Integer, Object> extractionTask;
    private final JLabel statusLabel;

    /**
     * Create the application.
     */
    private MainWindow() {
        l10n = L10n.Default.getInstance("de.jufickel.guiff.swing.messages");
        mainFrame = createMainFrame();
        inputData = new InputDataStructure();
        closeAction = new CloseAction(this, l10n);
        toolbar = createToolbar();
        mainFrame.getContentPane().add(toolbar, BorderLayout.NORTH);
        openInputFileButton = createOpenInputFileButton();
        addComponentToCenterOfMainFrame(openInputFileButton);
        qualitySlider = createQualitySlider();
        extractionTask = createExtractionTask();
        startButton = createStartButton();
        statusLabel = new JLabel(l10n.t("MainWindow.label.working"));
    }

    public static MainWindow newInstance() {
        return new MainWindow();
    }

    /**
     * Initialize the contents of the frame.
     */
    private JFrame createMainFrame() {
        final JFrame result = new JFrame();
        result.setTitle("guiff");
        result.setBounds(100, 100, 450, 300);
        result.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return result;
    }

    private JToolBar createToolbar() {
        final JToolBar result = new JToolBar();
        result.add(closeAction);
        result.add(new AboutAction(l10n, mainFrame));
        return result;
    }

    private JButton createOpenInputFileButton() {
        final EventHandler<File, Boolean> inputFileSelectedHandler = new InputFileSelectedHandler();
        final Action openInputFileAction = new OpenInputFileAction(l10n, mainFrame, inputFileSelectedHandler);
        final JButton result = new JButton(openInputFileAction);
        result.setTransferHandler(new OpenInputFileTransferHandler(result, inputFileSelectedHandler));
        return result;
    }

    private void addComponentToCenterOfMainFrame(final Component component) {
        final Container contentPane = mainFrame.getContentPane();
        contentPane.add(component, BorderLayout.CENTER);
        contentPane.validate();
    }

    private void addComponentToSouthOfMainFrame(final Component component) {
        final Container contentPane = mainFrame.getContentPane();
        contentPane.add(component, BorderLayout.SOUTH);
        contentPane.validate();
    }

    private void removeComponentFromCenterOfMainFrame(final Component component) {
        final Container contentPane = mainFrame.getContentPane();
        contentPane.remove(component);
        contentPane.validate();
    }

    private JSlider createQualitySlider() {
        final int min = Quality.MP3.LOW.ordinal();
        final int max = Quality.MP3.HIGH.ordinal();
        final int initialValue = Quality.MP3.NORMAL.ordinal();

        final JSlider result = new JSlider(min, max, initialValue);
        result.setMinorTickSpacing(1);
        result.setPaintTicks(true);
        final Dictionary<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put(Integer.valueOf(min), new JLabel(l10n.t("MainWindow.qualitySlider.low")));
        labelTable.put(Integer.valueOf(initialValue), new JLabel(l10n.t("MainWindow.qualitySlider.normal")));
        labelTable.put(Integer.valueOf(max), new JLabel(l10n.t("MainWindow.qualitySlider.high")));
        result.setLabelTable(labelTable);
        result.setPaintLabels(true);
        result.addChangeListener(new QualitySliderChangeHandler());
        return result;
    }

    private SwingWorker<Integer, Object> createExtractionTask() {
        final SwingWorker<Integer, Object> result = new ExtractionTask();
        result.addPropertyChangeListener(new ExtractionTaskListener());
        return result;
    }

    private JButton createStartButton() {
        return new JButton(new StartExtractionAction(l10n, extractionTask));
    }

    private void removeToolbarFromMainFrame() {
        final Container contentPane = mainFrame.getContentPane();
        contentPane.remove(toolbar);
        contentPane.validate();
    }

    private void setStatusLabelText(final String textToSet) {
        Validate.notNull(textToSet);
        statusLabel.setText(textToSet);
        statusLabel.validate();
    }

    private void setWorkingCursor() {
        mainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    private void setDefaultCursor() {
        mainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void close() {
        mainFrame.setVisible(false);
        mainFrame.dispose();
        System.exit(0);
    }

    /**
     * Launch the application.
     * 
     * @throws UnsupportedLookAndFeelException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    public static void main(final String[] args) throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, UnsupportedLookAndFeelException {
        LookAndFeelInfo metal = null;
        try {
            for (final LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                } else if ("Metal".equals(info.getName())) {
                    metal = info;
                }
            }
        } catch (final Exception e) {
            // If Nimbus is not available, use Metal.
            UIManager.setLookAndFeel(metal.getClassName());
        }
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    final MainWindow window = new MainWindow();
                    window.mainFrame.setVisible(true);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
