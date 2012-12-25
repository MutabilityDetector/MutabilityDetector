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
package de.jufickel.guiff.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 *
 * @author Juergen Fickel
 * @version 23.12.2012
 */
public final class DefaultControllerTest {

    private final File inputFile;
    private final File expectedOutputFile;

    private FfmpegController controller;

    public DefaultControllerTest() {
        super();
        final URL resource = getClass().getResource("GrooveCoverage_TheEnd.flv");
        inputFile = new File(resource.getPath());
        expectedOutputFile = new File(inputFile.getParentFile(), "GrooveCoverage_TheEnd.mp3");
        nullifyController();
    }

    @Before
    public void setUpController() {
        controller = FfmpegController.newInstance(inputFile, Quality.MP3.NORMAL);
    }

    @After
    public void nullifyController() {
        controller = null;
    }

    @Test
    public void inputFileContainsMp3AudioStream() {
        assertThat(controller.isMp3AudioStream(), is(true));
    }

    @Test
    public void copyAudioStreamToOutputFile() {
        controller.copyMp3AudioStream();
        assertOutputFileCreationAndDeletion();
    }

    private void assertOutputFileCreationAndDeletion() {
        assertThat(expectedOutputFile.exists(), is(true));
        assertThat(expectedOutputFile.delete(), is(true));
    }

    @Test
    public void extractAndEncodeAudioStreamToOutputFile() {
        controller.extractAudioStreamAndEncodeAsMp3();
        assertOutputFileCreationAndDeletion();
    }

}
