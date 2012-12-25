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

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.xml.sax.SAXException;

/**
 * @author Juergen Fickel
 * @version 23.12.2012
 */
public final class FfmpegController implements Controller {

    @ThreadSafe
    private static final class FfprobeHelper {

        private final AudioStreamCodecHandler audioStreamCodecExtractor = AudioStreamCodecHandler.newInstance();

        private final String inputFilePath;

        public FfprobeHelper(final File inputFile) {
            Validate.notNull(inputFile);
            inputFilePath = inputFile.getAbsolutePath();
        }

        public List<Codec> getAudioStreamCodecsFor() {
            final InputStream ffprobeOutputAsXmlStream = getFfprobeOutputAsXmlStream();
            parseXmlStream(ffprobeOutputAsXmlStream);
            return audioStreamCodecExtractor.getStreamCodecs();
        }

        private InputStream getFfprobeOutputAsXmlStream() {
            final ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("ffprobe", "-v", "quiet", "-print_format", "xml", "-show_streams", "-i",
                    inputFilePath);
            final Process ffprobe = tryToStartFfprobe(processBuilder);
            tryToWaitForFfprobeToEnd(ffprobe);
            return ffprobe.getInputStream();
        }

        private static Process tryToStartFfprobe(final ProcessBuilder processBuilder) {
            try {
                return processBuilder.start();
            } catch (final IOException e) {
                throw illegalStateException("Unable to run ffprobe.", e);
            }
        }

        private static RuntimeException illegalStateException(final String message, final Exception cause) {
            final RuntimeException illegalStateException = new IllegalStateException(message);
            illegalStateException.initCause(cause);
            return illegalStateException;
        }

        private static void tryToWaitForFfprobeToEnd(final Process ffprobe) {
            try {
                ffprobe.waitFor();
            } catch (final InterruptedException e) {
                throw illegalStateException("Unable to wait for ffprobe to end.", e);
            }
        }

        private void parseXmlStream(final InputStream ffprobeOutputAsXmlStream) {
            final SAXParser saxParser = tryToCreateNewSaxParser(SAXParserFactory.newInstance());
            tryToParseFfprobeOutputAsXmlStream(saxParser, ffprobeOutputAsXmlStream);
        }

        private static SAXParser tryToCreateNewSaxParser(final SAXParserFactory saxParserFactory) {
            try {
                return saxParserFactory.newSAXParser();
            } catch (final ParserConfigurationException e) {
                throw illegalStateException("Unable to configure SAX parser", e);
            } catch (final SAXException e) {
                throw illegalStateException("Unable to create SAX parser", e);
            }
        }

        private void tryToParseFfprobeOutputAsXmlStream(final SAXParser saxParser,
                final InputStream ffprobeOutputAsXmlStream) {
            try {
                parseFfprobeOutputAsXmlStream(saxParser, ffprobeOutputAsXmlStream);
            } catch (final SAXException e) {
                throw illegalStateException("Unable to parse XML output stream of ffprobe", e);
            } catch (final IOException e) {
                throw illegalStateException("Unable to parse XML output stream of ffprobe", e);
            }
        }

        private void parseFfprobeOutputAsXmlStream(final SAXParser saxParser,
                final InputStream ffprobeOutputAsXmlStream) throws SAXException, IOException {
            saxParser.parse(ffprobeOutputAsXmlStream, audioStreamCodecExtractor);
        }

    }

    private static final class FfmpegHelper {

        private final String inputFilePath;
        private final String outputFilePath;

        public FfmpegHelper(final File inputFile, final File outputDirectory) {
            Validate.notNull(inputFile);
            Validate.notNull(outputDirectory);
            inputFilePath = inputFile.getAbsolutePath();
            outputFilePath = getAppropriateOutputFile(inputFile, outputDirectory);
            Validate.isTrue(!new File(outputFilePath).exists(), "The output file '%s' already exists.", outputFilePath);
        }

        private static String getAppropriateOutputFile(final File inputFile, final File outputDirectory) {
            final String inputFileNameWithoutExtension = removeFileExtension(inputFile.getName());
            final String nameOfOutputMp3File = format("%s.mp3", inputFileNameWithoutExtension);
            final File outputFile = new File(outputDirectory, nameOfOutputMp3File);
            return outputFile.getAbsolutePath();
        }

        private static String removeFileExtension(final String fileNameWithExtension) {
            final int beginOfFileExtension = fileNameWithExtension.lastIndexOf('.');
            return fileNameWithExtension.substring(0, beginOfFileExtension);
        }

        public int extractAudioStreamAndEncodeAsMp3(final Quality quality) {
            final ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("ffmpeg", "-i", inputFilePath, "-vn", "-ar", "44100", "-ac", "2", "-ab",
                    quality.value(), "-f", "mp3", outputFilePath);
            final Process ffmpeg = tryToStartFfmpeg(processBuilder);
            tryToWaitForFfmpegToEnd(ffmpeg);
            return ffmpeg.exitValue();
        }

        private static Process tryToStartFfmpeg(final ProcessBuilder processBuilder) {
            try {
                return processBuilder.start();
            } catch (final IOException e) {
                throw illegalStateException("Unable to run ffmpeg.", e);
            }
        }

        private static RuntimeException illegalStateException(final String message, final Exception cause) {
            final RuntimeException illegalStateException = new IllegalStateException(message);
            illegalStateException.initCause(cause);
            return illegalStateException;
        }

        private static void tryToWaitForFfmpegToEnd(final Process ffmpeg) {
            try {
                ffmpeg.waitFor();
            } catch (final InterruptedException e) {
                throw illegalStateException("Unable to wait for ffmpeg to end.", e);
            }
        }

        public int copyMp3AudioStream() {
            final ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("ffmpeg", "-i", inputFilePath, "-vn", "-acodec", "copy", outputFilePath);
            final Process ffmpeg = tryToStartFfmpeg(processBuilder);
            tryToWaitForFfmpegToEnd(ffmpeg);
            return ffmpeg.exitValue();
        }

    }

    private final File inputFile;
    private final File outputDirectory;
    private final Quality quality;

    private FfmpegController(final File theInputFile, final Quality theQuality) {
        Validate.notNull(theInputFile);
        Validate.notNull(theQuality);
        inputFile = new File(theInputFile.getAbsolutePath());
        outputDirectory = inputFile.getParentFile();
        quality = theQuality;
        Validate.notNull(outputDirectory, "Unable to get output directory of '%s'.", inputFile.getName());
    }

    public static FfmpegController newInstance(final File inputFile, final Quality quality) {
        return new FfmpegController(inputFile, quality);
    }

    @Override
    public boolean isMp3AudioStream() {
        final FfprobeHelper ffprobeHelper = new FfprobeHelper(inputFile);
        final List<Codec> audioStreamCodecs = ffprobeHelper.getAudioStreamCodecsFor();
        final Codec firstCodec = audioStreamCodecs.get(0);
        return Codec.MP3 == firstCodec;
    }

    @Override
    public int extractAudioStreamAndEncodeAsMp3() {
        final FfmpegHelper helper = new FfmpegHelper(inputFile, outputDirectory);
        return helper.extractAudioStreamAndEncodeAsMp3(quality);
    }

    @Override
    public int copyMp3AudioStream() {
        final FfmpegHelper helper = new FfmpegHelper(inputFile, outputDirectory);
        return helper.copyMp3AudioStream();
    }

    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("inputFile", inputFile).append("outputDirectory", outputDirectory).append("quality", quality);
        return builder.toString();
    }

}
