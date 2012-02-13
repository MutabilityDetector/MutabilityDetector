package org.mutabilitydetector.findbugs;

import static org.mutabilitydetector.MutabilityReason.CANNOT_ANALYSE;
import static org.mutabilitydetector.MutabilityReason.NOT_DECLARED_FINAL;
import static org.mutabilitydetector.MutabilityReason.NULL_REASON;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.mutabilitydetector.MutabilityReason;

public class WriteFindBugsConfigFiles {

    
    public static void main(String[] args) throws Exception {
        writeMessagesXml();
        writeFindbugsXml();
    }

    private static void writeMessagesXml() throws Exception {
        StringBuilder content = new StringBuilder();
        
        content.append("<MessageCollection>\n\n")
        .append("<Plugin>\n")
        .append("<ShortDescription>MutabilityDetector4FindBugs plugin</ShortDescription>\n")
        .append("<Details>\n")
        .append("<![CDATA[\n")
        .append("<p>This plugin detects classes annotated with @Immutable that are actually mutable</p>\n")
        .append("]]>\n")
        .append("</Details>\n")
        .append("</Plugin>\n\n\n")
        
        .append("<Detector class=\"" + ThisPluginDetector.class.getName() + "\">\n")
        .append("<Details>\n")
        .append("<![CDATA[\n")
        .append("<p>This plugin detects classes annotated with @Immutable that are actually mutable</p>\n")
        .append("]]>\n")
        .append("</Details>\n")
        .append("</Detector>\n")
        
        .append("\n<!-- Each of the individual reasons for mutability -->\n");
        
        for (MutabilityReason reason : MutabilityReason.values()) {
            if (isReasonToExclude(reason)) { continue; }
            
            content.append("<BugPattern type=\"MUTDEC_" + reason.name() + "\">\n")
            .append("<ShortDescription>@Immutable class is actually mutable</ShortDescription>\n")
            .append("<LongDescription>\n")
            
            .append(reason.description() + "\n")
            
            .append("</LongDescription>\n");
            
            writeEmptyDetailsNode(content)
            .append("</BugPattern>\n")
            .append("\n");
        }
        
        content.append("</MessageCollection>\n");
        
        writeFile("src/main/resources/messages.xml", content);
    }

    private static void writeFindbugsXml() throws Exception {
        StringBuilder content = new StringBuilder();
        
        content.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        .append("<FindbugsPlugin xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" defaultenabled=\"true\" >\n")
        .append("<Detector class=\"" + ThisPluginDetector.class.getName() + "\" speed=\"fast\" />\n\n")
        .append("<!-- Each bug pattern -->\n");
        
        for (MutabilityReason reason : MutabilityReason.values()) {
            if (isReasonToExclude(reason)) { continue; }
            
            content.append("<BugPattern type=\"MUTDEC_" + reason.name() + "\" abbrev=\"MUTDEC\" category=\"CORRECTNESS\">\n");
            
            writeEmptyDetailsNode(content);
            
            content.append("</BugPattern>\n");
            
        }

        content.append("</FindbugsPlugin>\n");
        
        writeFile("src/main/resources/findbugs.xml", content);
    }

    private static StringBuilder writeEmptyDetailsNode(StringBuilder content) {
        content.append("<Details>\n")
               .append("<![CDATA[]]>\n")
               .append("</Details>\n");
        
        return content;
    }
    
    @SuppressWarnings("deprecation")
    private static boolean isReasonToExclude(MutabilityReason reason) {
        return reason.isOneOf(NULL_REASON, CANNOT_ANALYSE, NOT_DECLARED_FINAL);
    }

    private static void writeFile(String fileName, StringBuilder content) throws IOException {
        System.out.println(content);
        new BufferedWriter(new FileWriter(fileName)).append(content).close();
    }
    
}
