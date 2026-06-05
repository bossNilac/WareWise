package com.warewise.client.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class SimplePdfReportWriter {
    private static final int MAX_LINES_PER_PAGE = 42;
    private static final int MAX_CHARS_PER_LINE = 92;

    private SimplePdfReportWriter() {
    }

    public static void writeReport(Path path, String title, List<String> lines) throws IOException {
        List<String> reportLines = new ArrayList<>();
        reportLines.add("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        reportLines.add("");
        for (String line : lines) {
            reportLines.addAll(wrap(line == null ? "" : line));
        }

        List<List<String>> pages = paginate(reportLines);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        List<Integer> offsets = new ArrayList<>();

        write(out, "%PDF-1.4\n");
        offsets.add(out.size());
        write(out, "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");

        offsets.add(out.size());
        StringBuilder kids = new StringBuilder();
        for (int i = 0; i < pages.size(); i++) {
            kids.append(4 + (i * 2)).append(" 0 R ");
        }
        write(out, "2 0 obj\n<< /Type /Pages /Kids [").writeBytes(kids.toString().getBytes(StandardCharsets.ISO_8859_1));
        write(out, "] /Count " + pages.size() + " >>\nendobj\n");

        offsets.add(out.size());
        write(out, "3 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n");

        for (int i = 0; i < pages.size(); i++) {
            int pageObject = 4 + (i * 2);
            int contentObject = pageObject + 1;
            byte[] content = buildPageContent(title, pages.get(i), i + 1, pages.size());

            offsets.add(out.size());
            write(out, pageObject + " 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Resources << /Font << /F1 3 0 R >> >> /Contents " + contentObject + " 0 R >>\nendobj\n");

            offsets.add(out.size());
            write(out, contentObject + " 0 obj\n<< /Length " + content.length + " >>\nstream\n");
            out.write(content);
            write(out, "\nendstream\nendobj\n");
        }

        int xrefOffset = out.size();
        int objectCount = 3 + pages.size() * 2;
        write(out, "xref\n0 " + (objectCount + 1) + "\n");
        write(out, "0000000000 65535 f \n");
        for (Integer offset : offsets) {
            write(out, String.format("%010d 00000 n \n", offset));
        }
        write(out, "trailer\n<< /Size " + (objectCount + 1) + " /Root 1 0 R >>\nstartxref\n" + xrefOffset + "\n%%EOF\n");

        Files.write(path, out.toByteArray());
    }

    private static byte[] buildPageContent(String title, List<String> lines, int pageNumber, int pageCount) {
        StringBuilder content = new StringBuilder();
        content.append("BT\n/F1 18 Tf\n50 760 Td\n(").append(escape(title)).append(") Tj\n");
        content.append("/F1 10 Tf\n0 -26 Td\n");
        for (String line : lines) {
            content.append("(").append(escape(line)).append(") Tj\n0 -14 Td\n");
        }
        content.append("/F1 9 Tf\n0 -18 Td\n(Page ").append(pageNumber).append(" of ").append(pageCount).append(") Tj\nET");
        return content.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    private static List<List<String>> paginate(List<String> lines) {
        List<List<String>> pages = new ArrayList<>();
        List<String> page = new ArrayList<>();
        for (String line : lines) {
            if (page.size() == MAX_LINES_PER_PAGE) {
                pages.add(page);
                page = new ArrayList<>();
            }
            page.add(line);
        }
        if (!page.isEmpty()) {
            pages.add(page);
        }
        if (pages.isEmpty()) {
            pages.add(List.of(""));
        }
        return pages;
    }

    private static List<String> wrap(String line) {
        List<String> wrapped = new ArrayList<>();
        String remaining = line;
        while (remaining.length() > MAX_CHARS_PER_LINE) {
            int split = remaining.lastIndexOf(' ', MAX_CHARS_PER_LINE);
            if (split < 1) {
                split = MAX_CHARS_PER_LINE;
            }
            wrapped.add(remaining.substring(0, split).trim());
            remaining = remaining.substring(split).trim();
        }
        wrapped.add(remaining);
        return wrapped;
    }

    private static String escape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replaceAll("[^\\x20-\\x7E]", "?");
    }

    private static ByteArrayOutputStream write(ByteArrayOutputStream out, String text) throws IOException {
        out.write(text.getBytes(StandardCharsets.ISO_8859_1));
        return out;
    }
}
