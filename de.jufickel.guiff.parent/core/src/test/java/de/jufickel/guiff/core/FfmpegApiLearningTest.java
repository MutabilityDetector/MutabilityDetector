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

import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 *
 * @author Juergen Fickel
 * @version 22.12.2012
 */
public final class FfmpegApiLearningTest {

    private static final String FFMPEG = "ffmpeg";
    private static final String FFPROBE = "ffprobe";

    private final URL resource = getClass().getResource("GrooveCoverage_TheEnd.flv");

    @Test
    public void audioStreamCodecIsMp3() throws IOException, InterruptedException, ParserConfigurationException,
            SAXException {
        final String inputFilePath = resource.getPath();

        /*
         * ffprobe -v quiet -print_format xml -show_streams -i GrooveCoverage_TheEnd.flv
         */
        final ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(FFPROBE, "-v", "quiet", "-print_format", "xml", "-show_streams", "-i", inputFilePath);
        final Process ffprobe = processBuilder.start();
        ffprobe.waitFor();

        // SAX
        final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        final SAXParser saxParser = saxParserFactory.newSAXParser();
        final StreamCodecExtractor audioStreamCodecExtractor = AudioStreamCodecHandler.newInstance();
        saxParser.parse(ffprobe.getInputStream(), (DefaultHandler) audioStreamCodecExtractor);
        assertThat(audioStreamCodecExtractor.getStreamCodecs(), everyItem(is(Codec.MP3)));
    }

    @Test
    public void copyMp3AudioStreamWithFfmpeg() throws IOException, InterruptedException {
        /*
         * ffmpeg -i INPUT -vn -acodec copy OUTPUT
         */
        final ProcessBuilder processBuilder = new ProcessBuilder();
        final File inputFile = new File(resource.getPath());
        final File outputFile = new File(inputFile.getParentFile(), "GrooveCoverage_TheEnd.mp3");
        processBuilder.command(FFMPEG, "-i", inputFile.getAbsolutePath(), "-vn", "-acodec", "copy",
                outputFile.getAbsolutePath());
        final Process ffmpeg = processBuilder.start();
        ffmpeg.waitFor();
        assertThat(outputFile.exists(), is(true));
        assertThat(outputFile.delete(), is(true));
    }

    @Test
    public void substituteFileExtension() {
        final File inputFile = new File(resource.getPath()); 
        final String inputFileNameWithExtension = inputFile.getName();
        final int beginOfFileExtension = inputFileNameWithExtension.lastIndexOf('.');
        final String inputFileNameWithoutExtension = inputFileNameWithExtension.substring(0, beginOfFileExtension);
        assertThat(inputFileNameWithoutExtension, is("GrooveCoverage_TheEnd"));
    }

    @Test
    public void extractAndEncodeAudioStreamWithFfmpeg() throws IOException, InterruptedException {
        /*
         * ffmpeg -i source_video.avi -vn -ar 44100 -ac 2 -ab 192 -f mp3 sound.mp3
         */
        final ProcessBuilder processBuilder = new ProcessBuilder();
        final File inputFile = new File(resource.getPath());
        final File outputFile = new File(inputFile.getParentFile(), "GrooveCoverage_TheEnd.mp3");
        processBuilder.command(FFMPEG, "-i", inputFile.getAbsolutePath(), "-vn", "-ar", "44100", "-ac", "2", "-ab",
                "192", "-f", "mp3", outputFile.getAbsolutePath());
        final Process ffmpeg = processBuilder.start();
        ffmpeg.waitFor();
        assertThat(outputFile.exists(), is(true));
        assertThat(outputFile.delete(), is(true));
    }

}
