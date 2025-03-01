package com.warewise.server.service;


import com.warewise.common.util.protocol.Protocol;
import com.warewise.server.server.Server;
import com.warewise.server.server.ServerConnection;

/**
 * A concrete ServiceHandler for reporting commands.
 */
public class ReportingServiceHandler extends ServiceHandler {

    public ReportingServiceHandler(Server server, ServerConnection connection) {
        super(server, connection);
    }

    @Override
    public void sendCommand(String command, String... params) {
        StringBuilder sb = new StringBuilder(command);
        for (String param : params) {
            sb.append(Protocol.SEPARATOR).append(param);
        }
        connection.sendMessage(sb.toString());
    }

    @Override
    public void handleDisconnect() {
        System.out.println("ReportingServiceHandler disconnecting...");
        server.removeClient(this);
    }

    @Override
    public void handleCommand(String command, String[] params) {
        switch (command) {
            case Protocol.GENERATE_REPORT:
                if (params.length >= 3) {
                    String reportType = params[0];
                    String startDate = params[1];
                    String endDate = params[2];
                    System.out.println("Generating report: " + reportType);
                    // Report generation logic would go here.
                    sendCommand(Protocol.GENERATE_REPORT, "Report " + reportType + " generated");
                } else {
                    sendCommand(Protocol.ERRORTAG, "Invalid parameters for GENERATE_REPORT");
                }
                break;
            case Protocol.REPORT_RESULT:
                if (params.length >= 1) {
                    String reportData = params[0];
                    System.out.println("Reporting result: " + reportData);
                    sendCommand(Protocol.REPORT_RESULT, "Report result processed");
                } else {
                    sendCommand(Protocol.ERRORTAG, "Invalid parameters for REPORT_RESULT");
                }
                break;
            default:
                sendCommand(Protocol.ERRORTAG, "Invalid command in ReportingServiceHandler");
                break;
        }
    }
}
