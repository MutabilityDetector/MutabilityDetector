package org.mutabilitydetector.findbugs;

import static java.lang.String.format;
import static org.mutabilitydetector.MutabilityReason.CANNOT_ANALYSE;
import static org.mutabilitydetector.MutabilityReason.NOT_DECLARED_FINAL;
import static org.mutabilitydetector.MutabilityReason.NULL_REASON;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
            
            String description = format("@Immutable class is actually mutable (%s)", humanReadableReasonCode(reason));
            content.append("<BugPattern type=\"MUTDEC_" + reason.name() + "\">\n")
            .append("<ShortDescription>" + description + "</ShortDescription>\n")
            .append("<LongDescription>"+ description +"</LongDescription>\n");
            
            writeDetailsNode(content, reason)
            .append("</BugPattern>\n")
            .append("\n");
        }
        
        content.append("<BugCode abbrev=\"MUTDEC\">Class annotated with @Immutable is actually mutable</BugCode>\n\n");
        
        content.append("</MessageCollection>\n");
        
        writeFile("src/main/resources/messages.xml", content);
    }

    private static String humanReadableReasonCode(MutabilityReason reason) {
        return reason.code().replace("_", " ").toLowerCase();
    }

    private static void writeFindbugsXml() throws Exception {
        StringBuilder content = new StringBuilder();
        
        
        List<MutabilityReason> includedReasons = includedReasons();
        
        content.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        
        content
        .append("<FindbugsPlugin xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
        		"pluginid=\"org.mutabilitydetector.findbugs\"\n" +
        		"website=\"http://www.mutabilitydetector.org\"\n" +
        		"defaultenabled=\"true\" >\n")
        .append("<Detector class=\"" + ThisPluginDetector.class.getName() + "\" speed=\"fast\"\n" +
        		"\treports=\"" + reasonsAsCsvList(includedReasons) + "\"" +
        		"/>\n\n")
        .append("<!-- Each bug pattern -->\n");
        
        
        for (MutabilityReason reason : includedReasons) {
            content.append("<BugPattern type=\"MUTDEC_" + reason.name() + "\" abbrev=\"MUTDEC\" category=\"CORRECTNESS\">\n");
            
            writeEmptyDetailsNode(content);
            
            content.append("</BugPattern>\n");
            
        }
        content.append("</FindbugsPlugin>\n");
        
        writeFile("src/main/resources/findbugs.xml", content);
    }

    private static String reasonsAsCsvList(List<MutabilityReason> includedReasons) {
		StringBuilder csvList = new StringBuilder();
		
		csvList.append("MUTDEC_" + includedReasons.get(0) + ",\n");
		
		for (MutabilityReason mutabilityReason : includedReasons.subList(1, includedReasons.size())) {
			csvList.append("\t\tMUTDEC_" + mutabilityReason + ",\n");
		}
		
		String csvString = csvList.toString();
		
		return csvString.substring(0, csvString.length() - ",\n".length());
	}

	private static List<MutabilityReason> includedReasons() {
    	List<MutabilityReason> includedReasons = new ArrayList<MutabilityReason>();
    	for (MutabilityReason reason : MutabilityReason.values()) {
            if (!isReasonToExclude(reason)) { 
            	includedReasons.add(reason);
            }
    	}
    	return includedReasons;
	}

	private static StringBuilder writeEmptyDetailsNode(StringBuilder content) {
        content.append("<Details>\n")
               .append("<![CDATA[]]>\n")
               .append("</Details>\n");
        
        return content;
    }

    private static StringBuilder writeDetailsNode(StringBuilder content, MutabilityReason reason) {
        content.append("<Details>\n")
        .append("<![CDATA[\n")
        .append(reason.description() + "\n")
        .append("]]>\n")
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
