package io.jenkins.plugins.analysis.warnings;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

import org.junit.Test;

import io.jenkins.plugins.analysis.core.model.ReportScanningTool.ReportScanningToolDescriptor;
import io.jenkins.plugins.analysis.core.model.StaticAnalysisLabelProvider;
import io.jenkins.plugins.analysis.core.model.Tool;
import io.jenkins.plugins.analysis.core.model.Tool.ToolDescriptor;
import io.jenkins.plugins.analysis.core.testutil.IntegrationTestWithJenkinsPerSuite;

/**
 * Utility to create a report with all available tools.
 *
 * @author Ullrich Hafner
 */
public class ToolsLister extends IntegrationTestWithJenkinsPerSuite {
    /**
     * Creates the TOOLS.md file, that lists all registered tools.
     *
     * @throws IOException
     *         if hte file TOOLS.md cannot be written
     */
    @Test
    public void shouldPrintAllRegisteredTools() throws IOException {
        ArrayList<ToolDescriptor> descriptors = new ArrayList<>(
                getJenkins().getInstance().getDescriptorList(Tool.class));
        descriptors.sort(Comparator.comparing(d -> d.getLabelProvider().getName().toLowerCase(Locale.ENGLISH)));

        try (PrintWriter file = new PrintWriter("SUPPORTED-FORMATS.md", "UTF-8")) {
            file.printf("<!--- DO NOT EDIT - Generated by %s at %s-->%n", getClass().getSimpleName(), LocalDateTime.now());
            file.println("# Supported Report Formats\n"
                    + "\n"
                    + "Jenkins' Warnings Next Generation Plugin supports the following report formats. \n"
                    + "If your tool is supported, but has no custom icon yet, please file a pull request for the\n"
                    + "[Warnings Next Generation Plugin](https://github.com/jenkinsci/warnings-ng-plugin/pulls).\n"
                    + "\n"
                    + "If your tool is not yet supported you can\n"
                    + "1. define a new Groovy based parser in the user interface\n"
                    + "2. export the issues of your tool to the native XML format (or any other format)\n"
                    + "3. provide a parser within a new small plugin. \n"
                    + "\n"
                    + "If the parser is useful for \n"
                    + "other teams as well please share it and provide pull requests for the \n"
                    + "[Warnings Next Generation Plug-in](https://github.com/jenkinsci/warnings-ng-plugin/pulls) and \n"
                    + "the [Analysis Parsers Library](https://github.com/jenkinsci/analysis-model/). \n");
            file.print("| Number | ID | Symbol | Icons | Name | Default Pattern |\n");
            file.print("| --- | --- | --- | --- | --- | --- |\n");

            for (int i = 0; i < descriptors.size(); i++) {
                ToolDescriptor descriptor = descriptors.get(i);
                StaticAnalysisLabelProvider labelProvider = descriptor.getLabelProvider();
                file.printf("| %d | %s | %s() | %s | %s | %s |%n",
                        i,
                        descriptor.getId(),
                        descriptor.getSymbolName(),
                        getIcon(labelProvider, labelProvider.getSmallIconUrl())
                                + " " + getIcon(labelProvider, labelProvider.getLargeIconUrl()),
                        getName(descriptor),
                        descriptor instanceof ReportScanningToolDescriptor ? ((ReportScanningToolDescriptor)descriptor).getPattern() : "-");
            }
        }
    }

    private String getIcon(final StaticAnalysisLabelProvider labelProvider, final String icon) {
        if (icon.matches(".*analysis-\\d\\dx\\d\\d.png")) {
            return "-";
        }
        return String.format("![%s](%s)", labelProvider.getName(),
                icon.replace("/plugin/warnings-ng/", "src/main/webapp/"));
    }

    private String getName(final ToolDescriptor descriptor) {
        String name = descriptor.getLabelProvider().getName();
        String url = descriptor.getUrl();
        if (url.isEmpty()) {
            return name;
        }
        return String.format("[%s](%s)", name, url);
    }
}
